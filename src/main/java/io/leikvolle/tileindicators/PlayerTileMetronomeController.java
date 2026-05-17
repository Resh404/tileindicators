package io.leikvolle.tileindicators;

import java.awt.Color;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;

@Singleton
public class PlayerTileMetronomeController
{
	private final ImprovedTileIndicatorsConfig config;
	private final Client client;

	@Getter
	private int playerMetronomeTick;

	@Getter
	private int playerMetronomeColorIndex;

	@Getter
	private Color currentPlayerMetronomeColor = Color.WHITE;

	@Getter
	private int playerTickCounter1;

	@Getter
	private int playerTickCounter2;

	@Inject
	PlayerTileMetronomeController(ImprovedTileIndicatorsConfig config, Client client)
	{
		this.config = config;
		this.client = client;
		resetState();
	}

	void onGameTick()
	{
		if (config == null || client.getGameState() != GameState.LOGGED_IN) return;

		if (!hasAnyActiveFeature())
		{
			resetState();
			return;
		}

		advanceMetronomeState();

		playerTickCounter1 = config.showPlayerTileTickCounter1()
			? advanceCounter(playerTickCounter1, config.playerTileTickCounter1CycleLength())
			: 1;

		playerTickCounter2 = config.showPlayerTileTickCounter2()
			? advanceCounter(playerTickCounter2, config.playerTileTickCounter2CycleLength())
			: 1;
	}

	void onConfigChanged()
	{
		if (!hasAnyActiveFeature())
		{
			resetState();
			return;
		}

		normalizeMetronomeState();

		playerTickCounter1 = config.showPlayerTileTickCounter1()
			? normalizeCounter(playerTickCounter1, config.playerTileTickCounter1CycleLength())
			: 1;

		playerTickCounter2 = config.showPlayerTileTickCounter2()
			? normalizeCounter(playerTickCounter2, config.playerTileTickCounter2CycleLength())
			: 1;
	}

	void resetSyncState()
	{
		resetState();
	}

	boolean isPlayerTileMetronomeEnabled()
	{
		return config.enablePlayerTileMetronome();
	}

	Color getTickCounter1Color()
	{
		return config.syncTickCounter1ColorWithTile()
			? currentPlayerMetronomeColor
			: config.playerTileTickCounter1Color();
	}

	Color getTickCounter2Color()
	{
		return config.syncTickCounter2ColorWithTile()
			? currentPlayerMetronomeColor
			: config.playerTileTickCounter2Color();
	}

	private boolean hasAnyActiveFeature()
	{
		return config.enablePlayerTileMetronome()
			|| config.showPlayerTileTickCounter1()
			|| config.showPlayerTileTickCounter2();
	}

	private boolean shouldRunColorCycle()
	{
		return config.enablePlayerTileMetronome()
			|| (config.showPlayerTileTickCounter1() && config.syncTickCounter1ColorWithTile())
			|| (config.showPlayerTileTickCounter2() && config.syncTickCounter2ColorWithTile());
	}

	private void resetState()
	{
		resetMetronomeState();
		playerTickCounter1 = 1;
		playerTickCounter2 = 1;
	}

	private void resetMetronomeState()
	{
		playerMetronomeTick = 1;
		playerMetronomeColorIndex = 1;
		currentPlayerMetronomeColor = resolveMetronomeColor(1);
	}

	private void advanceMetronomeState()
	{
		if (!shouldRunColorCycle())
		{
			resetMetronomeState();
			return;
		}

		setMetronomeState(advanceCounter(playerMetronomeTick, config.playerMetronomeCycleLength()));
	}

	private void normalizeMetronomeState()
	{
		if (!shouldRunColorCycle())
		{
			resetMetronomeState();
			return;
		}

		setMetronomeState(normalizeCounter(playerMetronomeTick, config.playerMetronomeCycleLength()));
	}

	private void setMetronomeState(int tick)
	{
		playerMetronomeTick = tick;
		playerMetronomeColorIndex = tick;
		currentPlayerMetronomeColor = resolveMetronomeColor(tick);
	}

	private int advanceCounter(int currentValue, int cycleLength)
	{
		return currentValue >= cycleLength ? 1 : currentValue + 1;
	}

	private int normalizeCounter(int currentValue, int cycleLength)
	{
		if (currentValue < 1 || currentValue > cycleLength) return 1;

		return currentValue;
	}

	private Color resolveMetronomeColor(int colorIndex)
	{
		switch (colorIndex)
		{
			case 1:
				return config.playerMetronomeColor1();
			case 2:
				return config.playerMetronomeColor2();
			case 3:
				return config.playerMetronomeColor3();
			case 4:
				return config.playerMetronomeColor4();
			case 5:
				return config.playerMetronomeColor5();
			case 6:
				return config.playerMetronomeColor6();
			case 7:
				return config.playerMetronomeColor7();
			case 8:
				return config.playerMetronomeColor8();
			case 9:
				return config.playerMetronomeColor9();
			case 10:
				return config.playerMetronomeColor10();
			default:
				return config.playerMetronomeColor1();
		}
	}
}
