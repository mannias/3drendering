package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Intersectable;
import edu.ar.itba.raytracer.SceneElement;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;

public abstract class SceneShape extends SceneElement implements Intersectable {

	private final ShapeProperties properties;

	public SceneShape(final Transform transform,
			final ShapeProperties properties) {
		super(transform);
		this.properties = properties;

	}

	public SceneShape(final ShapeProperties properties) {
		this(new Transform(), properties);
	}

	public SceneShape() {
		this(new Transform(), new ShapeProperties());
	}

	public ShapeProperties getProperties() {
		return properties;
	}

	public abstract Vector3 normal(final Vector3 point);
	
}
