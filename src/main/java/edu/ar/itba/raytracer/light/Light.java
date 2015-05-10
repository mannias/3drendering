package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.SceneElement;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public abstract class Light extends SceneElement {

	private final LightProperties properties;

	public Light(final Transform transform, final LightProperties properties) {
		super(transform);
		this.properties = properties;
	}

	public Light() {
		this(new Transform(), new LightProperties());
	}

	public LightProperties getProperties() {
		return properties;
	}

	public abstract Vector4 getDirection(final Vector4 hitPoint);

	public abstract Color getIntensity(final Vector4 hitPoint);

}
