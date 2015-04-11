package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.properties.Transform;

public abstract class SceneElement {

	private final Transform transform;

	public SceneElement(final Transform transform) {
		this.transform = transform;
	}

	public SceneElement() {
		this(new Transform());
	}

	public Transform getTransform() {
		return transform;
	}

}
