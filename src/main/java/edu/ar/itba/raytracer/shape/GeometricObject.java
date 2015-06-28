package edu.ar.itba.raytracer.shape;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public abstract class GeometricObject {

	public static Matrix44 translationMatrix(final double x, final double y,
			final double z) {
		return new Matrix44(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1);
	}

	public static Matrix44 rotationXMatrix(final double deg) {
		final double rad = degToRad(deg);
		return new Matrix44(1, 0, 0, 0, 0, cos(rad), -sin(rad), 0, 0, sin(rad),
				cos(rad), 0, 0, 0, 0, 1);
	}

	public static Matrix44 rotationYMatrix(final double deg) {
		final double rad = degToRad(deg);
		return new Matrix44(cos(rad), 0, sin(rad), 0, 0, 1, 0, 0, -sin(rad), 0,
				cos(rad), 0, 0, 0, 0, 1);
	}

	public static Matrix44 rotationZMatrix(final double deg) {
		final double rad = degToRad(deg);
		return new Matrix44(cos(rad), -sin(rad), 0, 0, sin(rad), cos(rad), 0,
				0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public static Matrix44 scaleMatrix(final double x, final double y,
			final double z) {
		return new Matrix44(x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1);
	}

	public static double degToRad(final double deg) {
		return deg * Math.PI / 180;
	}

	public static class AABB {
		public double minX, maxX, minY, maxY, minZ, maxZ;

		public Vector4 center, exX, exY, exZ;

		public AABB(double minX, double maxX, double minY, double maxY,
				double minZ, double maxZ) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
			this.minZ = minZ;
			this.maxZ = maxZ;
		}

		public AABB(final Vector4 center, final Vector4 exX, final Vector4 exY,
				final Vector4 exZ) {
			this.center = center;
			this.exX = exX;
			this.exY = exY;
			this.exZ = exZ;
		}

		public double getSurfaceArea() {
			final double width = maxX - minX;
			final double height = maxY - minY;
			final double depth = maxZ - minZ;
			return 2 * (width * height + height * depth + depth * width);
		}

		public List<Vector4> getCorners() {
			final Vector4 min = new Vector4(minX, minY, minZ, 1);
			final Vector4 max = new Vector4(maxX, maxY, maxZ, 1);

			final double diffX = maxX - minX;
			final double diffY = maxY - minY;
			final double diffZ = maxZ - minZ;

			final Vector4 p0 = new Vector4(min);
			p0.add(new Vector4(diffX, 0, 0, 0));
			final Vector4 p1 = new Vector4(min);
			p1.add(new Vector4(0, diffY, 0, 0));
			final Vector4 p2 = new Vector4(min);
			p2.add(new Vector4(0, 0, diffZ, 0));
			final Vector4 p3 = new Vector4(max);
			p3.sub(new Vector4(diffX, 0, 0, 0));
			final Vector4 p4 = new Vector4(max);
			p4.sub(new Vector4(0, diffY, 0, 0));
			final Vector4 p5 = new Vector4(max);
			p5.sub(new Vector4(0, 0, diffZ, 0));

			final List<Vector4> list = new ArrayList<>(8);
			Collections.addAll(list, min, max, p0, p1, p2, p3, p4, p5);
			return list;
		}

	}

	public static class PerfectSplits {
		Collection<Vector4> points;

		public Vector4[] mins = new Vector4[3];
		public Vector4[] maxs = new Vector4[3];

		public PerfectSplits(final Collection<Vector4> newPoints) {
			this.points = newPoints;

			for (int i = 0; i < 3; i++) {
				loadSplitsForAxis(i);
			}

		}

		private void loadSplitsForAxis(final int axis) {
			final int axis2;
			switch (axis) {
			case 0:
				axis2 = 2;
				break;
			case 1:
				axis2 = 1;
				break;
			case 2:
				axis2 = 0;
				break;
			default:
				throw new RuntimeException();
			}
			double min = Double.MAX_VALUE;
			Vector4 minPoint = null;
			for (final Vector4 point : points) {
				if (point.getElemsAsArray()[axis2] < min) {
					min = point.getElemsAsArray()[axis2];
					minPoint = point;
				}
			}
			mins[axis] = minPoint;

			double max = -Double.MAX_VALUE;
			Vector4 maxPoint = null;
			for (final Vector4 point : points) {
				if (point.getElemsAsArray()[axis2] > max) {
					max = point.getElemsAsArray()[axis2];
					maxPoint = point;
				}
			}
			maxs[axis] = maxPoint;
		}

		public PerfectSplits clipToVoxel(final AABB voxel) {
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

			return new PerfectSplits(newPoints);
		}

		// Returns an array with [minx, maxx, miny, maxy, minz, maxz]
		public Vector4[] getAllExtremePoints() {
			return new Vector4[] { mins[0], maxs[0], mins[1], maxs[1], mins[2],
					maxs[2] };
		}
	}

	public PerfectSplits getPerfectSplits() {
		return new PerfectSplits(getAABB().getCorners());
	}

	public Material material;

	public abstract RayCollisionInfo hit(final Ray ray,
			final CustomStack stack, final int top);

	public abstract AABB getAABB();

    public abstract Vector4 sampleObject();

}
