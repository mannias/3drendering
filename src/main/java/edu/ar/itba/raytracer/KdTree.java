package edu.ar.itba.raytracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.vector.Vector3;

public class KdTree {

	private static class KdNode {
		private final int axis;
		private final double value;
		private final Collection<SceneShape> shapes;
		private final boolean isLeaf;
		private KdNode left, right;

		public KdNode(final int axis, final double value,
				final Collection<SceneShape> shapes) {
			this.axis = axis;
			this.value = value;
			this.shapes = shapes;
			isLeaf = shapes != null;
		}

	}

	private KdNode root = new KdNode(0, 0, null);

	public void add(final SceneShape shape) {
		Collection<SceneShape> sl = new ArrayList<>();
		if (root.left == null) {
			root.left = new KdNode(1, 0, sl);
		} else {
			sl = root.left.shapes;
		}
		sl.add(shape);
		if (root.right == null) {
			root.right = new KdNode(1, 0, new ArrayList<SceneShape>());
		}
	}

	private static class StackElement {
		private final KdNode node;
		private final double start;
		private final double end;

		public StackElement(final KdNode node, final double start,
				final double end) {
			this.node = node;
			this.start = start;
			this.end = end;
		}
	}

	private static double EPSILON = 0.00001;

	public boolean intersectionExists(final double tMax, final Ray ray) {
		double tNear = EPSILON;
		double tFar = tMax;
		KdNode node = root;
		final Stack<StackElement> stack = new Stack<>();
		while (true) {
			while (!node.isLeaf) {
				final int axis = node.axis;
				final double d = (node.value - getAxis(ray.getSource(), axis))
						/ getAxis(ray.getDir(), axis);
				if (d <= tNear) {
					node = node.right;
				} else if (d >= tFar) {
					node = node.left;
				} else {
					stack.push(new StackElement(node.right, d, tFar));
					node = node.left;
					tFar = d;
				}
			}
			double minDistance = -1;
			for (final SceneShape ss : node.shapes) {
				final double distance = ss.intersect(ray);
				if (distance != -1
						&& (distance < minDistance || minDistance == -1)) {
					minDistance = distance;
				}
			}
			if (minDistance == -1) {
				if (stack.isEmpty()) {
					return false;
				}

				final StackElement se = stack.pop();
				node = se.node;
				tNear = se.start;
				tFar = se.end;
			} else if (minDistance < tFar) {
				return true;
			} else {
				return false;
			}
		}
	}

	private static double getAxis(final Vector3 vector, final int axis) {
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
