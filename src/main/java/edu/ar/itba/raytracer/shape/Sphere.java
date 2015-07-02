package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

public class Sphere extends GeometricObject {
	
	private final double radius;
	
	public Sphere(final double radius) {
		this.radius = radius;
	}

    @Override
	public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();

		final double lx = -raySource.x;
		final double ly = -raySource.y;
		final double lz = -raySource.z;

		final double l2 = lx * lx + ly * ly + lz * lz;

		final double radius2 = radius * radius;

		final boolean originIsInsideSphere = l2 < radius2;

		final double s = lx * rayDir.x + ly * rayDir.y + lz * rayDir.z;

		if (s < 0 && !originIsInsideSphere) {
			return null;
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return null;
		}

		final double q2 = radius2 - m2;

		final double q = Math.sqrt(q2);

		if (originIsInsideSphere) {
			final RayCollisionInfo collision = new RayCollisionInfo(this, ray,
					s + q);
			final Vector4 normal = new Vector4(
					collision.getLocalCollisionPoint());
			normal.w = 0;
			// Center is (0,0,0), so nothing else is needed;
			normal.normalize();
			collision.normal = normal;
			return collision;
		}
		final RayCollisionInfo collision = new RayCollisionInfo(this, ray, s
				- q);
		final Vector4 normal = new Vector4(collision.getLocalCollisionPoint());
		normal.w = 0;
		// Center is (0,0,0), so nothing else is needed;
		normal.normalize();
		collision.normal = normal;
		return collision;
	}

	@Override
	public AABB getAABB() {
        return new AABB(-radius, radius, -radius, radius, -radius, radius);
	}

    @Override
    public Vector4 sampleObject() {
        double u = Math.random();
        double v = Math.random();

        double tita = 2*Math.PI*u;
        double phi = Math.acos(2*v-1);

        double x = Math.sqrt(1d-phi*phi)* Math.cos(tita);
        double y = Math.sqrt(1d-phi*phi)* Math.sin(tita);
        double z = phi;

        return new Vector4(x*radius, y*radius, z*radius,1);



    }

	@Override
	public Vector4 normal(Vector4 point) {
		final Vector4 n = new Vector4(point);
		n.w = 0;
		return n.normalize();
	}

}
