package io.leikvolle.tileindicators;

import java.awt.Color;

import net.runelite.client.config.*;

@ConfigGroup("tileoverlayindicators")
public interface ImprovedTileIndicatorsConfig extends Config
{

	@ConfigSection(
			name = "Player Tile indicators",
			description = "Settings replacing the normal tile indicators plugin",
			position = 0
	)
	String tileIndicatorsSection = "tileIndicatorsSection";

	@ConfigItem(
			keyName = "currentTileBelowPlayer",
			name = "Draw overlays below player",
			description = "Requires GPU. Draws overlays below the player",
			section = tileIndicatorsSection,
			position = 1
	)
	default boolean overlaysBelowPlayer()
	{
		return true;
	}

	@ConfigSection(
			name = "Player True Tile Metronome",
			description = "Settings for cycling the player true tile color and showing player tick counters",
			position = 1
	)
	String playerTileMetronomeSection = "playerTileMetronomeSection";

	@ConfigItem(
			keyName = "enablePlayerTileMetronome",
			name = "Enable",
			description = "Cycles the player true tile color every game tick",
			section = playerTileMetronomeSection,
			position = 1
	)
	default boolean enablePlayerTileMetronome()
	{
		return false;
	}

	@Range(
			min = 2,
			max = 10
	)
	@ConfigItem(
			keyName = "playerMetronomeCycleLength",
			name = "Cycle length",
			description = "Number of colors used by the player true tile metronome",
			section = playerTileMetronomeSection,
			position = 2
	)
	default int playerMetronomeCycleLength()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
			keyName = "playerTrueTileFillColor",
			name = "Fill color",
			description = "Fill color used for the player true tile overlay",
			section = playerTileMetronomeSection,
			position = 3
	)
	default Color playerTrueTileFillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@ConfigItem(
			keyName = "syncPlayerTrueTileFillColor",
			name = "Sync fill color with metronome",
			description = "Uses the current metronome color for the tile fill while keeping the configured fill opacity",
			section = playerTileMetronomeSection,
			position = 4
	)
	default boolean syncPlayerTrueTileFillColor()
	{
		return false;
	}

	@Range(
			min = 1,
			max = 8
	)
	@ConfigItem(
			keyName = "playerTrueTileBorderWidth",
			name = "Border width",
			description = "Border width used for the player true tile overlay",
			section = playerTileMetronomeSection,
			position = 5
	)
	default int playerTrueTileBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "playerTrueTileCornersOnly",
			name = "Corners only",
			description = "Draw only the corners of the player true tile overlay",
			section = playerTileMetronomeSection,
			position = 6
	)
	default boolean playerTrueTileCornersOnly()
	{
		return false;
	}

	@Range(
			min = 2,
			max = 8
	)
	@ConfigItem(
			keyName = "playerTrueTileCornerSize",
			name = "Corner size",
			description = "Length of the corner segments when corners only is enabled. Lower values make longer corners",
			section = playerTileMetronomeSection,
			position = 7
	)
	default int playerTrueTileCornerSize()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "playerMetronomeSyncHotkey",
			name = "Sync hotkey",
			description = "Resets the player true tile metronome color cycle and both counters so they sync together again",
			section = playerTileMetronomeSection,
			position = 8
	)
	default Keybind playerMetronomeSyncHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigSection(
			name = "Tick Counter 1",
			description = "Settings for the first player tick counter",
			position = 2
	)
	String playerTileTickCounter1Section = "playerTileTickCounter1Section";

	@ConfigItem(
			keyName = "showPlayerTileTickCounter1",
			name = "Show",
			description = "Shows the first tick counter",
			section = playerTileTickCounter1Section,
			position = 1
	)
	default boolean showPlayerTileTickCounter1()
	{
		return false;
	}

	@Range(
			min = 1,
			max = 10
	)
	@ConfigItem(
			keyName = "playerTileTickCounter1CycleLength",
			name = "Cycle length",
			description = "Number of ticks before the counter wraps back to 1",
			section = playerTileTickCounter1Section,
			position = 2
	)
	default int playerTileTickCounter1CycleLength()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "playerTileTickCounter1Color",
			name = "Color",
			description = "Color used by the tick counter when sync is disabled",
			section = playerTileTickCounter1Section,
			position = 3
	)
	default Color playerTileTickCounter1Color()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			keyName = "syncTickCounter1ColorWithTile",
			name = "Sync color with true tile",
			description = "Syncs tick counter color with true tile metronome color",
			section = playerTileTickCounter1Section,
			position = 4
	)
	default boolean syncTickCounter1ColorWithTile()
	{
		return false;
	}

	@ConfigItem(
			keyName = "playerTileTickCounter1FontType",
			name = "Font type",
			description = "Font used for the first tick counter",
			section = playerTileTickCounter1Section,
			position = 5
	)
	default FontType playerTileTickCounter1FontType()
	{
		return FontType.REGULAR;
	}

	@Range(
			min = 8,
			max = 32
	)
	@ConfigItem(
			keyName = "playerTileTickCounter1FontSize",
			name = "Font size",
			description = "Font size used for the first tick counter",
			section = playerTileTickCounter1Section,
			position = 6
	)
	default int playerTileTickCounter1FontSize()
	{
		return 16;
	}

	@ConfigItem(
			keyName = "playerTileTicksPosition1",
			name = "Counter position",
			description = "Base position of the tick counter",
			section = playerTileTickCounter1Section,
			position = 7
	)
	default TickCounterPosition playerTileTicksPosition1()
	{
		return TickCounterPosition.DEFAULT;
	}

	@Range(
			min = -100,
			max = 100
	)
	@ConfigItem(
			keyName = "playerTileTickXOffset1",
			name = "X offset",
			description = "Horizontal offset",
			section = playerTileTickCounter1Section,
			position = 8
	)
	default int playerTileTickXOffset1()
	{
		return 0;
	}

	@Range(
			min = -500,
			max = 500
	)
	@ConfigItem(
			keyName = "playerTileTickYOffset1",
			name = "Y offset",
			description = "Vertical offset",
			section = playerTileTickCounter1Section,
			position = 9
	)
	default int playerTileTickYOffset1()
	{
		return 0;
	}

	@ConfigSection(
			name = "Tick Counter 2",
			description = "Settings for the second tick counter",
			position = 3
	)
	String playerTileTickCounter2Section = "playerTileTickCounter2Section";

	@ConfigItem(
			keyName = "showPlayerTileTickCounter2",
			name = "Show",
			description = "Shows the second tick counter",
			section = playerTileTickCounter2Section,
			position = 1
	)
	default boolean showPlayerTileTickCounter2()
	{
		return false;
	}

	@Range(
			min = 1,
			max = 10
	)
	@ConfigItem(
			keyName = "playerTileTickCounter2CycleLength",
			name = "Cycle length",
			description = "Number of ticks before the counter wraps back to 1",
			section = playerTileTickCounter2Section,
			position = 2
	)
	default int playerTileTickCounter2CycleLength()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "playerTileTickCounter2Color",
			name = "Counter color",
			description = "Color used by the tick counter when sync is disabled",
			section = playerTileTickCounter2Section,
			position = 3
	)
	default Color playerTileTickCounter2Color()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
			keyName = "syncTickCounter2ColorWithTile",
			name = "Sync color with true tile",
			description = "Syncs tick counter color with true tile metronome color",
			section = playerTileTickCounter2Section,
			position = 4
	)
	default boolean syncTickCounter2ColorWithTile()
	{
		return false;
	}

	@ConfigItem(
			keyName = "playerTileTickCounter2FontType",
			name = "Font type",
			description = "Font used for the second tick counter",
			section = playerTileTickCounter2Section,
			position = 5
	)
	default FontType playerTileTickCounter2FontType()
	{
		return FontType.REGULAR;
	}

	@Range(
			min = 8,
			max = 32
	)
	@ConfigItem(
			keyName = "playerTileTickCounter2FontSize",
			name = "Font size",
			description = "Font size used for the second tick counter",
			section = playerTileTickCounter2Section,
			position = 6
	)
	default int playerTileTickCounter2FontSize()
	{
		return 16;
	}

	@ConfigItem(
			keyName = "playerTileTicksPosition2",
			name = "Counter position",
			description = "Base position of the second tick counter",
			section = playerTileTickCounter2Section,
			position = 7
	)
	default TickCounterPosition playerTileTicksPosition2()
	{
		return TickCounterPosition.TOP;
	}

	@Range(
			min = -100,
			max = 100
	)
	@ConfigItem(
			keyName = "playerTileTickXOffset2",
			name = "X offset",
			description = "Horizontal offset",
			section = playerTileTickCounter2Section,
			position = 8
	)
	default int playerTileTickXOffset2()
	{
		return 0;
	}

	@Range(
			min = -500,
			max = 500
	)
	@ConfigItem(
			keyName = "playerTileTickYOffset2",
			name = "Y offset",
			description = "Vertical offset",
			section = playerTileTickCounter2Section,
			position = 9
	)
	default int playerTileTickYOffset2()
	{
		return 0;
	}

	@ConfigSection(
			name = "Player Metronome Colors",
			description = "Colors used by the player true tile metronome",
			position = 4
	)
	String playerTileMetronomeColorsSection = "playerTileMetronomeColorsSection";

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor1",
			name = "1st tick color",
			description = "Color used for the first tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 1
	)
	default Color playerMetronomeColor1()
	{
		return new Color(0xFFFF233C, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor2",
			name = "2nd tick color",
			description = "Color used for the second tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 2
	)
	default Color playerMetronomeColor2()
	{
		return new Color(0xFF2BFF6B, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor3",
			name = "3rd tick color",
			description = "Color used for the third tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 3
	)
	default Color playerMetronomeColor3()
	{
		return new Color(0xFF589DFF, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor4",
			name = "4th tick color",
			description = "Color used for the fourth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 4
	)
	default Color playerMetronomeColor4()
	{
		return new Color(0xFFFFD639, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor5",
			name = "5th tick color",
			description = "Color used for the fifth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 5
	)
	default Color playerMetronomeColor5()
	{
		return new Color(0xFFFF00FF, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor6",
			name = "6th tick color",
			description = "Color used for the sixth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 6
	)
	default Color playerMetronomeColor6()
	{
		return new Color(0xFF00FFFF, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor7",
			name = "7th tick color",
			description = "Color used for the seventh tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 7
	)
	default Color playerMetronomeColor7()
	{
		return new Color(0x00070400, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor8",
			name = "8th tick color",
			description = "Color used for the eighth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 8
	)
	default Color playerMetronomeColor8()
	{
		return new Color(0xFF00BF16, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor9",
			name = "9th tick color",
			description = "Color used for the ninth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 9
	)
	default Color playerMetronomeColor9()
	{
		return new Color(0xFFFF695E, true);
	}

	@Alpha
	@ConfigItem(
			keyName = "playerMetronomeColor10",
			name = "10th tick color",
			description = "Color used for the tenth tick in the player true tile metronome",
			section = playerTileMetronomeColorsSection,
			position = 10
	)
	default Color playerMetronomeColor10()
	{
		return new Color(0xFFFF1100, true);
	}

	@ConfigSection(
			name = "Destination Tile",
			description = "Settings for modifying the destination tile",
			position = 5
	)
	String destinationTileSection = "destinationTileSection";

	@ConfigItem(
			keyName = "customDestinationTile",
			name = "Custom destination tile",
			description = "Enables the use of custom tile indicators on destination",
			section = destinationTileSection,
			position = 2
	)
	default boolean customDestinationTile() { return false;}

	@ConfigItem(
			keyName = "highlightDestinationStyle",
			name = "Destination Tile Style",
			description = "The style of the destination tile",
			section = destinationTileSection,
			position = 3
	)
	default TileStyle highlightDestinationStyle()  {return TileStyle.RS3;}

	@ConfigItem(
			keyName = "destinationTileBorderWitdh",
			name = "Destination tile border width",
			description = "The width of the custom destination indicator",
			section = destinationTileSection,
			position = 4
	)
	default double destinationTileBorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "highlightDestinationColor",
			name = "Destination tile",
			description = "Configures the highlight color of current destination",
			section = destinationTileSection,
			position = 5
	)
	default Color highlightDestinationColor()
	{
		return new Color(0xFFB3B03F);
	}

	@ConfigSection(
			name = "NPC Indicators",
			description = "Settings enhancing the standard NPC indicators",
			position = 6
	)
	String npcIndicatorsSection = "npcIndicatorsSection";

	@ConfigItem(
			keyName = "overlaysBelowNPCs",
			name = "Draw overlays below NPCs",
			description = "Requires GPU. Draws overlays below NPCs. CAUTION: Will make your game laggy if many NPCs are drawn above overlay at once. Best used for bosses, not large groups of NPCs. Do not enable this together with Better NPC Highlight's draw-below-NPC option.",
			section = npcIndicatorsSection,
			position = 6
	)
	default boolean overlaysBelowNPCs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "maxNPCsDrawn",
			name = "NPC limit",
			description = "The number of NPCs in the scene at a time to be affected by this plugin. Will affect FPS.",
			section = npcIndicatorsSection,
			position = 7
	)
	@Range(
			max = 20
	)
	default int maxNPCsDrawn() {return 10;}

	@ConfigItem(
			keyName = "topNPCs",
			name = "NPCs to draw on top",
			description = "List of NPCs to draw above overlays. To add NPCs, shift right-click them and click Draw-Above. Avoid combining this plugin's draw-below-NPC behavior with Better NPC Highlight's equivalent option.",
			section = npcIndicatorsSection,
			position = 8
	)
	default String getTopNPCs()
	{
		return "";
	}

	@ConfigItem(
			keyName = "topNPCs",
			name = "",
			description = ""
	)
	void setTopNPCs(String npcsToDrawAbove);

	@ConfigItem(
			keyName = "excludedNPCs",
			name = "NPC names to exclude",
			description = "Comma-separated list of NPC names or wildcard patterns. Matching NPCs in the current scene are resolved to NPC IDs internally.",
			section = npcIndicatorsSection,
			position = 9
	)
	default String getExcludedNPCs()
	{
		return "";
	}

	@ConfigItem(
			keyName = "excludedNPCs",
			name = "",
			description = ""
	)
	void setExcludedNPCs(String excludedNPCs);
}