package edu.ar.itba.raytracer.shape;

import java.util.Arrays;
import java.util.List;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.vector.Vector3;

/**
 * Shape that represents a triangle.
 * 
 * <p>
 * The intersection algorithm used is the one described by <a
 * href="http://www.graphics.cornell.edu/pubs/1997/MT97.pdf"
 * >http://www.graphics.cornell.edu/pubs/1997/MT97.pdf</a>
 */
public class Triangle extends SceneShape {

	private final static double EPSILON = 0.00001;

	private final Vector3 vertex0;
	private final Vector3 vertex1;
	private final Vector3 vertex2;

	private final Vector3 e1;
	private final Vector3 e2;

	private final Vector3 normal;

	public Triangle(final Vector3 vertex0, final Vector3 vertex1,
			final Vector3 vertex2) {
		super(new ShapeProperties(new Color(1, 1, 0)));
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		e1 = vertex1.sub(vertex0);
		e2 = vertex2.sub(vertex0);

		normal = e2.cross(e1);
	}

	public List<Vector3> getVertexes() {
		// We do this for simplicity, not efficiency, since this isn't supposed
		// to be called when the image is being rendered.
		return Arrays.asList(vertex0, vertex1, vertex2);
	}

	@Override
	public double intersect(Ray ray) {
		final Vector3 d = ray.getDir();
		final Vector3 p = d.cross(e2);
		final double div = p.dot(e1);
		if (div > -EPSILON && div < EPSILON) {
			return -1;
		}

		final double invDiv = 1 / div;

		final Vector3 t = ray.getSource().sub(vertex0);

		final double u = invDiv * t.dot(p);

		if (u < 0 || u > 1) {
			return -1;
		}

		final Vector3 q = t.cross(e1);

		final double v = invDiv * d.dot(q);
		if (v < 0 || u + v > 1) {
			return -1;
		}

		final double dist = invDiv * e2.dot(q);
		if (dist < -EPSILON) {
			return -1;
		}

		return dist;
	}

	@Override
	public Vector3 normal(Vector3 point) {
		return normal;
	}

}
