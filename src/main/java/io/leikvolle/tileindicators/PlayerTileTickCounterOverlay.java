package io.leikvolle.tileindicators;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class PlayerTileTickCounterOverlay extends Overlay
{
	private final Client client;
	private final ImprovedTileIndicatorsConfig config;
	private final PlayerTileMetronomeController controller;

	@Inject
	PlayerTileTickCounterOverlay(Client client, ImprovedTileIndicatorsConfig config, PlayerTileMetronomeController controller)
	{
		this.client = client;
		this.config = config;
		this.controller = controller;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Player localPlayer = client.getLocalPlayer();

		if (localPlayer == null || config == null || controller == null) return null;

		final Font originalFont = graphics.getFont();

		if (config.showPlayerTileTickCounter1())
		{
			renderTickCounter(
				graphics,
				localPlayer,
				String.valueOf(controller.getPlayerTickCounter1()),
				config.playerTileTicksPosition1(),
				config.playerTileTickXOffset1(),
				config.playerTileTickYOffset1(),
				controller.getTickCounter1Color(),
				config.playerTileTickCounter1FontType(),
				config.playerTileTickCounter1FontSize());
		}

		if (config.showPlayerTileTickCounter2())
		{
			renderTickCounter(
				graphics,
				localPlayer,
				String.valueOf(controller.getPlayerTickCounter2()),
				config.playerTileTicksPosition2(),
				config.playerTileTickXOffset2(),
				config.playerTileTickYOffset2(),
				controller.getTickCounter2Color(),
				config.playerTileTickCounter2FontType(),
				config.playerTileTickCounter2FontSize());
		}

		graphics.setFont(originalFont);

		return null;
	}

	private void renderTickCounter(
		Graphics2D graphics,
		Player player,
		String text,
		TickCounterPosition position,
		int xOffset,
		int yOffset,
		Color color,
		FontType fontType,
		int fontSize)
	{
		graphics.setFont(createCounterFont(fontType, fontSize));
		final Point basePoint = getBasePoint(graphics, player, text, position);

		if (basePoint == null) return;

		final Point renderPoint = new Point(basePoint.getX() + xOffset, basePoint.getY() + yOffset);
		OverlayUtil.renderTextLocation(graphics, renderPoint, text, color);
	}

	private Font createCounterFont(FontType fontType, int fontSize)
	{
		if (fontType == FontType.REGULAR) return FontManager.getRunescapeFont().deriveFont(Font.PLAIN, (float) fontSize);

		return new Font(fontType.toString(), Font.PLAIN, fontSize);
	}

	private Point getBasePoint(
		Graphics2D graphics,
		Player player,
		String text,
		TickCounterPosition position)
	{
		final LocalPoint localLocation = player.getLocalLocation();
		final int plane = player.getWorldView().getPlane();

		if (localLocation == null) return null;

		switch (position)
		{
			case TOP:
				return Perspective.localToCanvas(client, localLocation, plane, 214);

			case CENTERED:
				return Perspective.localToCanvas(client, localLocation, plane, 100);

			case BOTTOM:
				return player.getCanvasTextLocation(graphics, text, 10);

			case DEFAULT:
			default:
				final int height = player.getLogicalHeight() + 20;
				return Perspective.localToCanvas(client, localLocation, plane, height);
		}
	}
}
