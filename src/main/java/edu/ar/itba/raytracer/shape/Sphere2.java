package edu.ar.itba.raytracer.shape;

import java.util.ArrayList;
import java.util.List;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

public class Sphere2 extends GeometricObject {

//	private static class SphereAABB extends AABB {
//
//		public SphereAABB(double minX, double maxX, double minY, double maxY,
//				double minZ, double maxZ) {
//			super(minX, maxX, minY, maxY, minZ, maxZ);
//		}
//
//		@Override
//		public AABB translate(double x, double y, double z) {
//			return new SphereAABB(minX + x, maxX + x, minY + y, maxY + y, minZ
//					+ z, maxZ + z);
//		}
//
//		@Override
//		public AABB rotateX(double angle) {
//			return new SphereAABB(minX, maxX, minY, maxY, minZ, maxZ);
//		}
//
//		@Override
//		public AABB rotateY(double angle) {
//			return new SphereAABB(minX, maxX, minY, maxY, minZ, maxZ);
//		}
//
//		@Override
//		public AABB rotateZ(double angle) {
//			return new SphereAABB(minX, maxX, minY, maxY, minZ, maxZ);
//		}
//
//		@Override
//		public AABB scale(double x, double y, double z) {
//			return new SphereAABB(minX * x, maxX * x, minY * y, maxY * y, minZ
//					* z, maxZ * z);
//		}
//
//	}

    private final double radius;

    public Sphere2(final double radius) {
        this.radius = radius;
    }

    private static final long serialVersionUID = 92320640105733127L;

    @Override
    public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
        final Vector4 raySource = ray.getSource();
        final Vector4 rayDir = ray.getDir();

        final double lx = -raySource.x;
        final double ly = -raySource.y;
        final double lz = -raySource.z;

        final double l2 = lx * lx + ly * ly + lz * lz;

        final boolean originIsInsideSphere = l2 < radius;

        final double s = lx * rayDir.x + ly * rayDir.y + lz * rayDir.z;

        if (s < 0 && !originIsInsideSphere) {
            return null;// RayCollisionInfo.noCollision(ray);
        }

        final double m2 = l2 - s * s;

        if (m2 > radius) {
            return null;// RayCollisionInfo.noCollision(ray);
        }

        final double q2 = radius - m2;

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
        double minX = -radius;
        double maxX = radius;
        double minY = -radius;
        double maxY = radius;
        double minZ = -radius;
        double maxZ = radius;
        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
