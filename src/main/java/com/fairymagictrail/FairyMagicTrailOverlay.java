package com.fairymagictrail;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Deque;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class FairyMagicTrailOverlay extends Overlay
{
	private static final Color[] FAIRY_COLORS = {
			new Color(255, 160, 225), new Color(194, 145, 255),
			new Color(125, 225, 255), new Color(255, 245, 255)
	};

	private final Client client;
	private final FairyMagicTrailPlugin plugin;

	@Inject
	private FairyMagicTrailOverlay(Client client, FairyMagicTrailPlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getConfig().enabled() || client.getLocalPlayer() == null)
		{
			return null;
		}

		Deque<TrailParticle> particles = plugin.getParticles();
		if (particles.isEmpty()) return null;

		long now = System.currentTimeMillis();
		long lifetime = Math.max(200, Math.min(2500, plugin.getConfig().lifetime()));
		int starSize = Math.max(4, Math.min(14, plugin.getConfig().size()));
		Object oldAntialias = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (TrailParticle particle : particles)
		{
			long age = now - particle.getSpawnTime();
			if (age < 0 || age >= lifetime) continue;

			LocalPoint local = particle.getLocation();
			Point canvas = Perspective.localToCanvas(client, local, client.getPlane());
			if (canvas == null) continue;

			float progress = (float) age / lifetime;

			float movement = progress * progress * (3.0f - 2.0f * progress);

			float fadeProgress = Math.max(0.0f, Math.min(1.0f,
					(progress - 0.55f) / 0.45f));

			float fade = 1.0f - fadeProgress * fadeProgress
					* (3.0f - 2.0f * fadeProgress);

			int alpha = Math.max(0, Math.min(255, (int) (fade * 220)));

			Color color = FAIRY_COLORS[
					(int) (Math.abs(particle.getPhase() * 10) % FAIRY_COLORS.length)
					];

			int driftX = (int) (Math.cos(particle.getPhase()) * 10.0 * movement);
			int driftY = (int) ((Math.sin(particle.getPhase()) * 4.0 - 14.0) * movement);

			int x = canvas.getX() + driftX;
			int y = canvas.getY() - 8 + driftY;

			int size = Math.max(1, (int) (starSize * (0.25 + 0.35 * fade)));

			// Soft pastel glow halo.
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha / 5));
			graphics.fillOval(x - size, y - size, size * 2, size * 2);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha / 3));
			graphics.fillOval(x - Math.max(1, size / 2), y - Math.max(1, size / 2),
					Math.max(2, size), Math.max(2, size));
			Polygon star = new Polygon();
			star.addPoint(x, y - size);
			star.addPoint(x + size / 3, y - size / 3);
			star.addPoint(x + size, y);
			star.addPoint(x + size / 3, y + size / 3);
			star.addPoint(x, y + size);
			star.addPoint(x - size / 3, y + size / 3);
			star.addPoint(x - size, y);
			star.addPoint(x - size / 3, y - size / 3);
			graphics.setColor(new Color(255, 255, 255, alpha));
			graphics.drawPolygon(star);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
			graphics.fillPolygon(star);
		}

		if (oldAntialias != null)
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
		return null;
	}
}
