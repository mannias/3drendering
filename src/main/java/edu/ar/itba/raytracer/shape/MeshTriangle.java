package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class MeshTriangle extends GeometricObject {

	private final static double EPSILON = 0.00001;

	private final Vector4 vertex0;
	private final Vector4 vertex1;
	private final Vector4 vertex2;

	private final Vector4 n0;
	private final Vector4 n1;
	private final Vector4 n2;

//	private final Vector2 uv0;
//	private final Vector2 uv1;
//	private final Vector2 uv2;

	private final Vector4 e1;
	private final Vector4 e2;

	public MeshTriangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2, final Vector4 n0, final Vector4 n1,
			final Vector4 n2) {
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		this.n0 = n0;
		this.n0.normalize();
		this.n1 = n1;
		this.n1.normalize();
		this.n2 = n2;
		this.n2.normalize();

//		this.uv0 = uv0;
//		this.uv1 = uv1;
//		this.uv2 = uv2;

		e1 = new Vector4(vertex1);
		e1.sub(vertex0);
		e2 = new Vector4(vertex2);
		e2.sub(vertex0);
	}

	@Override
	public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
		final Vector4 d = ray.getDir();
		final Vector4 p = d.cross(e2);
		final double div = p.dot(e1);
		if (div > -EPSILON && div < EPSILON) {
			return null;
		}

		final double invDiv = 1 / div;

		final Vector4 t = new Vector4(ray.getSource());
		t.sub(vertex0);

		final double u = invDiv * t.dot(p);

		if (u < EPSILON || u > 1 + EPSILON) {
			return null;
		}

		final Vector4 q = t.cross(e1);

		final double v = invDiv * d.dot(q);
		if (v < EPSILON || u + v > 1 + EPSILON) {
			return null;
		}

		final double dist = invDiv * e2.dot(q);
		if (dist < -EPSILON) {
			return null;
		}

		final RayCollisionInfo rci = new RayCollisionInfo(this, ray, dist);

//		final double interpolatedU = uv0.x * (1 - u - v) + uv1.x * u + uv2.x
//				* v;
//		final double interpolatedV = uv0.y * (1 - u - v) + uv1.y * u + uv2.y
//				* v;

//		rci.u = interpolatedU;
//		rci.v = interpolatedV;

		final Vector4 normal = new Vector4(n0);
		normal.scalarMult(1 - u - v);

		final Vector4 normal1 = new Vector4(n1);
		normal1.scalarMult(u);

		final Vector4 normal2 = new Vector4(n2);
		normal2.scalarMult(v);

		normal.add(normal1);
		normal.add(normal2);

		rci.normal = normal;

		return rci;
	}

	@Override
	public AABB getAABB() {
		final Vector4[] vertexes = new Vector4[] { vertex0, vertex1, vertex2 };
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (final Vector4 vertex : vertexes) {
			if (vertex.x < minX) {
				minX = vertex.x;
			}
			if (vertex.x > maxX) {
				maxX = vertex.x;
			}
			if (vertex.y < minY) {
				minY = vertex.y;
			}
			if (vertex.y > maxY) {
				maxY = vertex.y;
			}
			if (vertex.z < minZ) {
				minZ = vertex.z;
			}
			if (vertex.z > maxZ) {
				maxZ = vertex.z;
			}
		}

		return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
	}
}
