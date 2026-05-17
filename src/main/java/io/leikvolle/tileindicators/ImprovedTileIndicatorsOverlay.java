package io.leikvolle.tileindicators;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

public class ImprovedTileIndicatorsOverlay extends Overlay
{
	private static final Color DESTINATION_TILE_SHADOW_COLOR = new Color(0x8D000000, true);

	private final Client client;
	private final ImprovedTileIndicatorsConfig config;

	@Inject
	private ImprovedTileIndicatorsPlugin plugin;

	@Inject
	private PlayerTileMetronomeController playerTileMetronomeController;

	private final BufferedImage ARROW_ICON;

	private LocalPoint lastDestination;
	private LocalPoint lastlastDestination;
	private int spawnGameCycle;
	private int despawnGameCycle;

	@Inject
	private ImprovedTileIndicatorsOverlay(Client client, ImprovedTileIndicatorsConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(0.6f);

		ARROW_ICON = ImageUtil.loadImageResource(ImprovedTileIndicatorsPlugin.class, "arrow.png");
	}

    @Override
    public Dimension render(Graphics2D graphics)
    {
		final Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) return null;

        final LocalPoint playerPosLocal = localPlayer.getLocalLocation();
        if (playerPosLocal == null) return null;

        if (config.customDestinationTile())
        {
            if (lastDestination != null && !lastDestination.equals(client.getLocalDestinationLocation()))
            {
                lastlastDestination = lastDestination;
                despawnGameCycle = client.getGameCycle();
            }

            if (lastDestination == null || !lastDestination.equals(client.getLocalDestinationLocation()))
            {
                if (client.getLocalDestinationLocation() != null) spawnGameCycle = client.getGameCycle();
                lastDestination = client.getLocalDestinationLocation();
            }

            switch (config.highlightDestinationStyle())
            {
                case RS3:
                    renderRS3Tile(graphics, lastDestination, config.highlightDestinationColor(), true, true);
                    renderRS3Tile(graphics, lastlastDestination, config.highlightDestinationColor(), false, false);
                    break;
                case RS3_NO_ARROW:
                    renderRS3Tile(graphics, lastDestination, config.highlightDestinationColor(), false, true);
                    renderRS3Tile(graphics, lastlastDestination, config.highlightDestinationColor(), false, false);
                    break;
            }
        }

        if (config.enablePlayerTileMetronome()) renderPlayerMetronomeTile(graphics, playerPosLocal);

        if (config.overlaysBelowPlayer() && client.isGpu()) removePlayer(graphics, localPlayer);

        if (config.overlaysBelowNPCs() && client.isGpu())
        {
            client.getTopLevelWorldView().npcs().stream()
                .filter(npc -> npc.getLocalLocation() != null)
                .filter(npc -> !plugin.isExcludedNpc(npc))
                .sorted(Comparator.comparingInt(npc -> npc.getLocalLocation().distanceTo(playerPosLocal)))
                .limit(config.maxNPCsDrawn())
                .forEach(npc -> removeNpc(graphics, npc));
        }
        return null;
    }

	private void renderPlayerMetronomeTile(Graphics2D graphics, LocalPoint playerPosLocal)
	{
		final Color tileColor = playerTileMetronomeController.getCurrentPlayerMetronomeColor();

		if (tileColor == null) return;

		renderPlayerMetronomeSquare(graphics, playerPosLocal, tileColor);
	}

	private void renderPlayerMetronomeSquare(Graphics2D graphics, LocalPoint playerPosLocal, Color tileColor)
	{
		final Polygon poly = Perspective.getCanvasTilePoly(client, playerPosLocal);

		if (poly == null) return;

		final Stroke borderStroke = new BasicStroke((float) config.playerTrueTileBorderWidth());
		final Color fillColor = getPlayerTrueTileFillColor(tileColor);

        if (config.playerTrueTileCornersOnly())
        {
            renderPolygonCorners(graphics, poly, tileColor, fillColor, borderStroke, config.playerTrueTileCornerSize());
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, tileColor, fillColor, borderStroke);
	}

	private Color getPlayerTrueTileFillColor(Color tileColor)
	{
		final Color configuredFillColor = config.playerTrueTileFillColor();

		if (!config.syncPlayerTrueTileFillColor()) return configuredFillColor;

		return new Color(tileColor.getRed(), tileColor.getGreen(), tileColor.getBlue(), configuredFillColor.getAlpha());
	}

	private static void renderPolygonCorners(Graphics2D graphics, Polygon poly, Color color, Color fillColor, Stroke borderStroke, int divisor)
	{
		graphics.setColor(color);
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(borderStroke);

		for (int i = 0; i < poly.npoints; i++)
		{
			final int pointX = poly.xpoints[i];
			final int pointY = poly.ypoints[i];
			final int previousIndex = (i - 1) < 0 ? poly.npoints - 1 : i - 1;
			final int nextIndex = (i + 1) > poly.npoints - 1 ? 0 : i + 1;

			final int nextX = (poly.xpoints[nextIndex] - pointX) / divisor + pointX;
			final int nextY = (poly.ypoints[nextIndex] - pointY) / divisor + pointY;
			final int previousX = (poly.xpoints[previousIndex] - pointX) / divisor + pointX;
			final int previousY = (poly.ypoints[previousIndex] - pointY) / divisor + pointY;

			graphics.drawLine(pointX, pointY, nextX, nextY);
			graphics.drawLine(pointX, pointY, previousX, previousY);
		}

		graphics.setColor(fillColor);
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}

    private void renderRS3Tile(final Graphics2D graphics, final LocalPoint dest, final Color color, boolean drawArrow, boolean appearing)
    {
        if (dest == null) return;

		final int plane = client.getTopLevelWorldView().getPlane();

 		final double size = appearing
            ? 0.65 * (Math.min(7.0, client.getGameCycle() - spawnGameCycle) / 7.0)
            : 0.65 * ((7 - (client.getGameCycle() - despawnGameCycle)) / 7.0);

        if (size < 0) return;

        final Polygon poly = getCanvasTargetTileCirclePoly(client, dest, size, plane, 10);
        final Polygon shadow = getCanvasTargetTileCirclePoly(client, dest, size, plane, 0);
        final Point canvasLoc = Perspective.getCanvasImageLocation(client, dest, ARROW_ICON, 150 + (int) (20 * Math.sin(client.getGameCycle() / 10.0)));

        if (poly != null)
        {
            final Stroke originalStroke = graphics.getStroke();
            graphics.setStroke(new BasicStroke((float) config.destinationTileBorderWidth()));
            graphics.setColor(DESTINATION_TILE_SHADOW_COLOR);
            graphics.draw(shadow);
            graphics.setColor(color);
            graphics.draw(poly);
            graphics.setStroke(originalStroke);
        }

        if (canvasLoc != null && drawArrow && shadow != null)
        {
            // TODO: improve scale as you zoom out
            final double imageScale = 0.8 * Math.min(client.get3dZoom() / 500.0, 1);
            graphics.drawImage(ARROW_ICON, (int) (shadow.getBounds().width / 2 + shadow.getBounds().x - ARROW_ICON.getWidth() * imageScale / 2), canvasLoc.getY(), (int) (ARROW_ICON.getWidth() * imageScale), (int) (ARROW_ICON.getHeight() * imageScale), null);
        }
    }

    public static Polygon getCanvasTargetTileCirclePoly(
        @Nonnull Client client,
        @Nonnull LocalPoint localLocation,
        double size,
        int plane,
        int zOffset)
    {
        final int sceneX = localLocation.getSceneX();
        final int sceneY = localLocation.getSceneY();

        if (sceneX < 0 || sceneY < 0 || sceneX >= Perspective.SCENE_SIZE || sceneY >= Perspective.SCENE_SIZE) return null;

        final Polygon poly = new Polygon();
        final int resolution = 64;
        final int height = Perspective.getTileHeight(client, localLocation, plane) - zOffset;

        for (int i = 0; i < resolution; i++)
        {
            final double angle = (double) i / resolution * 2 * Math.PI;
            final double offsetX = Math.cos(angle);
            final double offsetY = Math.sin(angle);
            final int x = (int) (localLocation.getX() + offsetX * Perspective.LOCAL_TILE_SIZE * size);
            final int y = (int) (localLocation.getY() + offsetY * Perspective.LOCAL_TILE_SIZE * size);
            final Point p = Perspective.localToCanvas(client, x, y, height);
            if (p == null) continue;
            poly.addPoint(p.getX(), p.getY());
        }

        return poly;
    }

    private void removePlayer(final Graphics2D graphics, final Player player)
    {
        final int localZ = Perspective.getFootprintTileHeight(client, player.getLocalLocation(), player.getWorldView().getPlane(), player.getFootprintSize()) - player.getAnimationHeightOffset();
        removeActor(graphics, player, localZ);
    }

    private void removeNpc(final Graphics2D graphics, final NPC npc)
    {
        final int localZ = Perspective.getFootprintTileHeight(client, npc.getLocalLocation(), npc.getWorldView().getPlane(), npc.getComposition().getFootprintSize()) - npc.getAnimationHeightOffset();
        removeActor(graphics, npc, localZ);
    }

    private void removeActor(final Graphics2D graphics, final Actor actor, final int localZ)
    {
        final int clipX1 = client.getViewportXOffset();
        final int clipY1 = client.getViewportYOffset();
        final int clipX2 = client.getViewportWidth() + clipX1;
        final int clipY2 = client.getViewportHeight() + clipY1;
        final Object origAA = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        final Model model = actor.getModel();
        if (model == null)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origAA);
            return;
        }

        final int vCount = model.getVerticesCount();
        final float[] x3d = model.getVerticesX();
        final float[] y3d = model.getVerticesY();
        final float[] z3d = model.getVerticesZ();
        final int[] x2d = new int[vCount];
        final int[] y2d = new int[vCount];
        final LocalPoint lp = actor.getLocalLocation();
        final int localX = lp.getX();
        final int localY = lp.getY();
        final int rotation = actor.getCurrentOrientation();

        Perspective.modelToCanvas(client, actor.getWorldView(), vCount, localX, localY, localZ, rotation, x3d, z3d, y3d, x2d, y2d);

        boolean anyVisible = false;
        for (int i = 0; i < vCount; i++)
        {
            final int x = x2d[i];
            final int y = y2d[i];
            final boolean visibleX = x >= clipX1 && x < clipX2;
            final boolean visibleY = y >= clipY1 && y < clipY2;
            anyVisible |= visibleX && visibleY;
        }

        if (!anyVisible) return;

        final int tCount = model.getFaceCount();
        final int[] tx = model.getFaceIndices1();
        final int[] ty = model.getFaceIndices2();
        final int[] tz = model.getFaceIndices3();
        final byte[] triangleTransparencies = model.getFaceTransparencies();
        final Composite originalComposite = graphics.getComposite();

        graphics.setComposite(AlphaComposite.Clear);
        graphics.setColor(Color.WHITE);
        for (int i = 0; i < tCount; i++)
        {
            if (getTriDirection(x2d[tx[i]], y2d[tx[i]], x2d[ty[i]], y2d[ty[i]], x2d[tz[i]], y2d[tz[i]]) >= 0) continue;
            if (triangleTransparencies == null || (triangleTransparencies[i] & 255) < 254)
            {
                final Polygon polygon = new Polygon(
                    new int[] {x2d[tx[i]], x2d[ty[i]], x2d[tz[i]]},
                    new int[] {y2d[tx[i]], y2d[ty[i]], y2d[tz[i]]},
                    3);
                graphics.fill(polygon);
            }
        }

        graphics.setComposite(originalComposite);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origAA);
    }

    private int getTriDirection(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        final int x4 = x2 - x1;
        final int y4 = y2 - y1;
        final int x5 = x3 - x1;
        final int y5 = y3 - y1;
        return x4 * y5 - y4 * x5;
    }

}
