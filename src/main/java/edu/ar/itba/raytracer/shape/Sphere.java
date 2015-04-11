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

	@Override
	public double intersect(Ray ray) {
		final Vector3 raySource = ray.getSource();
		final Vector3 rayDir = ray.getDir();

		// Ray source components.
		final double xr0 = raySource.x;
		final double yr0 = raySource.y;
		final double zr0 = raySource.z;

		// Ray direction components.
		final double xd = rayDir.x;
		final double yd = rayDir.y;
		final double zd = rayDir.z;

		final double xDiff = (xr0 - center.x);
		final double yDiff = (yr0 - center.y);
		final double zDiff = (zr0 - center.z);

		final double c = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff - radius
				* radius;
		final double b = 2 * (xDiff * xd + yDiff * yd + zDiff * zd);
		final double a = 1;

		final double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			// No intersection.
			return -1.0f;
		} else if (discriminant == 0) {
			return -b / 2 / a;
		}
		final double dSqrt = (double) Math.sqrt(discriminant);
		final double divisor = 2 * a;
		final double negB = -b;
		final double sol1 = (negB + dSqrt) / divisor;
		if (sol1 < 0) {
			// The intersection happens behind the source of the ray.
			return -1.0f;
		}
		final double sol2 = (negB - dSqrt) / divisor;
		if (sol2 < 0) {
			return sol1;
		}

		return sol2;
	}

	@Override
	public Vector3 normal(Vector3 point) {
		return point.sub(center).normalize();
	}

}
