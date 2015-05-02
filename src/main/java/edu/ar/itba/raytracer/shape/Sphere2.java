package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

public class Sphere2 extends GeometricObject {

	@Override
	public RayCollisionInfo hit(Ray ray) {
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();

		final double lx = raySource.x;
		final double ly = raySource.y;
		final double lz = raySource.z;

		final double l2 = lx * lx + ly * ly + lz * lz;

		final double radius2 = 1;

		final boolean originIsInsideSphere = l2 < radius2;

		final double s = lx * rayDir.x + ly * rayDir.y + lz * rayDir.z;

		if (s < 0 && !originIsInsideSphere) {
			return RayCollisionInfo.noCollision(ray);
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return RayCollisionInfo.noCollision(ray);
		}

		final double q2 = radius2 - m2;

		final double q = Math.sqrt(q2);

		if (originIsInsideSphere) {
			return new RayCollisionInfo(this, ray, s + q);
		}
		return new RayCollisionInfo(this, ray, s - q);

	}
}
