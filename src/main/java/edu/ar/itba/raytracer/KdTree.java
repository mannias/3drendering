package edu.ar.itba.raytracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.shape.GeometricObject.AABB;
import edu.ar.itba.raytracer.shape.GeometricObject.PerfectSplits;
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

	private static class Split {
		private final int axis;
		private final double position;
		private boolean left;
		private double minCost;

		Split(final int axis, final double position) {
			this.axis = axis;
			this.position = position;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Split)) {
				return false;
			}
			final Split o = (Split) obj;

			return axis == o.axis && position > o.position - EPSILON
					&& position < o.position + EPSILON;
		}

		@Override
		public int hashCode() {
			return Objects.hash(axis, position);
		}
	}

	private static double C(final double pl, final double pr, final int nl,
			final int nr, final double kt, final double ki) {
		final double red = .8;
		return red * (kt + ki * (pl * nl + pr * nr));
	}

	private static class SAHResult {
		private final double c;
		private final boolean left;

		SAHResult(final double c, final boolean left) {
			this.c = c;
			this.left = left;
		}
	}

	private static AABB[] splitBox(final AABB voxel, final Split p) {
		switch (p.axis) {
		case 2:
			return new AABB[] {
					new AABB(voxel.minX, p.position, voxel.minY, voxel.maxY,
							voxel.minZ, voxel.maxZ),
					new AABB(p.position, voxel.maxX, voxel.minY, voxel.maxY,
							voxel.minZ, voxel.maxZ) };
		case 1:
			return new AABB[] {
					new AABB(voxel.minX, voxel.maxX, voxel.minY, p.position,
							voxel.minZ, voxel.maxZ),
					new AABB(voxel.minX, voxel.maxX, p.position, voxel.maxY,
							voxel.minZ, voxel.maxZ) };
		case 0:
			return new AABB[] {
					new AABB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
							voxel.minZ, p.position),
					new AABB(voxel.minX, voxel.maxX, voxel.minY, voxel.maxY,
							p.position, voxel.maxZ) };
		default:
			return null;
		}
	}

	private static SAHResult SAH(final Split p, final AABB voxel, final int nl,
			final int nr, final int np, final double kt, final double ki) {
		final AABB[] children = splitBox(voxel, p);
		final AABB vl = children[0];
		final AABB vr = children[1];

		// Esto podria dar 0, en cuyo caso el resultado de la cuenta daria NaN.
		// Como NaN hace que cualquier evaluacion de false, no nos molestamos en
		// chequear el caso especial.
		final double sav = voxel.getSurfaceArea();
		final double pl = vl.getSurfaceArea() / sav;
		final double pr = vr.getSurfaceArea() / sav;

		final double cpl = C(pl, pr, nl + np, nr, kt, ki);
		final double cpr = C(pl, pr, nl, nr + np, kt, ki);

		if (cpl < cpr) {
			return new SAHResult(cpl, true);
		} else {
			return new SAHResult(cpr, false);
		}
	}

	private static Split findPlane(final int numberTris, final AABB voxel,
			final Event[] events, final double kt, final double ki) {
		final int eventsSize = events.length;
		Split minP = null;
		double minC = Double.MAX_VALUE;

		int nlx = 0;
		int npx = 0;
		int nrx = numberTris;
		int nly = 0;
		int npy = 0;
		int nry = numberTris;
		int nlz = 0;
		int npz = 0;
		int nrz = numberTris;

		for (int i = 0; i < eventsSize;) {

			int pplusx = 0;
			int pminusx = 0;
			int pplanex = 0;
			int pplusy = 0;
			int pminusy = 0;
			int pplaney = 0;
			int pplusz = 0;
			int pminusz = 0;
			int pplanez = 0;

			final Split p = new Split(events[i].axis, events[i].position);

			while (i < eventsSize && events[i].axis == p.axis
					&& events[i].position == p.position && events[i].type == 0) {
				i++;
				switch (p.axis) {
				case 0:
					pminusx++;
					break;
				case 1:
					pminusy++;
					break;
				case 2:
					pminusz++;
					break;
				}
			}

			while (i < eventsSize && events[i].axis == p.axis
					&& events[i].position == p.position && events[i].type == 1) {
				i++;
				switch (p.axis) {
				case 0:
					pplanex++;
					break;
				case 1:
					pplaney++;
					break;
				case 2:
					pplanez++;
					break;
				}
			}

			while (i < eventsSize && events[i].axis == p.axis
					&& events[i].position == p.position && events[i].type == 2) {
				i++;
				switch (p.axis) {
				case 0:
					pplusx++;
					break;
				case 1:
					pplusy++;
					break;
				case 2:
					pplusz++;
					break;
				}
			}

			final SAHResult sah;
			switch (p.axis) {
			case 0:
				npx = pplanex;
				nrx -= pplanex;
				nrx -= pminusx;
				if (nrx < 0) {
					System.out.println("X " + nrx);
				}
				sah = SAH(p, voxel, nlx, nrx, npx, kt, ki);
				nlx += pplusx;
				nlx += pplanex;
				npx = 0;
				break;
			case 1:
				npy = pplaney;
				nry -= pplaney;
				nry -= pminusy;
				if (nry < 0) {
					System.out.println("Y " + nry);
				}
				sah = SAH(p, voxel, nly, nry, npy, kt, ki);
				nly += pplusy;
				nly += pplaney;
				npy = 0;
				break;
			case 2:
				npz = pplanez;
				nrz -= pplanez;
				nrz -= pminusz;
				if (nrz < 0) {
					System.out.println("Z " + nrz);
				}
				sah = SAH(p, voxel, nlz, nrz, npz, kt, ki);
				nlz += pplusz;
				nlz += pplanez;
				npz = 0;
				break;
			default:
				throw new RuntimeException();
			}

			if (sah.c < minC) {
				minC = sah.c;
				minP = p;
				minP.left = sah.left;
				minP.minCost = sah.c;
			}
		}

		if (minP == null && numberTris != 0) {
			System.out.println("WTF");
		}

		return minP;
	}

	public static KdTree from(Scene scene) {
		return from(scene.getGObjects());
	}

	private static List<Event> generateEvents(final GeometricObject obj,
			final AABB voxel) {
		final List<Event> eventList = new ArrayList<>();
		for (int axis = 0; axis < 3; axis++) {
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
			final PerfectSplits ps = obj.getPerfectSplits().clipToVoxel(voxel);
			double min = ps.mins[axis].getElemsAsArray()[axis2];
			double max = ps.maxs[axis].getElemsAsArray()[axis2];

			// Is planar
			if (min == max) {
				eventList.add(new Event(axis, 1, obj, min));
			} else {
				eventList.add(new Event(axis, 2, obj, min));
				eventList.add(new Event(axis, 0, obj, max));
			}
		}

		return eventList;

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

		final List<Event> eventList = new ArrayList<>();
		for (final GeometricObject obj : objs) {
			eventList.addAll(generateEvents(obj, tree.rootBb));
		}

		Event[] orderedEvents = eventList.toArray(new Event[eventList.size()]);

		Arrays.sort(orderedEvents);

		final long start = System.currentTimeMillis();
		tree.root = recBuild(0, objs, tree.rootBb, orderedEvents,
				new HashSet<>());
		System.out.println("Tree built in "
				+ (System.currentTimeMillis() - start) + " milliseconds.");
		return tree;
	}

	private AABB rootBb;

	private static final int MAX_DEPTH = 50;

	private static KdNode recBuild(final int depth,
			Collection<GeometricObject> shapes, AABB bb, final Event[] events,
			final Set<Split> prevSplits) {

		final double cisec = 1.5;
		final double ctrav = 1;

		Split p = findPlane(shapes.size(), bb, events, ctrav, cisec);

		if (shapes.isEmpty() || depth > MAX_DEPTH || p == null
				|| p.minCost >= shapes.size() * cisec || prevSplits.contains(p)) {
			return new KdLeafNode(shapes);
		}

		final AABB[] voxels = splitBox(bb, p);

		final TriangleClassification tc = classify(shapes, events, p);

		final EventClassification ec = splice(events, tc, voxels[0], voxels[1]);

		final Event[] ebl = ec.ebl.toArray(new Event[] {});
		final Event[] ebr = ec.ebr.toArray(new Event[] {});

		Arrays.sort(ebl);
		Arrays.sort(ebr);

		final List<Event> el = mergeEvents(Arrays.asList(ebl), ec.elo);
		final List<Event> er = mergeEvents(Arrays.asList(ebr), ec.ero);

		final List<GeometricObject> tl = new ArrayList<>();
		final List<GeometricObject> tr = new ArrayList<>();

		for (final GeometricObject go : shapes) {
			switch (tc.sides.get(go)) {
			case 1:
				tl.add(go);
				break;
			case 2:
				tr.add(go);
				break;
			case 3:
				tl.add(go);
				tr.add(go);
				break;
			}
		}

		final Set<Split> newSplits = new HashSet<>(prevSplits);
		newSplits.add(p);

		return new KdInternalNode(p.axis, p.position, recBuild(depth + 1,
				tc.tl, voxels[0], el.toArray(new Event[] {}), newSplits),
				recBuild(depth + 1, tc.tr, voxels[1],
						er.toArray(new Event[] {}), newSplits));

	}

	private static List<Event> mergeEvents(final List<Event> e1,
			final List<Event> e2) {
		final Iterator<Event> it1 = e1.iterator();
		final Iterator<Event> it2 = e2.iterator();
		final List<Event> merged = new ArrayList<>();

		if (!it1.hasNext()) {
			merged.addAll(e2);
			return merged;
		}

		if (!it2.hasNext()) {
			merged.addAll(e1);
			return merged;
		}

		Event ev1 = it1.next();
		Event ev2 = it2.next();

		do {
			if (ev1.compareTo(ev2) < 0) {
				merged.add(ev1);
				if (it1.hasNext()) {
					ev1 = it1.next();
				} else {
					merged.add(ev2);
					break;
				}
			} else {
				merged.add(ev2);
				if (it2.hasNext()) {
					ev2 = it2.next();
				} else {
					merged.add(ev1);
					break;
				}
			}
		} while (it1.hasNext() && it2.hasNext());

		while (it1.hasNext()) {
			merged.add(it1.next());
		}

		while (it2.hasNext()) {
			merged.add(it2.next());
		}

		return merged;
	}

	private static class TriangleClassification {
		final Collection<GeometricObject> tl, tr;
		final Map<GeometricObject, Integer> sides;

		public TriangleClassification(final Collection<GeometricObject> tl,
				final Collection<GeometricObject> tr,
				final Map<GeometricObject, Integer> sides) {
			this.tl = tl;
			this.tr = tr;
			this.sides = sides;
		}
	}

	private static TriangleClassification classify(
			Collection<GeometricObject> shapes, final Event[] events,
			final Split split) {
		// 1: Left
		// 2: Right
		// 3: Both
		Map<GeometricObject, Integer> sides = new HashMap<>();

		for (final GeometricObject go : shapes) {
			sides.put(go, 3);
		}

		for (final Event e : events) {
			if (e.type == 0 && e.axis == split.axis
					&& e.position <= split.position) {
				sides.put(e.obj, 1);
			} else if (e.type == 2 && e.axis == split.axis
					&& e.position >= split.position) {
				sides.put(e.obj, 2);
			} else if (e.type == 1 && e.axis == split.axis) {
				if (e.position < split.position
						|| (e.position == split.position && split.left)) {
					sides.put(e.obj, 1);
				} else if (e.position > split.position
						|| (e.position == split.position && !split.left)) {
					sides.put(e.obj, 2);
				}

			}
		}

		final Collection<GeometricObject> tl = new ArrayList<>();
		final Collection<GeometricObject> tr = new ArrayList<>();

		for (final Entry<GeometricObject, Integer> e : sides.entrySet()) {
			switch (e.getValue()) {
			case 1:
				tl.add(e.getKey());
				break;
			case 2:
				tr.add(e.getKey());
				break;
			case 3:
				// tp.add(e.getKey());
				tl.add(e.getKey());
				tr.add(e.getKey());
				break;
			}
		}

		return new TriangleClassification(tl, tr, sides);
	}

	private static class EventClassification {
		final List<Event> elo;
		final List<Event> ero;
		final List<Event> ebl;
		final List<Event> ebr;

		public EventClassification(final List<Event> elo,
				final List<Event> ero, final List<Event> ebl,
				final List<Event> ebr) {
			this.elo = elo;
			this.ero = ero;
			this.ebl = ebl;
			this.ebr = ebr;
		}
	}

	private static EventClassification splice(final Event[] events,
			final TriangleClassification tc, final AABB vl, final AABB vr) {
		final List<Event> elo = new ArrayList<Event>();
		final List<Event> ero = new ArrayList<Event>();
		final List<Event> ebl = new ArrayList<Event>();
		final List<Event> ebr = new ArrayList<Event>();

		for (final Event e : events) {
			switch (tc.sides.get(e.obj)) {
			case 1:
				elo.add(e);
				break;
			case 2:
				ero.add(e);
				break;
			}
		}

		for (final Entry<GeometricObject, Integer> entry : tc.sides.entrySet()) {
			if (entry.getValue() == 3) {
				final GeometricObject obj = entry.getKey();
				ebl.addAll(generateEvents(obj, vl));
				ebr.addAll(generateEvents(obj, vr));
			}
		}

		return new EventClassification(elo, ero, ebl, ebr);
	}

	private static class Event implements Comparable<Event> {
		@Override
		public String toString() {
			return "Event [axis=" + axis + ", type=" + type + ", position="
					+ position + "]";
		}

		final int axis;
		final int type;
		final GeometricObject obj;
		final double position;

		public Event(final int axis, final int type, final GeometricObject obj,
				final double position) {
			this.axis = axis;
			this.type = type;
			this.obj = obj;
			this.position = position;
		}

		@Override
		public int compareTo(Event o) {
			final double first = position - o.position;
			if (first < 0) {
				return -1;
			}

			if (first == 0) {
				if (axis == o.axis) {
					return type - o.type;
				}
				return axis - o.axis;
			}

			return 1;
		}
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

		int[] other = new int[] { 2, 1, 0 };

		while (true) {
			// while (!node.isLeaf)
			while (node instanceof KdInternalNode) {
				final KdInternalNode internal = (KdInternalNode) node;
				final int axis = internal.splitAxis;
				KdNode near, far;
				final double dirAxis = dirAxes[other[axis]];
				final double sourceAxis = sourceAxes[other[axis]];

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

		int[] other = new int[] { 2, 1, 0 };

		while (true) {
			while (node instanceof KdInternalNode) {
				final KdInternalNode internal = (KdInternalNode) node;
				final int axis = internal.splitAxis;
				KdNode near, far;
				final double dirAxis = dirAxes[other[axis]];
				final double sourceAxis = sourceAxes[other[axis]];

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
