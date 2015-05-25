package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

/**
 * Shape that represents a triangle.
 * 
 * <p>
 * The intersection algorithm used is the one described by <a
 * href="http://www.graphics.cornell.edu/pubs/1997/MT97.pdf"
 * >http://www.graphics.cornell.edu/pubs/1997/MT97.pdf</a>
 */
public class Triangle extends GeometricObject {

	private final static double EPSILON = 0.00001;

	private final Vector4 vertex0;
	private final Vector4 vertex1;
	private final Vector4 vertex2;

	private final Vector4 e1;
	private final Vector4 e2;

	private final Vector4 normal;

	public Triangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2, final Vector4 normal) {
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		e1 = new Vector4(vertex1);
		e1.sub(vertex0);
		e2 = new Vector4(vertex2);
		e2.sub(vertex0);

		this.normal = normal;
		normal.normalize();
	}

	public Triangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2) {
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		e1 = new Vector4(vertex1);
		e1.sub(vertex0);
		e2 = new Vector4(vertex2);
		e2.sub(vertex0);

		this.normal = e1.cross(e2);
		normal.normalize();
	}

	@Override
	public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
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

		if (u < 0 || u > 1) {
			return null;
		}

		final Vector4 q = t.cross(e1);

		final double v = invDiv * d.dot(q);
		if (v < 0 || u + v > 1) {
			return null;
		}

		final double dist = invDiv * e2.dot(q);
		if (dist < EPSILON) {
			return null;
		}

		final RayCollisionInfo rci = new RayCollisionInfo(this, ray, dist);
		rci.normal = normal;
		rci.u = u;
		rci.v = v;
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

	@Override
	public String toString() {
		return "Triangle [vertex0=" + vertex0 + ", vertex1=" + vertex1
				+ ", vertex2=" + vertex2 + ", normal=" + normal + "]";
	}

}
