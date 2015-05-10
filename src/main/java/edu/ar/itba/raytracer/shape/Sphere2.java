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

	private static final long serialVersionUID = 92320640105733127L;

	@Override
	public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();

		final double lx = -raySource.x;
		final double ly = -raySource.y;
		final double lz = -raySource.z;

		final double l2 = lx * lx + ly * ly + lz * lz;

		final double radius2 = 1;

		final boolean originIsInsideSphere = l2 < radius2;

		final double s = lx * rayDir.x + ly * rayDir.y + lz * rayDir.z;

		if (s < 0 && !originIsInsideSphere) {
			return null;// RayCollisionInfo.noCollision(ray);
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return null;// RayCollisionInfo.noCollision(ray);
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
		return new AABB(-1, 1, -1, 1, -1, 1);
	}

//	@Override
//	public List<Vector4> getExtremePoints() {
//		final List<Vector4> extremePoints = new ArrayList<>(6);
//
//		final Vector4 p1 = new Vector4(0, 0, 0, 1);
//		p1.add(new Vector4(1, 0, 0, 0));
//		extremePoints.add(p1);
//		final Vector4 p2 = new Vector4(0, 0, 0, 1);
//		p2.sub(new Vector4(1, 0, 0, 0));
//		extremePoints.add(p2);
//		final Vector4 p3 = new Vector4(0, 0, 0, 1);
//		p3.add(new Vector4(0, 1, 0, 0));
//		extremePoints.add(p3);
//		final Vector4 p4 = new Vector4(0, 0, 0, 1);
//		p4.sub(new Vector4(0, 1, 0, 0));
//		extremePoints.add(p4);
//		final Vector4 p5 = new Vector4(0, 0, 0, 1);
//		p5.add(new Vector4(0, 0, 1, 0));
//		extremePoints.add(p5);
//		final Vector4 p6 = new Vector4(0, 0, 0, 1);
//		p6.sub(new Vector4(0, 0, 1, 0));
//		extremePoints.add(p6);
//
//		return extremePoints;
//	}
}
