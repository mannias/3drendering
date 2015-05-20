package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public class DirectionalLight extends Light {

	private final Vector4 dir;

	public DirectionalLight(final Vector4 dir, final LightProperties properties) {
		super(new Transform(), properties);
		this.dir = dir;
		dir.normalize();
		dir.scalarMult(-1);
	}

	public Vector4 getDirection(final Vector4 hitPoint) {
		return dir;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		return getProperties().getColor();
	}

}
