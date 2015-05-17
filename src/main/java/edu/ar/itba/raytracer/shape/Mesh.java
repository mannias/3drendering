package edu.ar.itba.raytracer.shape;

import java.util.ArrayList;
import java.util.Collection;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.MeshTriangle;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;

public class Mesh extends GeometricObject {

	private static final long serialVersionUID = 2143506060307002492L;
	private final Collection<MeshTriangle> triangles;
	private final KdTree tree;

	public Mesh(final Collection<MeshTriangle> triangles) {
		this.triangles = new ArrayList<>(triangles);
		final Collection<GeometricObject> instances = new ArrayList<>(
				triangles.size());
		for (final MeshTriangle t : triangles) {
			instances.add(t);
		}
		tree = KdTree.from(instances);
	}

	@Override
	public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
		// double dist = Double.MAX_VALUE;
		// RayCollisionInfo minCollision = null;
		// for (final Triangle triangle : triangles) {
		// final RayCollisionInfo collision = triangle.hit(ray, stack, top);
		// if (collision != null && collision.distance < dist) {
		// dist = collision.distance;
		// minCollision = collision;
		// }
		// }
		// return minCollision;
		return tree.getCollision(Double.MAX_VALUE, ray, stack, top);
	}

	@Override
	public AABB getAABB() {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (final MeshTriangle triangle : triangles) {
			final AABB aabb = triangle.getAABB();
			if (aabb.minX < minX) {
				minX = aabb.minX;
			}
			if (aabb.maxX > maxX) {
				maxX = aabb.maxX;
			}
			if (aabb.minY < minY) {
				minY = aabb.minY;
			}
			if (aabb.maxY > maxY) {
				maxY = aabb.maxY;
			}
			if (aabb.minZ < minZ) {
				minZ = aabb.minZ;
			}
			if (aabb.maxZ > maxZ) {
				maxZ = aabb.maxZ;
			}
		}

		return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
	}

}
