package com.fairymagictrail;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FairyMagicTrailPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FairyMagicTrailPlugin.class);
		RuneLite.main(args);
	}
}