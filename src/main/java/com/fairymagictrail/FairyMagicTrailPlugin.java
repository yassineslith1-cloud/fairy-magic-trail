package com.fairymagictrail;

import com.google.inject.Provides;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
		name = "Fairy Magic Trail",
		description = "Pastel fairy sparkles follow your character while moving",
		tags = {"fairy", "trail", "sparkles", "cosmetic"}
)
public class FairyMagicTrailPlugin extends Plugin
{
	private static final int MAX_PARTICLES = 6;

	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private FairyMagicTrailOverlay overlay;
	@Inject
	private FairyMagicTrailConfig config;

	private final Deque<TrailParticle> particles = new ArrayDeque<>();
	private LocalPoint lastLocation;

	@Provides
	FairyMagicTrailConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FairyMagicTrailConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		particles.clear();
		lastLocation = null;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		particles.clear();
		lastLocation = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			lastLocation = null;
			particles.clear();
			return;
		}

		LocalPoint current = client.getLocalPlayer().getLocalLocation();

		if (current == null)
		{
			lastLocation = null;
			return;
		}

		if (!config.enabled())
		{
			lastLocation = current;
			particles.clear();
			return;
		}

		long now = System.currentTimeMillis();
		removeExpired(now);

		if (lastLocation != null
				&& (current.getX() != lastLocation.getX()
				|| current.getY() != lastLocation.getY()))
		{
			for (int i = 0; i < 2; i++)
			{
				int offsetX = (int) (Math.random() * 41) - 20;
				int offsetY = (int) (Math.random() * 41) - 20;

				LocalPoint sparklePoint = new LocalPoint(
						Math.max(0, current.getX() + offsetX),
						Math.max(0, current.getY() + offsetY)
				);

				particles.addLast(
						new TrailParticle(sparklePoint, now, Math.random() * Math.PI * 2)
				);
			}
		}

		while (particles.size() > MAX_PARTICLES)
		{
			particles.removeFirst();
		}

		lastLocation = current;
	}

	private void removeExpired(long now)
	{
		long lifetime = Math.max(200, Math.min(2500, config.lifetime()));
		while (!particles.isEmpty() && now - particles.peekFirst().getSpawnTime() > lifetime)
		{
			particles.removeFirst();
		}
	}

	Deque<TrailParticle> getParticles()
	{
		return particles;
	}

	FairyMagicTrailConfig getConfig()
	{
		return config;
	}
}
