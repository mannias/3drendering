package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class BoundedPlane extends Plane {

	private final Vector4 widthVector;
	private final Vector4 heightVector;

	public BoundedPlane(final Transform transform,
			final ShapeProperties properties) {
		super(transform, properties);
		widthVector = new Vector3(transform.getScale().x, 0, 0).normalize()
				.rotate(transform.getRotation());
		heightVector = new Vector3(0, 0, transform.getScale().z).normalize()
				.rotate(transform.getRotation());
	}

	@Override
	public double intersect(Ray ray) {
		final double dist = super.intersect(ray);

		// If the ray doesn't intersect with the infinite plane, then it won't
		// intersect with this bounded plane.
		if (dist == -1) {
			return -1;
		}

		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();
		final Vector4 center = getTransform().getPosition();

		final Vector4 intersectionPoint = new Vector3(raySource.x + dist
				* rayDir.x, raySource.y + dist * rayDir.y, raySource.z + dist
				* rayDir.z);

		// Calculate Manhattan steps to check that they are both less that the
		// width and the height.
		{
			final double xy = heightVector.x * widthVector.y - widthVector.x
					* heightVector.y;

			// First intersection + wV * s = center + hV * t
			double a, b, c, d, div, d1, d2;
			if (xy > EPSILON || xy < -EPSILON) {
				a = center.x;
				b = intersectionPoint.x;
				c = center.y;
				d = intersectionPoint.y;
				div = xy;
				d1 = widthVector.x;
				d2 = widthVector.y;
			} else {
				final double xz = heightVector.x * widthVector.z
						- widthVector.x * heightVector.z;
				if (xz > EPSILON || xz < -EPSILON) {
					a = center.x;
					b = intersectionPoint.x;
					c = center.z;
					d = intersectionPoint.z;
					div = xz;
					d1 = widthVector.x;
					d2 = widthVector.z;
				} else {
					final double yz = heightVector.y * widthVector.z
							- widthVector.y * heightVector.z;
					if (yz > EPSILON || yz < -EPSILON) {
						a = center.y;
						b = intersectionPoint.y;
						c = center.z;
						d = intersectionPoint.z;
						div = yz;
						d1 = widthVector.y;
						d2 = widthVector.z;
					} else {
						return -1;
					}
				}
			}

			final double t = ((c - d) * d1 - (a - b) * d2) / div;
			if (Math.abs(t) > getTransform().getScale().z) {
				return -1;
			}
		}
		{
			final double xy = widthVector.x * heightVector.y - heightVector.x
					* widthVector.y;

			// intersection + hV * s = center + wV * t
			double a, b, c, d, div, d1, d2;
			if (xy > EPSILON || xy < -EPSILON) {
				a = center.x;
				b = intersectionPoint.x;
				c = center.y;
				d = intersectionPoint.y;
				div = xy;
				d1 = heightVector.x;
				d2 = heightVector.y;
			} else {
				final double xz = widthVector.x * heightVector.z
						- heightVector.x * widthVector.z;
				if (xz > EPSILON || xz < -EPSILON) {
					a = center.x;
					b = intersectionPoint.x;
					c = center.z;
					d = intersectionPoint.z;
					div = xz;
					d1 = heightVector.x;
					d2 = heightVector.z;
				} else {
					final double yz = widthVector.y * heightVector.z
							- heightVector.y * widthVector.z;
					if (yz > EPSILON || yz < -EPSILON) {
						a = center.y;
						b = intersectionPoint.y;
						c = center.z;
						d = intersectionPoint.z;
						div = yz;
						d1 = heightVector.y;
						d2 = heightVector.z;
					} else {
						return -1;
					}
				}
			}

			final double t = ((c - d) * d1 - (a - b) * d2) / div;
			if (Math.abs(t) > getTransform().getScale().x) {
				return -1;
			}
		}

		return dist;
	}

    //TODO: Intersection
    @Override
    public boolean intersectionExists(Ray ray) {
        return super.intersectionExists(ray);
    }
}
