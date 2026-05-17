package io.leikvolle.tileindicators;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import java.awt.event.KeyEvent;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import java.util.*;

@PluginDescriptor(
		name = "Tile Overlay Indicators",
		configName = "tileoverlayindicatorsplugin",
		description = "Tile overlay for NPCs, and players.",
		tags = {"overlay", "tile", "indicator", "highlight", "draw", "color", "npc", "tick", "metronome", "counter"},
		conflicts = {"Improved Tile Indicators"}
)
public class ImprovedTileIndicatorsPlugin extends Plugin
implements KeyListener
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ImprovedTileIndicatorsOverlay overlay;

	@Inject
	private PlayerTileTickCounterOverlay playerTileTickCounterOverlay;

	@Inject
	private PlayerTileMetronomeController playerTileMetronomeController;

	@Inject ImprovedTileIndicatorsConfig config;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	@Getter(AccessLevel.PACKAGE)
	private final Set<NPC> onTopNpcs = new HashSet<>();
	private List<String> onTopNPCNames = new ArrayList<>();
	private List<String> excludedNPCNames = new ArrayList<>();
	private final Set<Integer> excludedNPCIds = new HashSet<>();

	private static final String DRAW_ABOVE = "Draw-Above";
	private static final String DRAW_BELOW = "Draw-Below";
	@Provides
	ImprovedTileIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImprovedTileIndicatorsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(playerTileTickCounterOverlay);
		keyManager.registerKeyListener(this);
		clientThread.invoke(this::rebuild);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(playerTileTickCounterOverlay);
		keyManager.unregisterKeyListener(this);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		playerTileMetronomeController.onGameTick();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (config.playerMetronomeSyncHotkey().matches(e)) playerTileMetronomeController.resetSyncState();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) onTopNpcs.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("tileoverlayindicators")) return;

		if (shouldResetMetronomeSync(configChanged.getKey())) playerTileMetronomeController.resetSyncState();

		playerTileMetronomeController.onConfigChanged();
		clientThread.invoke(this::rebuild);
	}

	private boolean shouldResetMetronomeSync(String keyName)
	{
		if ("enablePlayerTileMetronome".equals(keyName)) return config.enablePlayerTileMetronome();

		if ("syncPlayerTrueTileFillColor".equals(keyName)) return config.syncPlayerTrueTileFillColor();

		if ("syncTickCounter1ColorWithTile".equals(keyName)) return config.syncTickCounter1ColorWithTile();

		if ("syncTickCounter2ColorWithTile".equals(keyName)) return config.syncTickCounter2ColorWithTile();

		return false;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null) return;

		if (excludedMatchesNPC(npc)) return;

		if (onTopMatchesNPCName(npcName)) onTopNpcs.add(npc);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		onTopNpcs.remove(npc);
	}


	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int type = event.getType();

		if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET) type -= MENU_ACTION_DEPRIORITIZE_OFFSET;

		final MenuAction menuAction = MenuAction.of(type);

		if (menuAction == MenuAction.EXAMINE_NPC && client.isKeyPressed(KeyCode.KC_SHIFT) && config.overlaysBelowNPCs())
		{
			final NPC npc = client.getTopLevelWorldView().npcs().byIndex(event.getIdentifier());
			if (npc == null) return;
			final String npcName = getNameForCachedNPC(event.getIdentifier());
			if (npcName == null) return;
			if (excludedMatchesNPC(npc)) return;
			boolean matchesList = onTopNPCNames.stream()
					.filter(highlight -> !highlight.equalsIgnoreCase(npcName))
					.anyMatch(highlight -> WildcardMatcher.matches(highlight, npcName));

			// Only show draw options to npcs not affected by a wildcard entry, as wildcards will not be removed by menu options
			if (!matchesList)
			{
				client.getMenu().createMenuEntry(-1)
					.setOption(onTopNPCNames.stream().anyMatch(npcName::equalsIgnoreCase) ? DRAW_BELOW : DRAW_ABOVE)
					.setTarget(event.getTarget())
					.setIdentifier(event.getIdentifier())
					.setType(MenuAction.RUNELITE)
					.onClick(this::toggleDraw);
			}
		}
	}

	public void toggleDraw(MenuEntry click)
	{
		final String name = getNameForCachedNPC(click.getIdentifier());
		if (name == null) return;
		// this trips a config change which triggers the overlay rebuild
		updateNpcsToDrawAbove(name);
	}

	private void updateNpcsToDrawAbove(String npc)
	{
		final List<String> highlightedNpcs = new ArrayList<>(onTopNPCNames);

		if (!highlightedNpcs.removeIf(npc::equalsIgnoreCase)) highlightedNpcs.add(npc);

		// this triggers the config change event and rebuilds npcs
		config.setTopNPCs(Text.toCSV(highlightedNpcs));
	}

	List<String> getTopNPCs()
	{
		final String configNpcs = config.getTopNPCs();

		if (configNpcs.isEmpty()) return Collections.emptyList();

		return Text.fromCSV(configNpcs);
	}

	List<String> getExcludedNPCs()
	{
		final String configNpcs = config.getExcludedNPCs();

		if (configNpcs.isEmpty()) return Collections.emptyList();

		return Text.fromCSV(configNpcs);
	}

	void rebuild()
	{
		onTopNPCNames = getTopNPCs();
		excludedNPCNames = getExcludedNPCs();
		excludedNPCIds.clear();
		onTopNpcs.clear();

		if (client.getGameState() != GameState.LOGGED_IN && client.getGameState() != GameState.LOADING) return;

		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			final String npcName = npc.getName();

			if (npcName == null) continue;

			if (excludedMatchesNPC(npc)) continue;

			if (onTopMatchesNPCName(npcName)) onTopNpcs.add(npc);
		}
	}

	private boolean onTopMatchesNPCName(String npcName)
	{
		for (String matching : onTopNPCNames)
		{
			if (WildcardMatcher.matches(matching, npcName)) return true;
		}

		return false;
	}

	private boolean excludedMatchesNPC(NPC npc)
	{
		final String npcName = npc.getName();

		if (npcName != null && excludedMatchesNPCName(npcName))
		{
			excludedNPCIds.add(npc.getId());
			return true;
		}

		return excludedNPCIds.contains(npc.getId());
	}

	boolean isExcludedNpc(NPC npc)
	{
		return excludedMatchesNPC(npc);
	}

	private boolean excludedMatchesNPCName(String npcName)
	{
		for (String matching : excludedNPCNames)
		{
			final String pattern = matching.trim();

			if (!pattern.isEmpty() && WildcardMatcher.matches(pattern, npcName)) return true;
		}

		return false;
	}

	private String getNameForCachedNPC(int id)
	{
		final NPC npc = client.getTopLevelWorldView().npcs().byIndex(id);

		if (npc == null) return null;

		return npc.getName();
	}

}
