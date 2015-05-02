package edu.ar.itba.raytracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.shape.SceneShape.BB;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.shape.Triangle;
import edu.ar.itba.raytracer.vector.Vector4;

public class KdTree {

	public abstract static class KdNode {
	}

	private static class KdInternalNode extends KdNode {
		private final int splitAxis;
		private final double splitPosition;
		private final KdNode left, right;
		private final BB bb;

		public KdInternalNode(final int splitAxis, final double splitPosition,
				final KdNode left, final KdNode right, final BB bb) {
			this.splitAxis = splitAxis;
			this.splitPosition = splitPosition;
			this.left = left;
			this.right = right;
			this.bb = bb;
		}

	}

	private static class KdLeafNode extends KdNode {
		private final Collection<SceneShape> shapes;

		public KdLeafNode(final Collection<SceneShape> shapes) {
			this.shapes = shapes;
		}

	}

	private KdNode root;

	// public void add(final SceneShape shape) {
	// Collection<SceneShape> sl = new ArrayList<>();
	// if (root.left == null) {
	// root.left = new KdNode(1, 0, sl);
	// } else {
	// sl = root.left.shapes;
	// }
	// sl.add(shape);
	// if (root.right == null) {
	// root.right = new KdNode(1, 0, new ArrayList<SceneShape>());
	// }
	// }

	public static class StackElement {
		public KdNode node;
		private int depth;
		public double start;
		public double end;

		private double entryT;
		private double exitT;

		// private Vector3 entryPt;
		// private Vector3 exitPt;

		public StackElement() {

		}

		public StackElement(final KdNode node, final double start,
				final double end) {
			this.node = node;
			this.start = start;
			this.end = end;
		}

		// public StackElement(final KdNode node, final Vector3 entryPt,
		// final Vector3 exitPt) {
		// this.node = node;
		// this.entryPt = entryPt;
		// this.exitPt = exitPt;
		// }
	}

	private static double EPSILON = 0.00001;

	public static KdTree from(Scene scene) {
		final Collection<SceneShape> sceneObjects = scene.getObjects();
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		for (SceneShape shape : sceneObjects) {
			BB bb = shape.getBB();
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
		tree.rootBb = new BB(-size, size, -size, size, -size, size);
		tree.root = recBuild(0, sceneObjects, tree.rootBb);
		System.out.println("LEAVES  " + leaves);
		System.out.println("AMOUNT " + amount / leaves);
		System.out.println("DEPTH " + depth / leaves);
		return tree;
	}

	private BB rootBb;

	private static int leaves;
	private static double amount;
	private static double depth;

	private static KdNode recBuild(final int depth,
			Collection<SceneShape> shapes, BB bb) {
		// if (shapes.size() <= 1 || depth >= 15) {
		// leaves++;
		// amount += shapes.size();
		// KdTree.depth += depth;
		// return new KdLeafNode(shapes);
		// }
		//
		// final int axis = depth % 3;
		// final double splitPosition;
		// final BB leftBB, rightBB;
		// switch (axis) {
		// case 0:
		// splitPosition = (bb.minX + bb.maxX) / 2;
		// leftBB = new BB(bb.minX, splitPosition, bb.minY, bb.maxY, bb.minZ,
		// bb.maxZ);
		// rightBB = new BB(splitPosition, bb.maxX, bb.minY, bb.maxY, bb.minZ,
		// bb.maxZ);
		// break;
		// case 1:
		// splitPosition = (bb.minY + bb.maxY) / 2;
		// leftBB = new BB(bb.minX, bb.maxX, bb.minY, splitPosition, bb.minZ,
		// bb.maxZ);
		// rightBB = new BB(bb.minX, bb.maxX, splitPosition, bb.maxY, bb.minZ,
		// bb.maxZ);
		// break;
		// case 2:
		// splitPosition = (bb.minZ + bb.maxZ) / 2;
		// leftBB = new BB(bb.minX, bb.maxX, bb.minY, bb.maxY, bb.minZ,
		// splitPosition);
		// rightBB = new BB(bb.minX, bb.maxX, bb.minY, bb.maxY, splitPosition,
		// bb.maxZ);
		// break;
		// default:
		// throw new AssertionError();
		// }
		// final Collection<SceneShape> leftShapes = new ArrayList<>();
		// final Collection<SceneShape> rightShapes = new ArrayList<>();
		//
		// for (SceneShape s : shapes) {
		// BB sbb = s.getBB();
		// switch (axis) {
		// case 0:
		// if (splitPosition < sbb.maxX && splitPosition > sbb.minX) {
		// leftShapes.add(s);
		// rightShapes.add(s);
		// } else if (splitPosition >= sbb.maxX) {
		// leftShapes.add(s);
		// } else {
		// rightShapes.add(s);
		// }
		// break;
		// case 1:
		// if (splitPosition < sbb.maxY && splitPosition > sbb.minY) {
		// leftShapes.add(s);
		// rightShapes.add(s);
		// } else if (splitPosition >= sbb.maxY) {
		// leftShapes.add(s);
		// } else {
		// rightShapes.add(s);
		// }
		// break;
		// case 2:
		// if (splitPosition < sbb.maxZ && splitPosition > sbb.minZ) {
		// leftShapes.add(s);
		// rightShapes.add(s);
		// } else if (splitPosition >= sbb.maxZ) {
		// leftShapes.add(s);
		// } else {
		// rightShapes.add(s);
		// }
		// break;
		// }
		// }

		SplitInfo si = naiveSah(shapes, bb, 1, 1);

		if (shapes.isEmpty() || si.minCost >= shapes.size() * 1) {
			leaves++;
			amount += shapes.size();
			KdTree.depth += depth;
			return new KdLeafNode(shapes);
		}

		return new KdInternalNode(si.axis, si.splitPosition, recBuild(
				depth + 1, si.tl, si.leftVoxel), recBuild(depth + 1, si.tr,
				si.rightVoxel), bb);

		// return new KdInternalNode(depth % 3, splitPosition, recBuild(depth +
		// 1,
		// leftShapes, leftBB), recBuild(depth + 1, rightShapes, rightBB),
		// bb);

	}

	private static class TriangleClassification {
		Collection<SceneShape> tl, tr, tp;

		public TriangleClassification(final Collection<SceneShape> tl,
				final Collection<SceneShape> tr, final Collection<SceneShape> tp) {
			this.tl = tl;
			this.tr = tr;
			this.tp = tp;
		}
	}

	@SuppressWarnings("unchecked")
	private static TriangleClassification classify(
			Collection<SceneShape> shapes, BB leftVoxel, BB rightVoxel,
			int splitAxis, double splitPosition) {
		final Collection<SceneShape> tl = new ArrayList<>();
		final Collection<SceneShape> tr = new ArrayList<>();
		final Collection<SceneShape> tp = new ArrayList<>();

		for (SceneShape shape : shapes) {
			Triangle triangle = (Triangle) shape;
			List<Vector4> vertexes = triangle.getVertexes();
			// Vector33[] vertexes = triangle.getExtremePoints();
			boolean isOnPlane = true;

			for (Vector4 vertex : vertexes) {
				if (!(vertex.getElemsAsArray()[splitAxis] <= splitPosition + 0.00001 && vertex
						.getElemsAsArray()[splitAxis] >= splitPosition - 0.00001)) {
					isOnPlane = false;
					break;
				}
			}
			if (isOnPlane) {
				tp.add(triangle);
				continue;
			}

			for (Vector4 vertex : vertexes) {
				if (vertex.getElemsAsArray()[splitAxis] < splitPosition) {
					tl.add(triangle);
					break;
				}
			}

			for (Vector4 vertex : vertexes) {
				if (vertex.getElemsAsArray()[splitAxis] > splitPosition) {
					tr.add(triangle);
					break;
				}
			}
		}

		return new TriangleClassification(tl, tr, tp);
	}

	private static class SplitInfo {
		int axis;
		double splitPosition;
		Collection<SceneShape> tl;
		Collection<SceneShape> tr;
		BB leftVoxel;
		BB rightVoxel;
		double minCost;

		public SplitInfo(int axis, double splitPosition,
				Collection<SceneShape> tl, Collection<SceneShape> tr,
				BB leftVoxel, BB rightVoxel, double minCost) {
			this.axis = axis;
			this.splitPosition = splitPosition;
			this.tl = tl;
			this.tr = tr;
			this.leftVoxel = leftVoxel;
			this.rightVoxel = rightVoxel;
			this.minCost = minCost;
		}
	}

	private static SplitInfo naiveSah(Collection<SceneShape> shapes, BB voxel,
			final double ctrav, final double cisec) {
		Boolean toLeft = null;
		double costAux = Double.MAX_VALUE;
		double bestSplitPosition = 0;
		TriangleClassification triangleCl = null;
		int bestAxis = 0;
		BB[] childVoxels = null;

		for (final SceneShape ss : shapes) {
			// Init vars
			final Vector4[] splits = ((Triangle) ss).getPerfectSplits()
					.clipToVoxel(voxel).getAllExtremePoints();

			for (int i = 0; i < splits.length; i++) {
				final int axis = i / 2;
				final double splitPosition = splits[i].getElemsAsArray()[axis];

				final BB[] voxels = splitVoxel(voxel, axis, splitPosition);

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

	private static BB[] splitVoxel(final BB voxel, final int splitAxis,
			final double splitPosition) {
		final BB left, right;

		switch (splitAxis) {
		case 0:
			left = new BB(voxel.minX, splitPosition, voxel.minY, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			right = new BB(splitPosition, voxel.maxX, voxel.minY, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			return new BB[] { left, right };
		case 1:
			left = new BB(voxel.minX, voxel.maxX, voxel.minY, splitPosition,
					voxel.minZ, voxel.maxZ);
			right = new BB(voxel.minX, voxel.maxX, splitPosition, voxel.maxY,
					voxel.minZ, voxel.maxZ);
			return new BB[] { left, right };
		case 2:
			left = new BB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
					voxel.minZ, splitPosition);
			right = new BB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
					splitPosition, voxel.maxZ);
			return new BB[] { left, right };
		default:
			throw new AssertionError();

		}
	}

	public boolean intersectionExists(final double tMax, final Ray ray,
			final CustomStack stack) {
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
			SceneShape intersected = null;
			double minDistance = Double.MAX_VALUE;
			for (SceneShape ss : leaf.shapes) {
				final double distance = ss.intersect(ray);
				if (distance != -1 && distance < tFar) {
					return true;
				}
			}

			// If stack is empty
			if (stack.top == 0) {
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

	public RayCollisionInfo getCollision(double tMax, Ray ray, CustomStack stack) {
		double tNear = EPSILON;
		double tFar = tMax;

		if (tNear > tFar) {
			return RayCollisionInfo.noCollision(ray);
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
			SceneShape intersected = null;
			double minDistance = Double.MAX_VALUE;
			for (SceneShape ss : leaf.shapes) {
				final double distance = ss.intersect(ray);
				if (distance != -1 && distance < minDistance) {
					minDistance = distance;
					intersected = ss;
				}
			}

			if (minDistance != Double.MAX_VALUE) {
				if (minDistance <= tFar) {
					return new RayCollisionInfo(intersected, ray, minDistance);
				}
			}

			// If stack is empty
			if (stack.top == 0) {
				return RayCollisionInfo.noCollision(ray);
			}
			StackElement e = stack.pop();
			node = e.node;
			tNear = e.start;
			tFar = e.end;
		}
	}

	// public RayCollisionInfo getCollision(double tMax, Ray[] ray,
	// CustomStack stack) {
	// double tNear0 = EPSILON;
	// double tFar0 = tMax;
	// double tNear1 = EPSILON;
	// double tFar1 = tMax;
	// double tNear2 = EPSILON;
	// double tFar2 = tMax;
	// double tNear3 = EPSILON;
	// double tFar3 = tMax;
	//
	// KdNode node = root;
	//
	// while (true) {
	// // while (!node.isLeaf)
	// while (node instanceof KdInternalNode) {
	// final KdInternalNode internal = (KdInternalNode) node;
	// final int axis = internal.splitAxis;
	// KdNode near, far;
	// final double dirAxis0 = getAxis(ray[0].getDir(), axis);
	// final double sourceAxis0 = getAxis(ray[0].getSource(), axis);
	// final double dirAxis1 = getAxis(ray[1].getDir(), axis);
	// final double sourceAxis1 = getAxis(ray[1].getSource(), axis);
	// final double dirAxis2 = getAxis(ray[2].getDir(), axis);
	// final double sourceAxis2 = getAxis(ray[2].getSource(), axis);
	// final double dirAxis3 = getAxis(ray[3].getDir(), axis);
	// final double sourceAxis3 = getAxis(ray[3].getSource(), axis);
	//
	// final double d0 = (internal.splitPosition - sourceAxis0)
	// / dirAxis0;
	// final double d1 = (internal.splitPosition - sourceAxis1)
	// / dirAxis1;
	// final double d2 = (internal.splitPosition - sourceAxis2)
	// / dirAxis2;
	// final double d3 = (internal.splitPosition - sourceAxis3)
	// / dirAxis3;
	//
	// final boolean active0 = tNear0 < tFar0;
	// final boolean active1 = tNear1 < tFar1;
	// final boolean active2 = tNear2 < tFar2;
	// final boolean active3 = tNear3 < tFar3;
	//
	// if (sourceAxis0 < internal.splitPosition) {
	// near = internal.left;
	// far = internal.right;
	// } else if (sourceAxis0 > internal.splitPosition) {
	// near = internal.right;
	// far = internal.left;
	// } else if (dirAxis0 < 0) {
	// near = internal.left;
	// far = internal.right;
	// } else {
	// near = internal.right;
	// far = internal.left;
	// }
	//
	// if ((d0 >= tFar0 || !active0 || d0 < EPSILON)
	// || (d1 >= tFar1 || !active1 || d1 < EPSILON)
	// || (d2 >= tFar2 || !active2 || d2 < EPSILON)
	// || (d3 >= tFar3 || !active3 || d3 < EPSILON)) {
	// node = near;
	// } else if ((d0 <= tNear0 || !active0)
	// || (d1 <= tNear1 || !active1)
	// || (d2 <= tNear2 || !active2)
	// || (d3 <= tNear3 || !active3)) {
	// node = far;
	// } else {
	// stack.push(far, d3, tFar3);
	// stack.push(far, d2, tFar2);
	// stack.push(far, d1, tFar1);
	// stack.push(far, d0, tFar0);
	//
	// node = near;
	// tFar0 = Math.min(d0, tNear0);
	// tFar1 = Math.min(d0, tNear1);
	// tFar2 = Math.min(d0, tNear2);
	// tFar3 = Math.min(d0, tNear3);
	// }
	// }
	//
	// final KdLeafNode leaf = (KdLeafNode) node;
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
	// if (minDistance != Double.MAX_VALUE) {
	// if (minDistance <= tFar) {
	// return new RayCollisionInfo(intersected, ray, minDistance);
	// }
	// }
	//
	// // If stack is empty
	// if (stack.top == 0) {
	// return RayCollisionInfo.noCollision(ray[0]);
	// }
	// StackElement e = stack.pop();
	// node = e.node;
	// tNear0 = e.start;
	// tFar0 = e.end;
	// e = stack.pop();
	// tNear1 = e.start;
	// tFar1 = e.end;
	// e = stack.pop();
	// tNear2 = e.start;
	// tFar2 = e.end;
	// e = stack.pop();
	// tNear3 = e.start;
	// tFar3 = e.end;
	// }
	// }

	// Algorithm based on http://dcgi.felk.cvut.cz/home/havran/DISSVH/dissvh.pdf
	// public boolean intersectionExists2(final double tMax, final Ray ray) {
	// double a = EPSILON, b = tMax, t;
	// int depth;
	// KdNode currentNode, farNode;
	// Vector3 entryPt;
	// double entryT = a;
	// Vector3 exitPt;
	// double exitT = b;
	//
	// if (a >= 0.0) {
	// entryPt = ray.getSource().add(ray.getDir().scalarMult(a));
	// } else {
	// entryPt = ray.getSource();
	// }
	//
	// exitPt = ray.getSource().add(ray.getDir().scalarMult(b));
	//
	// depth = 0;
	//
	// Stack<StackElement> stack = new Stack<>();
	//
	// StackElement first = new StackElement();
	// first.depth = 0;
	// first.node = root;
	// first.entryT = a;
	// first.exitT = b;
	// first.entryPt = entryPt;
	// first.exitPt = exitPt;
	//
	// stack.push(first);
	//
	// while (!stack.isEmpty()) {
	// StackElement se = stack.pop();
	// depth = se.depth;
	// currentNode = se.node;
	// entryT = se.entryT;
	// exitT = se.exitT;
	// entryPt = se.entryPt;
	// exitPt = se.exitPt;
	//
	// while (!currentNode.isLeaf()) {
	// KdInteriorNode interiorNode = (KdInteriorNode) currentNode;
	// double splitPosition = interiorNode.splitPosition;
	// int axis = depth % 3;
	//
	// if (getAxis(entryPt, axis) <= splitPosition) {
	// if (getAxis(exitPt, axis) <= splitPosition) {
	// depth++;
	// currentNode = interiorNode.left;
	// continue;
	// }
	// if (getAxis(exitPt, axis) == splitPosition) {
	// depth++;
	// currentNode = interiorNode.right;
	// continue;
	// }
	//
	// farNode = interiorNode.left;
	// currentNode = interiorNode.right;
	// depth++;
	// } else {
	// if (splitPosition < getAxis(exitPt, axis)) {
	// currentNode = interiorNode.right;
	// depth++;
	// continue;
	// }
	// depth++;
	// farNode = interiorNode.left;
	// currentNode = interiorNode.right;
	// }
	//
	// t = (splitPosition - getAxis(ray.getSource(), axis))
	// / getAxis(ray.getDir(), axis);
	//
	// int nextAxis = (axis + 1) % 3;
	// int prevAxis = (axis + 2) % 3;
	//
	// StackElement push = new StackElement();
	//
	// push.node = farNode;
	// push.depth = depth + 1;
	// push.entryPt = exitPt;
	// push.entryT = exitT;
	// push.exitT = t;
	// switch (axis) {
	// case 0:
	// se.exitPt = new Vector3(splitPosition, getAxis(
	// ray.getSource(), nextAxis)
	// + t * getAxis(ray.getDir(), nextAxis), getAxis(
	// ray.getSource(), prevAxis)
	// + t * getAxis(ray.getDir(), prevAxis));
	// break;
	// case 1:
	// se.exitPt = new Vector3(getAxis(ray.getSource(), prevAxis)
	// + t * getAxis(ray.getDir(), prevAxis),
	// splitPosition, getAxis(ray.getSource(), nextAxis)
	// + t * getAxis(ray.getDir(), nextAxis));
	// break;
	// case 2:
	// se.exitPt = new Vector3(getAxis(ray.getSource(), nextAxis)
	// + t * getAxis(ray.getDir(), nextAxis), getAxis(
	// ray.getSource(), prevAxis)
	// + t * getAxis(ray.getDir(), prevAxis),
	// splitPosition);
	// break;
	// }
	// }
	//
	// KdLeafNode leaf = (KdLeafNode) currentNode;
	// if (!leaf.shapes.isEmpty()) {
	// // double minDistance = Double.POSITIVE_INFINITY;
	// for (final SceneShape ss : leaf.shapes) {
	// final double distance = ss.intersect(ray);
	// if (distance != -1 && distance < exitT) {
	// return true;
	// }
	// }
	// }
	// }
	//
	// return false;
	// }

	private static double getAxis(final Vector4 vector, final int axis) {
		switch (axis) {
		case 0:
			return vector.x;
		case 1:
			return vector.y;
		case 2:
			return vector.z;
		}

		return -1;
	}
}
