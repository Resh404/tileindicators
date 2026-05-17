package io.leikvolle.tileindicators;

public enum TickCounterPosition
{
	DEFAULT("Default"),
	TOP("Top"),
	CENTERED("Centered"),
	BOTTOM("Bottom");

	private final String name;

	TickCounterPosition(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
