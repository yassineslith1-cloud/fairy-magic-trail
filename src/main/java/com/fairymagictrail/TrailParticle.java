package com.fairymagictrail;

import net.runelite.api.coords.LocalPoint;

final class TrailParticle
{
	private final LocalPoint location;
	private final long spawnTime;
	private final double phase;

	TrailParticle(LocalPoint location, long spawnTime, double phase)
	{
		this.location = location;
		this.spawnTime = spawnTime;
		this.phase = phase;
	}

	LocalPoint getLocation()
	{
		return location;
	}

	long getSpawnTime()
	{
		return spawnTime;
	}

	double getPhase()
	{
		return phase;
	}
}