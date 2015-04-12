package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;

public class Sphere extends SceneShape {

	private final Vector3 center;
	private final double radius;

	public Sphere(final Vector3 center, final double radius,
			final Transform transform, final ShapeProperties properties) {
		super(transform, properties);
		this.center = center;
		this.radius = radius;
	}

	public Sphere(final Vector3 center, final double radius,
			final ShapeProperties properties) {
		this(center, radius, new Transform(), properties);
	}

	public Sphere(final Vector3 center, final double radius,
			final Transform transform) {
		this(center, radius, transform, new ShapeProperties());
	}

	public Sphere(final Vector3 center, final double radius) {
		this(center, radius, new Transform(), new ShapeProperties());
	}

	public boolean intersectionExists(Ray ray) {
		final Vector3 raySource = ray.getSource();
		final Vector3 rayDir = ray.getDir();

		final Vector3 l = center.sub(raySource);
		final double l2 = l.dot(l);

		final double radius2 = radius * radius;

		if (l2 < radius2) {
			return true;
		}

		final double s = l.dot(rayDir);

		if (s < 0) {
			return false;
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return false;
		}

		return true;
	}

	// We use the algorithm described by Real-Time Rendering p. 740
	@Override
	public double intersect(Ray ray) {
		final Vector3 raySource = ray.getSource();
		final Vector3 rayDir = ray.getDir();

		final Vector3 l = center.sub(raySource);
		final double l2 = l.dot(l);

		final double radius2 = radius * radius;

		final boolean originIsInsideSphere = l2 < radius2;

		final double s = l.dot(rayDir);

		if (s < 0 && !originIsInsideSphere) {
			return -1;
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return -1;
		}

		final double q2 = radius2 - m2;

		final double q = Math.sqrt(q2);

		if (originIsInsideSphere) {
			return s + q;
		}
		return s - q;
	}

	@Override
	public Vector3 normal(Vector3 point) {
		return point.sub(center).normalize();
	}

}
