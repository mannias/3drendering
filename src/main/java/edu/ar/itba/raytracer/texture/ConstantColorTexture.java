package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;

public class ConstantColorTexture implements Texture {

	private final Color color;

	public ConstantColorTexture(final Color color) {
		this.color = color;
	}

	public ConstantColorTexture(final double r, final double g, final double b) {
		this(new Color(r, g, b));
	}

	@Override
	public Color getColor(RayCollisionInfo collisionInfo) {
		return color;
	}

}
