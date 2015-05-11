package edu.ar.itba.raytracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ar.itba.raytracer.GeometricObject.AABB;
import edu.ar.itba.raytracer.GeometricObject.PerfectSplits;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.Triangle;
import edu.ar.itba.raytracer.vector.Vector4;

public class KdTree implements Serializable {

	private static final long serialVersionUID = -4875961080744635950L;

	public abstract static class KdNode implements Serializable {

		private static final long serialVersionUID = -2305599400602183527L;
	}

	private static class KdInternalNode extends KdNode {
		private static final long serialVersionUID = 9107223497382945170L;
		private final int splitAxis;
		private final double splitPosition;
		private final KdNode left, right;

		public KdInternalNode(final int splitAxis, final double splitPosition,
				final KdNode left, final KdNode right) {
			this.splitAxis = splitAxis;
			this.splitPosition = splitPosition;
			this.left = left;
			this.right = right;
		}

	}

	private static class KdLeafNode extends KdNode {
		private static final long serialVersionUID = 2104639551541046226L;

		private final Collection<GeometricObject> shapes;

		public KdLeafNode(final Collection<GeometricObject> shapes) {
			this.shapes = shapes;
		}

	}

	private KdNode root;

	public static class StackElement {
		public KdNode node;
		public double start;
		public double end;

		public StackElement() {

		}

		public StackElement(final KdNode node, final double start,
				final double end) {
			this.node = node;
			this.start = start;
			this.end = end;
		}

	}

	private static double EPSILON = 0.00001;

	public static KdTree from(Scene scene) {
		return from(scene.getGObjects());
	}

	public static KdTree from(Collection<GeometricObject> objs) {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		for (GeometricObject instance : objs) {
			AABB bb = instance.getAABB();
			if (bb.minX < minX) {
				minX = bb.minX;
			}
			if (bb.maxX > maxX) {
				maxX = bb.maxX;
			}
			if (bb.minY < minY) {
				minY = bb.minY;
			}
			if (bb.maxY > maxY) {
				maxY = bb.maxY;
			}
			if (bb.minZ < minZ) {
				minZ = bb.minZ;
			}
			if (bb.maxZ > maxZ) {
				maxZ = bb.maxZ;
			}
		}

		KdTree tree = new KdTree();
		// tree.rootBb = new BB(minX, maxX, minY, maxY, minZ, maxZ);
		double size = 30;
		tree.rootBb = new AABB(-size, size, -size, size, -size, size);
		tree.root = recBuild(0, objs, tree.rootBb);
		return tree;
	}

	private AABB rootBb;

	private static int leaves;
	private static double amount;
	private static double depth;

	private static KdNode recBuild(final int depth,
			Collection<GeometricObject> shapes, AABB bb) {
		final double cisec = 1;
		final double ctrav = 1;

//		 SplitInfo si = findPlane(shapes, bb, ctrav, cisec);

		SplitInfo si = naiveSah(shapes, bb, ctrav, cisec);

		if (shapes.isEmpty() || si.minCost >= shapes.size() * cisec) {
			leaves++;
			amount += shapes.size();
			KdTree.depth += depth;
			return new KdLeafNode(shapes);
		}

		return new KdInternalNode(si.axis, si.splitPosition, recBuild(
				depth + 1, si.tl, si.leftVoxel), recBuild(depth + 1, si.tr,
				si.rightVoxel));

	}

	private static class TriangleClassification {
		Collection<GeometricObject> tl, tr, tp;

		public TriangleClassification(final Collection<GeometricObject> tl,
				final Collection<GeometricObject> tr,
				final Collection<GeometricObject> tp) {
			this.tl = tl;
			this.tr = tr;
			this.tp = tp;
		}
	}

	private static TriangleClassification classify(
			Collection<GeometricObject> shapes, AABB leftVoxel, AABB rightVoxel,
			int splitAxis, double splitPosition) {
		final Collection<GeometricObject> tl = new ArrayList<>();
		final Collection<GeometricObject> tr = new ArrayList<>();
		final Collection<GeometricObject> tp = new ArrayList<>();

		for (GeometricObject instance : shapes) {
			final Collection<Vector4> vertexes = instance.getAABB()
					.getCorners();
			boolean isOnPlane = true;

			for (Vector4 vertex : vertexes) {
				if (!(vertex.getElemsAsArray()[splitAxis] <= splitPosition + 0.00001 && vertex
						.getElemsAsArray()[splitAxis] >= splitPosition - 0.00001)) {
					isOnPlane = false;
					break;
				}
			}
			if (isOnPlane) {
				tp.add(instance);
				continue;
			}

			for (Vector4 vertex : vertexes) {
				if (vertex.getElemsAsArray()[splitAxis] < splitPosition) {
					tl.add(instance);
					break;
				}
			}

			for (Vector4 vertex : vertexes) {
				if (vertex.getElemsAsArray()[splitAxis] > splitPosition) {
					tr.add(instance);
					break;
				}
			}
		}

		return new TriangleClassification(tl, tr, tp);
	}

	private static class SplitInfo {
		int axis;
		double splitPosition;
		Collection<GeometricObject> tl;
		Collection<GeometricObject> tr;
		AABB leftVoxel;
		AABB rightVoxel;
		double minCost;

		public SplitInfo(int axis, double splitPosition,
				Collection<GeometricObject> tl, Collection<GeometricObject> tr,
				AABB leftVoxel, AABB rightVoxel, double minCost) {
			this.axis = axis;
			this.splitPosition = splitPosition;
			this.tl = tl;
			this.tr = tr;
			this.leftVoxel = leftVoxel;
			this.rightVoxel = rightVoxel;
			this.minCost = minCost;
		}
	}

	private static class SAH {
		private boolean left;
		private double cost;

		public SAH(final boolean left, final double cost) {
			this.left = left;
			this.cost = cost;
		}
	}

	private static SAH doSAH(final int splitAxis, final double splitPosition,
			final AABB voxel, final double nl, final double nr, final double np,
			final double ctrav, final double cisec) {
		final double sav = voxel.getArea();
		final AABB[] voxels = splitVoxel(voxel, splitAxis, splitPosition);
		final double pvlv = voxels[0].getArea() / sav;
		final double pvrv = voxels[1].getArea() / sav;

		final double csplitl = ctrav + cisec * (pvlv * (nl + np) + pvrv * nr);

		final double csplitr = ctrav + cisec * (pvlv * nl + pvrv * (nr + np));

		boolean left = csplitl < csplitr;

		return new SAH(left, left ? csplitl : csplitr);

	}

	private static SplitInfo naiveSah(Collection<GeometricObject> shapes,
			AABB voxel, final double ctrav, final double cisec) {
		Boolean toLeft = null;
		double costAux = Double.MAX_VALUE;
		double bestSplitPosition = 0;
		TriangleClassification triangleCl = null;
		int bestAxis = 0;
		AABB[] childVoxels = null;

		for (final GeometricObject ss : shapes) {
			// Init vars
			final Vector4[] splits = ss.getPerfectSplits().clipToVoxel(voxel)
					.getAllExtremePoints();

			for (int i = 0; i < splits.length; i++) {
				final int axis = i / 2;
				final double splitPosition = splits[i].getElemsAsArray()[axis];

				final AABB[] voxels = splitVoxel(voxel, axis, splitPosition);

				final TriangleClassification cl = classify(shapes, voxels[0],
						voxels[1], axis, splitPosition);

				final double sav = voxel.getArea();
				final double pvlv = voxels[0].getArea() / sav;
				final double pvrv = voxels[1].getArea() / sav;

				final double csplitl = ctrav
						+ cisec
						* (pvlv * (cl.tl.size() + cl.tp.size()) + pvrv
								* cl.tr.size());

				final double csplitr = ctrav
						+ cisec
						* (pvlv * cl.tl.size() + pvrv
								* (cl.tr.size() + cl.tp.size()));

				boolean left = csplitl < csplitr;

				if (left) {
					if (csplitl < costAux) {
						costAux = csplitl;
						toLeft = left;
						bestSplitPosition = splitPosition;
						bestAxis = axis;
						triangleCl = cl;
						childVoxels = voxels;
					}
				} else {
					if (csplitr < costAux) {
						costAux = csplitr;
						toLeft = left;
						bestSplitPosition = splitPosition;
						bestAxis = axis;
						triangleCl = cl;
						childVoxels = voxels;
					}
				}
			}
		}

		if (toLeft == null) {
			return new SplitInfo(0, 0, Collections.emptyList(),
					Collections.emptyList(), null, null, 0);
		}

		if (toLeft) {
			triangleCl.tl.addAll(triangleCl.tp);
			return new SplitInfo(bestAxis, bestSplitPosition, triangleCl.tl,
					triangleCl.tr, childVoxels[0], childVoxels[1], costAux);
		} else {
			triangleCl.tr.addAll(triangleCl.tp);
			return new SplitInfo(bestAxis, bestSplitPosition, triangleCl.tl,
					triangleCl.tr, childVoxels[0], childVoxels[1], costAux);
		}

	}

	private static AABB[] splitVoxel(final AABB voxel, final int splitAxis,
			final double splitPosition) {
		final AABB left, right;

		switch (splitAxis) {
		case 0:
			left = new AABB(voxel.minX, splitPosition, voxel.minY, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			right = new AABB(splitPosition, voxel.maxX, voxel.minY, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			return new AABB[] { left, right };
		case 1:
			left = new AABB(voxel.minX, voxel.maxX, voxel.minY, splitPosition,
					voxel.minZ, voxel.maxZ);
			right = new AABB(voxel.minX, voxel.maxX, splitPosition, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			return new AABB[] { left, right };
		case 2:
			left = new AABB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
					voxel.minZ, splitPosition);
			right = new AABB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
					splitPosition, voxel.maxZ);
			return new AABB[] { left, right };
		default:
			throw new AssertionError();

		}
	}

	private static class Event implements Comparable<Event> {
		int type;
		GeometricObject obj;
		double position;

		public Event(final int type, final GeometricObject obj,
				final double position) {
			this.type = type;
			this.obj = obj;
			this.position = position;
		}

		@Override
		public int compareTo(Event o) {
			double first = position - o.position;
			if (first < 0) {
				return 1;
			}
			if (first == 0) {
				return o.type - type;
			}

			return -1;
		}
	}

	private static class PlaneInfo {
		double position;
		int axis;

		PlaneInfo(double position, int axis) {
			this.position = position;
			this.axis = axis;
		}
	}

	private class SplitInfo2 {
		double position;
		int axis;
		double cost;

		SplitInfo2(double position, int axis, double cost) {
			this.position = position;
			this.axis = axis;
			this.cost = cost;
		}
	}

	private static SplitInfo findPlane(Collection<GeometricObject> shapes,
			AABB voxel, final double ctrav, final double cisec) {
		if (shapes.isEmpty()) {
			return new SplitInfo(0, 0, Collections.emptyList(),
					Collections.emptyList(), null, null, 0);
		}
		double minCost = Double.MAX_VALUE;
		double minPos = Double.MAX_VALUE;
		boolean minSide = false;
		PlaneInfo minPlane = null;

		for (int axis = 0; axis < 3; axis++) {
			final List<Event> eventList = new ArrayList<>();
			for (final GeometricObject obj : shapes) {
				final PerfectSplits ps = obj.getPerfectSplits().clipToVoxel(
						voxel);
				double min = ps.mins[axis].getElemsAsArray()[axis];
				double max = ps.maxs[axis].getElemsAsArray()[axis];

				// Is planar
				if (min == max) {
					eventList.add(new Event(1, obj, min));
				} else {
					eventList.add(new Event(2, obj, min));
					eventList.add(new Event(0, obj, max));
				}
			}

			int nl = 0;
			int np = 0;
			int nr = shapes.size();

			Event[] orderedEvents = eventList.toArray(new Event[eventList
					.size()]);

			Arrays.sort(orderedEvents);

			for (int i = 0; i < orderedEvents.length; i++) {
				final double p = orderedEvents[i].position;
				int pplus = 0;
				int pminus = 0;
				int pplane = 0;

				while (i < orderedEvents.length
						&& orderedEvents[i].position < p
						&& orderedEvents[i].type == 0) {
					pminus++;
					i++;
				}
				while (i < orderedEvents.length
						&& orderedEvents[i].position == p
						&& orderedEvents[i].type == 1) {
					pplane++;
					i++;
				}
				while (i < orderedEvents.length
						&& orderedEvents[i].position == p
						&& orderedEvents[i].type == 2) {
					pplus++;
					i++;
				}

				np = pplane;
				nr -= pplane;
				nr -= pminus;

				final SAH sah = doSAH(axis, p, voxel, nl, nr, np, ctrav, cisec);

				if (sah.cost < minCost) {
					minPlane = new PlaneInfo(p, axis);
					minCost = sah.cost;
					minSide = sah.left;
				}

				nl += pplus;
				nl += pplane;
				np = 0;
			}
		}

		AABB[] voxels = splitVoxel(voxel, minPlane.axis, minPlane.position);
		TriangleClassification tc = classify(shapes, voxels[0], voxels[1],
				minPlane.axis, minPlane.position);

		if (minSide) {
			tc.tl.addAll(tc.tp);
			return new SplitInfo(minPlane.axis, minPlane.position, tc.tl,
					tc.tr, voxels[0], voxels[1], minCost);
		} else {
			tc.tr.addAll(tc.tp);
			return new SplitInfo(minPlane.axis, minPlane.position, tc.tl,
					tc.tr, voxels[0], voxels[1], minCost);
		}
	}

	private double getMin(final AABB bb, final int axis) {
		switch (axis) {
		case 0:
			return bb.minX;
		case 1:
			return bb.minY;
		case 2:
			return bb.minZ;
		}
		return -1;
	}

	private double getMax(final AABB bb, final int axis) {
		switch (axis) {
		case 0:
			return bb.maxX;
		case 1:
			return bb.maxY;
		case 2:
			return bb.maxZ;
		}
		return -1;
	}

	public boolean intersectionExists(final double tMax, final Ray ray,
			final CustomStack stack, final int top) {
		double tNear = EPSILON;
		double tFar = tMax;

		if (tNear > tFar) {
			return false;
		}

		KdNode node = root;

		final Vector4 source = ray.getSource();
		final Vector4 dir = ray.getDir();
		final double[] sourceAxes = { source.x, source.y, source.z };
		final double[] dirAxes = { dir.x, dir.y, dir.z };

		while (true) {
			// while (!node.isLeaf)
			while (node instanceof KdInternalNode) {
				final KdInternalNode internal = (KdInternalNode) node;
				final int axis = internal.splitAxis;
				KdNode near, far;
				final double dirAxis = dirAxes[axis];
				final double sourceAxis = sourceAxes[axis];

				final double d = (internal.splitPosition - sourceAxis)
						/ dirAxis;
				if (sourceAxis < internal.splitPosition) {
					near = internal.left;
					far = internal.right;
				} else if (sourceAxis > internal.splitPosition) {
					near = internal.right;
					far = internal.left;
				} else if (dirAxis < 0) {
					near = internal.left;
					far = internal.right;
				} else {
					near = internal.right;
					far = internal.left;
				}
				if (d >= tFar || d < EPSILON) {
					node = near;
				} else if (d <= tNear) {
					node = far;
				} else {
					stack.push(far, d, tFar);
					node = near;
					tFar = d;
				}
			}

			final KdLeafNode leaf = (KdLeafNode) node;
			for (GeometricObject ss : leaf.shapes) {
				final RayCollisionInfo collision = ss
						.hit(ray, stack, stack.top);
				if (collision != null && collision.getDistance() < tFar) {
					stack.top = top;
					return true;
				}
			}

			// If stack is empty
			if (stack.top == top) {
				return false;
			}
			StackElement e = stack.pop();
			node = e.node;
			tNear = e.start;
			tFar = e.end;
		}

	}

	// public boolean intersectionExists(final double tMax, final Ray ray,
	// final CustomStack stack) {
	// double tNear;
	// double tFar;
	// KdNode node;
	// KdNode near, far;
	// final double[] raySourceAxes = new double[] { ray.getSource().x,
	// ray.getSource().y, ray.getSource().z };
	// final double[] rayDirAxes = new double[] { ray.getDir().x,
	// ray.getDir().y, ray.getDir().z };
	// stack.push(root, EPSILON, tMax);
	// while (stack.top > 0) {
	// final StackElement se = stack.pop();
	// node = se.node;
	// tNear = se.start;
	// tFar = se.end;
	// while (node instanceof KdInternalNode) {
	// KdInternalNode interiorNode = (KdInternalNode) node;
	// final int axis = interiorNode.splitAxis;
	// final double raySourceAxis = raySourceAxes[axis];
	// final double d = (interiorNode.splitPosition - raySourceAxis)
	// / rayDirAxes[axis];
	// if (raySourceAxis < interiorNode.splitPosition) {
	// near = interiorNode.left;
	// far = interiorNode.right;
	// } else {
	// near = interiorNode.right;
	// far = interiorNode.left;
	// }
	//
	// if (d >= tFar || d < 0) {
	// node = near;
	// } else if (d <= tNear) {
	// node = far;
	// } else {
	// stack.push(far, d, tFar);
	// node = near;
	// tFar = d;
	// }
	// }
	// KdLeafNode leaf = (KdLeafNode) node;
	// for (final SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < tFar) {
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	//
	// public boolean intersectionExists3(final double tMax, final Ray ray) {
	// double tNear;
	// double tFar;
	// KdNode node;
	// KdNode near, far;
	// final Stack<StackElement> stack = new Stack<>();
	// stack.push(new StackElement(root, EPSILON, tMax));
	// while (!stack.isEmpty()) {
	// final StackElement se = stack.pop();
	// node = se.node;
	// tNear = se.start;
	// tFar = se.end;
	// while (!node.isLeaf()) {
	// KdInteriorNode interiorNode = (KdInteriorNode) node;
	// final int axis = interiorNode.splitAxis;
	//
	// final double entryPointDistance;
	// final double exitPointDistance;
	//
	// switch (axis) {
	// case 0:
	// entryPointDistance = (interiorNode.bb.minX - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// exitPointDistance = (interiorNode.bb.maxX - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// break;
	// case 1:
	// entryPointDistance = (interiorNode.bb.minY - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// exitPointDistance = (interiorNode.bb.maxY - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// break;
	// case 2:
	// entryPointDistance = (interiorNode.bb.minZ - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// exitPointDistance = (interiorNode.bb.maxZ - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	// break;
	// default:
	// throw new AssertionError();
	// }
	//
	// final Vector3 entryPoint, exitPoint;
	//
	// if (entryPointDistance < 0) {
	// entryPoint = ray.getSource();
	// } else {
	// entryPoint = ray.getSource().add(
	// ray.getDir().scalarMult(entryPointDistance));
	// }
	//
	// exitPoint = ray.getSource().add(
	// ray.getDir().scalarMult(exitPointDistance));
	//
	// if (getAxis(entryPoint, axis) <= interiorNode.splitPosition) {
	// if (getAxis(exitPoint, axis) < interiorNode.splitPosition) {
	// node = interiorNode.left;
	// continue;
	// }
	// if (getAxis(exitPoint, axis) == interiorNode.splitPosition) {
	// node = interiorNode.right;
	// continue;
	// }
	//
	// stack.push(new StackElement(interiorNode.right, 0, 0));
	// node = interiorNode.left;
	// } else {
	// if (getAxis(exitPoint, axis) > interiorNode.splitPosition) {
	// node = interiorNode.right;
	// continue;
	// }
	// stack.push(new StackElement(interiorNode.left, 0, 0));
	// node = interiorNode.right;
	// }
	// }
	// KdLeafNode leaf = (KdLeafNode) node;
	// if (!leaf.shapes.isEmpty()) {
	// // double minDistance = Double.POSITIVE_INFINITY;
	// for (final SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < tMax) {
	// return true;
	// }
	// }
	// }
	// }
	// return false;
	// }
	//
	// public RayCollisionInfo getCollision3(final double tMax, final Ray ray) {
	// KdNode node;
	//
	// final double entryPointDistance;
	// final double exitPointDistance;
	//
	// final Stack<StackElement> stack = new Stack<>();
	// stack.push(new StackElement(root, ray.getSource().add(
	// ray.getDir().scalarMult(EPSILON)), ray.getDir().scalarMult(
	// Double.MAX_VALUE / 2)));
	// while (!stack.isEmpty()) {
	// final StackElement se = stack.pop();
	// node = se.node;
	// while (!node.isLeaf()) {
	// KdInteriorNode interiorNode = (KdInteriorNode) node;
	// final int axis = interiorNode.splitAxis;
	// final Vector3 entryPoint = se.entryPt, exitPoint = se.exitPt;
	//
	// if (getAxis(entryPoint, axis) <= interiorNode.splitPosition) {
	// if (getAxis(exitPoint, axis) < interiorNode.splitPosition) {
	// node = interiorNode.left;
	// continue;
	// }
	// if (getAxis(exitPoint, axis) == interiorNode.splitPosition) {
	// node = interiorNode.right;
	// continue;
	// }
	//
	// final double d = (interiorNode.splitPosition - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	//
	// stack.push(new StackElement(interiorNode.right, ray
	// .getSource().add(ray.getDir().scalarMult(d)),
	// exitPoint));
	// node = interiorNode.left;
	// } else {
	// if (getAxis(exitPoint, axis) > interiorNode.splitPosition) {
	// node = interiorNode.right;
	// continue;
	// }
	// final double d = (interiorNode.splitPosition - getAxis(
	// ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	//
	// stack.push(new StackElement(interiorNode.left, entryPoint,
	// ray.getSource().add(ray.getDir().scalarMult(d))));
	// node = interiorNode.right;
	// }
	// }
	// KdLeafNode leaf = (KdLeafNode) node;
	// if (!leaf.shapes.isEmpty()) {
	// double minDistance = Double.POSITIVE_INFINITY;
	// SceneShape intersected = null;
	// for (final SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < minDistance) {
	// minDistance = distance;
	// intersected = ss;
	// }
	// }
	//
	// if (minDistance < tMax) {
	// return new RayCollisionInfo(intersected, ray, minDistance);
	// }
	// }
	// }
	// return RayCollisionInfo.noCollision(ray);
	// }

	// public RayCollisionInfo getCollision(final double tMax, final Ray ray,
	// final CustomStack stack) {
	// double tNear;
	// double tFar;
	// KdNode node;
	// KdNode near, far;
	// stack.push(root, EPSILON, tMax);
	// final double[] raySourceAxes = new double[] { ray.getSource().x,
	// ray.getSource().y, ray.getSource().z };
	// final double[] rayDirAxes = new double[] { ray.getDir().x,
	// ray.getDir().y, ray.getDir().z };
	// while (stack.top > 0) {
	// final StackElement se = stack.pop();
	// node = se.node;
	// tNear = se.start;
	// tFar = se.end;
	// while (node instanceof KdInternalNode) {
	// KdInternalNode interiorNode = (KdInternalNode) node;
	// final int axis = interiorNode.splitAxis;
	// final double rayAxis = raySourceAxes[axis];
	// final double d = (interiorNode.splitPosition - rayAxis)
	// / rayDirAxes[axis];
	// if (rayAxis < interiorNode.splitPosition) {
	// near = interiorNode.left;
	// far = interiorNode.right;
	// } else {
	// near = interiorNode.right;
	// far = interiorNode.left;
	// }
	//
	// if (d >= tFar || d < 0) {
	// node = near;
	// } else if (d <= tNear) {
	// node = far;
	// } else {
	// // se.node = far;
	// // se.start = d;
	// // se.end = tFar;
	// stack.push(far, d, tFar);
	// node = near;
	// tFar = d;
	// }
	// }
	// KdLeafNode leaf = (KdLeafNode) node;
	// double minDistance = Double.POSITIVE_INFINITY;
	// SceneShape intersected = null;
	// for (final SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < minDistance) {
	// minDistance = distance;
	// intersected = ss;
	// }
	// }
	//
	// if (minDistance < tFar) {
	// return new RayCollisionInfo(intersected, ray, minDistance);
	// }
	// }
	// return RayCollisionInfo.noCollision(ray);
	// }

	// public RayCollisionInfo getCollision(final double tMax, final Ray ray,
	// final CustomStack stack, boolean debug) {
	// double tNear = EPSILON;
	// double tFar = tMax;
	//
	// // Clip ray to scene bounding box.
	// // final double d1 = ray.getSource().distanceTo(new
	// // Vector33(rootBb.minX, rootBb.minY, rootBb.minZ));
	// // if (d1 > 0) {
	// // tNear = d1;
	// // }
	// //
	// // final double d2 = ray.getSource().distanceTo(new
	// // Vector33(rootBb.maxX, rootBb.maxY, rootBb.maxZ));
	// // if (d2 < tFar) {
	// // tFar = d2;
	// // }
	//
	// if (tNear > tFar) {
	// return RayCollisionInfo.noCollision(ray);
	// }
	//
	// return recTraverse(root, tNear, tFar, ray);
	// }

	// public RayCollisionInfo recTraverse(KdNode node, double tNear, double
	// tFar,
	// Ray ray) {
	// if (node instanceof KdLeafNode) {
	// KdLeafNode leaf = (KdLeafNode) node;
	// SceneShape intersected = null;
	// double minDistance = Double.MAX_VALUE;
	// for (SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < minDistance) {
	// minDistance = distance;
	// intersected = ss;
	// }
	// }
	//
	// if (minDistance == Double.MAX_VALUE) {
	// return RayCollisionInfo.noCollision(ray);
	// }
	//
	// return new RayCollisionInfo(intersected, ray, minDistance);
	// }
	// final KdInternalNode internal = (KdInternalNode) node;
	// final int axis = internal.splitAxis;
	// KdNode near, far;
	// final double dirAxis = getAxis(ray.getDir(), axis);
	// final double sourceAxis = getAxis(ray.getSource(), axis) + EPSILON *
	// Math.signum(dirAxis);
	// final double d = (internal.splitPosition - sourceAxis) / dirAxis;
	// // Caso especial axis == internal.splitPosition.
	// // Habria que avanzar en la direccion del rayo y ver que onda.
	// // Caso especial (2): El rayo va justo adentro del plano de corte.
	// if (sourceAxis < internal.splitPosition) {
	// near = internal.left;
	// far = internal.right;
	// } else if (sourceAxis > internal.splitPosition) {
	// near = internal.right;
	// far = internal.left;
	// } else if (dirAxis < 0) {
	// near = internal.left;
	// far = internal.right;
	// } else {
	// near = internal.right;
	// far = internal.left;
	// }
	// if (d >= tFar || d < 0) {
	// return recTraverse(near, tNear, tFar, ray);
	// } else if (d <= tNear) {
	// return recTraverse(far, tNear, tFar, ray);
	// } else {
	// final RayCollisionInfo rci = recTraverse(near, tNear, d, ray);
	// if (rci.collisionDetected() && rci.getDistance() <= d) {
	// return rci;
	// }
	// return recTraverse(far, d, tFar, ray);
	// }
	//
	// }

	public RayCollisionInfo getCollision(double tMax, Ray ray,
			CustomStack stack, final int top) {
		double tNear = EPSILON;
		double tFar = tMax;

		if (tNear > tFar) {
			return null;
		}

		KdNode node = root;

		final Vector4 source = ray.getSource();
		final Vector4 dir = ray.getDir();
		final double[] sourceAxes = { source.x, source.y, source.z };
		final double[] dirAxes = { dir.x, dir.y, dir.z };

		while (true) {
			// while (!node.isLeaf)
			while (node instanceof KdInternalNode) {
				final KdInternalNode internal = (KdInternalNode) node;
				final int axis = internal.splitAxis;
				KdNode near, far;
				final double dirAxis = dirAxes[axis];
				final double sourceAxis = sourceAxes[axis];

				final double d = (internal.splitPosition - sourceAxis)
						/ dirAxis;
				if (sourceAxis < internal.splitPosition) {
					near = internal.left;
					far = internal.right;
				} else if (sourceAxis > internal.splitPosition) {
					near = internal.right;
					far = internal.left;
				} else if (dirAxis < 0) {
					near = internal.left;
					far = internal.right;
				} else {
					near = internal.right;
					far = internal.left;
				}
				if (d > tFar || d < EPSILON) {
					node = near;
				} else if (d <= tNear) {
					node = far;
				} else {
					stack.push(far, d, tFar);
					node = near;
					tFar = d;
				}
			}

			final KdLeafNode leaf = (KdLeafNode) node;
			RayCollisionInfo minCollision = null;
			double minDistance = Double.MAX_VALUE;
			for (GeometricObject ss : leaf.shapes) {
				final RayCollisionInfo collision = ss
						.hit(ray, stack, stack.top);
				if (collision != null && collision.getDistance() < minDistance) {
					minDistance = collision.getDistance();
					minCollision = collision;
				}
			}
			
			Triangle t;

			if (minCollision != null) {
				if (minDistance <= tFar + EPSILON) {
					stack.top = top;
					return minCollision;
				}
			}

			// If stack is empty
			if (stack.top == top) {
				return null;
			}

			StackElement e = stack.pop();
			node = e.node;
			tNear = e.start;
			tFar = e.end;
		}
	}

}
