package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public class PointLight extends Light {

	public PointLight(Transform transform, LightProperties properties) {
		super(transform, properties);
	}

	public Vector4 getDirection(final Vector4 hitPoint) {
		final Vector4 ret = new Vector4(getTransform().getPosition());
		ret.sub(hitPoint);
		ret.normalize();
		return ret;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		return getProperties().getColor();

	}

}
