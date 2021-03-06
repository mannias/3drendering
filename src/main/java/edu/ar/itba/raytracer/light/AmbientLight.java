package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Vector4;

public class AmbientLight extends Light {

	public AmbientLight(Color color) {
		super(color);
	}

	@Override
	public Vector4 getDirection(final Vector4 hitPoint) {
		return null;
	}

	@Override
	public Color getIntensity(RayCollisionInfo hitPoint) {
		return color;
	}

}
