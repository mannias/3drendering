package edu.ar.itba.raytracer.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.shape.SceneShape.BB;
import edu.ar.itba.raytracer.shape.Sphere.PerfectSplits;
import edu.ar.itba.raytracer.vector.Vector4;

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

	private final Vector4 vertex0;
	private final Vector4 vertex1;
	private final Vector4 vertex2;

	private final Vector4 e1;
	private final Vector4 e2;

	private final Vector4 normal;

	public Triangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2) {
		super(new ShapeProperties(new Material(new Color(1, 1, 0), 1, 1, 1, 0,
				0, 1)));
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		e1 = new Vector4(vertex1);
		e1.sub(vertex0);
		e2 = new Vector4(vertex2);
		e2.sub(vertex0);

		normal = e2.cross(e1);
	}

	public List<Vector4> getVertexes() {
		// We do this for simplicity, not efficiency, since this isn't supposed
		// to be called when the image is being rendered.
		return Arrays.asList(vertex0, vertex1, vertex2);
	}

	@Override
	public double intersect(Ray ray) {
		final Vector4 d = ray.getDir();
		final Vector4 p = d.cross(e2);
		final double div = p.dot(e1);
		if (div > -EPSILON && div < EPSILON) {
			return -1;
		}

		final double invDiv = 1 / div;

		final Vector4 t = new Vector4(ray.getSource());
		t.sub(vertex0);

		final double u = invDiv * t.dot(p);

		if (u < 0 || u > 1) {
			return -1;
		}

		final Vector4 q = t.cross(e1);

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
	public Vector4 normal(Vector4 point) {
		return normal;
	}

	@Override
	public boolean intersectionExists(Ray ray) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BB getBB() {
		PerfectSplits ps = getPerfectSplits();
		return new BB(ps.mins[0].x, ps.maxs[0].x, ps.mins[1].y, ps.maxs[1].y,
				ps.mins[2].z, ps.maxs[2].z);
	}

	public static class PerfectSplits {
		Collection<Vector4> points;

		Vector4[] mins = new Vector4[3];
		Vector4[] maxs = new Vector4[3];

		public PerfectSplits(final Vector4... points) {
			this.points = Arrays.asList(points);

			for (int i = 0; i < 3; i++) {
				loadSplitsForAxis(i);
			}

		}

		private void loadSplitsForAxis(final int axis) {
			double min = Double.MAX_VALUE;
			Vector4 minPoint = null;
			for (final Vector4 point : points) {
				if (point.getElemsAsArray()[axis] < min) {
					min = point.getElemsAsArray()[axis];
					minPoint = point;
				}
			}
			mins[axis] = minPoint;

			double max = -Double.MAX_VALUE;
			Vector4 maxPoint = null;
			for (final Vector4 point : points) {
				if (point.getElemsAsArray()[axis] > max) {
					max = point.getElemsAsArray()[axis];
					maxPoint = point;
				}
			}
			maxs[axis] = maxPoint;
		}

		public PerfectSplits clipToVoxel(final BB voxel) {
			Vector4 minX = mins[0];
			Vector4 maxX = maxs[0];
			Vector4 minY = mins[1];
			Vector4 maxY = maxs[1];
			Vector4 minZ = mins[2];
			Vector4 maxZ = maxs[2];

			final Collection<Vector4> newPoints = new ArrayList<>();
			newPoints.add(minX);
			newPoints.add(minY);
			newPoints.add(minZ);
			newPoints.add(maxX);
			newPoints.add(maxY);
			newPoints.add(maxZ);
			if (maxX.x > voxel.maxX) {
				maxX.x = voxel.maxX;
			}
			if (minX.x < voxel.minX) {
				minX.x = voxel.minX;
			}
			if (maxY.y > voxel.maxY) {
				maxY.y = voxel.maxY;
			}
			if (minY.y < voxel.minY) {
				minY.y = voxel.minY;
			}
			if (maxZ.z > voxel.maxZ) {
				maxZ.z = voxel.maxZ;
			}
			if (minZ.z < voxel.minZ) {
				minZ.z = voxel.minZ;
			}

			return new PerfectSplits(newPoints.toArray(new Vector4[newPoints
					.size()]));
		}

		// Returns an array with [minx, maxx, miny, maxy, minz, maxz]
		public Vector4[] getAllExtremePoints() {
			return new Vector4[] { mins[0], maxs[0], mins[1], maxs[1],
					mins[2], maxs[2] };
		}
	}

	public PerfectSplits getPerfectSplits() {
		return new PerfectSplits(vertex0, vertex1, vertex2);
	}

}
