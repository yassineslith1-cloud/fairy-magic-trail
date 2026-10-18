package com.fairymagictrail;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("fairymagictrail")
public interface FairyMagicTrailConfig extends Config
{
	@ConfigItem(keyName = "enabled", name = "Enable fairy trail", description = "Show fairy sparkles behind your character")
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(keyName = "lifetime", name = "Sparkle lifetime (ms)", description = "How long each sparkle stays visible")
	default int lifetime()
	{
		return 850;
	}

	@ConfigItem(keyName = "density", name = "Trail density", description = "Sparkles spawned per movement tick (1-5)")
	default int density()
	{
		return 3;
	}

	@ConfigItem(keyName = "size", name = "Sparkle size", description = "Sparkle size in pixels (4-14)")
	default int size()
	{
		return 8;
	}
}
