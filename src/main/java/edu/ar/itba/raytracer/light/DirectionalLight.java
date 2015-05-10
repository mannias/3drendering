package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Vector4;

public class DirectionalLight extends Light {

	private final Vector4 dir;

	public DirectionalLight(final Vector4 dir) {
		this.dir = dir;
	}

	public Vector4 getDirection(final Vector4 hitPoint) {
		return dir;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		return getProperties().getColor();
	}

}
