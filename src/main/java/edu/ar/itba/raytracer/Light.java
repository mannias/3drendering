package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.properties.Transform;

public class Light extends SceneElement {

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

}
