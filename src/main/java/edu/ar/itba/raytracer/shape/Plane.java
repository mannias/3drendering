package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;

public class Plane extends SceneShape {

	protected static final double EPSILON = 0.000001f;

	private final double d;
	private final Vector3 normal;

	public Plane(final Transform transform, final ShapeProperties properties) {
		super(transform, properties);

		final Vector3 defaultNormal = new Vector3(0, 1, 0);
		this.normal = defaultNormal.rotate(transform.getRotation());
		d = -normal.dot(transform.getPosition());
	}

	public Plane(final Transform transform) {
		this(transform, new ShapeProperties());
	}

	public Plane(final ShapeProperties properties) {
		this(new Transform(), properties);
	}

	public Plane() {
		this(new Transform(), new ShapeProperties());
	}

	@Override
	public double intersect(final Ray ray) {
		final double vd = normal.dot(ray.getDir());
		 if (vd == 0) {
		 // No intersection.
		 return -1;
		 }

		final double v0 = -(normal.dot(ray.getSource()) + d);
		final double t = v0 / vd;
		if (t < 0) {
			// Intersection happens behind source.
			return -1;
		}

		return t;
	}

	@Override
	public Vector3 normal(final Vector3 point) {
		return normal;
	}

	public boolean containsPoint(final Vector3 point) {
		final double planeEq = normal.x * point.x + normal.y * point.y
				+ normal.z * point.z + d;

		return planeEq < EPSILON && planeEq > -EPSILON;
	}

}
