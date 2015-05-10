package edu.ar.itba.raytracer.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public class Sphere extends GeometricObject {

	private final Vector4 center;
	private final double radius;

	public Sphere(final Vector4 center, final double radius,
			final Transform transform, final ShapeProperties properties) {
		super(transform, properties);
		this.center = center;
		this.radius = radius;
	}

	public Sphere(final Vector4 center, final double radius,
			final ShapeProperties properties) {
		this(center, radius, new Transform(), properties);
	}

//	public Sphere(final Vector4 center, final double radius,
//			final Transform transform) {
//		this(center, radius, transform, new ShapeProperties());
//	}
//
//	public Sphere(final Vector4 center, final double radius) {
//		this(center, radius, new Transform(), new ShapeProperties());
//	}

	@Override
	public boolean intersectionExists(Ray ray) {
		// calls.incrementAndGet();
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();

		// final Vector33 l = new Vector33(center);
		// l.sub(raySource);

		final double lx = center.x - raySource.x;
		final double ly = center.y - raySource.y;
		final double lz = center.z - raySource.z;

		// final double l2 = l.dot(l);

		final double l2 = lx * lx + ly * ly + lz * lz;

		final double radius2 = radius * radius;

		if (l2 < radius2) {
			intersections.incrementAndGet();

			return true;
		}

		// final double s = l.dot(rayDir);

		final double s = lx * rayDir.x + ly * rayDir.y + lz * rayDir.z;

		if (s < 0) {
			return false;
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return false;
		}
		// intersections.incrementAndGet();

		return true;
	}

	public static final AtomicInteger intersections = new AtomicInteger();
	public static final AtomicInteger calls = new AtomicInteger();

	// We use the algorithm described by Real-Time Rendering p. 740
	@Override
	public double intersect(Ray ray) {
		// calls.incrementAndGet();
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();
		
		
		final Vector4 transformedSource = getTransform().invTranslationTransform.multiplyVec(raySource);
		final Vector4 transformedDir = getTransform().invTranslationTransform.multiplyVec(rayDir);
		
		
		

		// final Vector33 l = new Vector33(center);
		// l.sub(raySource);
		// final double l2 = l.dot(l);

		final double lx = transformedSource.x;
		final double ly = transformedSource.y;
		final double lz = transformedSource.z;

		final double l2 = lx * lx + ly * ly + lz * lz;

		final double radius2 = 1;

		final boolean originIsInsideSphere = l2 < radius2;

		// final double s = l.dot(rayDir);

		final double s = lx * transformedDir.x + ly * transformedDir.y + lz * transformedDir.z;

		if (s < 0 && !originIsInsideSphere) {
			return -1;
		}

		final double m2 = l2 - s * s;

		if (m2 > radius2) {
			return -1;
		}

		final double q2 = radius2 - m2;

		final double q = Math.sqrt(q2);

		// intersections.incrementAndGet();

		if (originIsInsideSphere) {
			return s + q;
		}
		return s - q;
	}

	@Override
	public Vector4 normal(Vector4 point) {
		Vector4 aux = new Vector4(point);
		aux.sub(center);
		aux.normalize();
		return aux;
	}

	public BB getBB() {
		return new BB(center.x - radius, center.x + radius, center.y - radius,
				center.y + radius, center.z - radius, center.z + radius);
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
		return new PerfectSplits(getExtremePoints());
	}

    @Override
    public RayCollisionInfo hit(Ray ray) {
        return null;
    }

    @Override
    public AABB getAABB() {
        return null;
    }

    public Vector4[] getExtremePoints() {

		final Vector4 p1 = new Vector4(center);
		p1.add(new Vector4(radius, 0, 0, 0));
		final Vector4 p2 = new Vector4(center);
		p2.sub(new Vector4(radius, 0, 0, 0));
		final Vector4 p3 = new Vector4(center);
		p3.add(new Vector4(0, radius, 0, 0));
		final Vector4 p4 = new Vector4(center);
		p4.sub(new Vector4(0, radius, 0, 0));
		final Vector4 p5 = new Vector4(center);
		p5.add(new Vector4(0, 0, radius, 0));
		final Vector4 p6 = new Vector4(center);
		p6.sub(new Vector4(0, 0, radius, 0));

		return new Vector4[] { p1, p2, p3, p4, p5, p6 };
	}
}
