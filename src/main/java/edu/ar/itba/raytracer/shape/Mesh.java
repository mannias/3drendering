package edu.ar.itba.raytracer.shape;

import java.util.ArrayList;
import java.util.Collection;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.vector.Vector3;

public class Mesh extends SceneShape {

	private final Collection<Triangle> triangles;

	public Mesh(final Collection<Triangle> triangles) {
		this.triangles = new ArrayList<>(triangles);
	}

	@Override
	public double intersect(Ray ray) {
		double dist = -1;
		for (final Triangle triangle : triangles) {
			final double distToTriangle = triangle.intersect(ray);
			if (dist == -1) {
				dist = distToTriangle;
			} else if (distToTriangle != -1) {
				dist = Math.min(dist, distToTriangle);
			}
		}
		return dist;
	}

	@Override
	public Vector3 normal(Vector3 point) {
		// TODO(jmozzino): Might be hard.
		return null;
	}

}
