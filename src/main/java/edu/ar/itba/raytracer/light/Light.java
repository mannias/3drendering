package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Vector4;

public abstract class Light {

	public final Color color;

	public Light(final Color color) {
		this.color = color;
	}

	public abstract Vector4 getDirection(final Vector4 hitPoint);

	public abstract Color getIntensity(final RayCollisionInfo hitPoint);
}
