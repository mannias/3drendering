package edu.ar.itba.raytracer.shape;

import java.util.concurrent.atomic.AtomicInteger;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;
//
//public class Plane extends SceneShape {
//
//	protected static final double EPSILON = 0.000001f;
//
//	private final double d;
//	private final Vector4 normal;
//
//    public Plane(final Transform transform, final ShapeProperties properties, final Vector4 normal){
//        super(transform, properties);
//
//        this.normal = normal;
//        d = -normal.dot(transform.getPosition());
//    }
//
//	public Plane(final Transform transform, final ShapeProperties properties) {
//		this(transform,properties,new Vector4(0,1,0,0));
//	}
//
////	public Plane(final Transform transform) {
////		this(transform, new ShapeProperties());
////	}
//
//	public Plane(final ShapeProperties properties) {
//		this(new Transform(), properties);
//	}
//
////	public Plane() {
////		this(new Transform(), new ShapeProperties());
////	}
//
//	public static final AtomicInteger intersections = new AtomicInteger();
//	public static final AtomicInteger calls = new AtomicInteger();
//
//	@Override
//	public double intersect(final Ray ray) {
//		// calls.incrementAndGet();
//		final double vd = normal.dot(ray.getDir());
//		if (vd == 0) {
//			// No intersection.
//			return -1;
//		}
//
//		final double v0 = -(normal.dot(ray.getSource()) + d);
//		final double t = v0 / vd;
//		if (t < 0) {
//			// Intersection happens behind source.
//			return -1;
//		}
//
//		// intersections.incrementAndGet();
//		return t;
//	}
//
//    @Override
//    public boolean intersectionExists(Ray ray) {
//        return false;
//    }
//
//    @Override
//	public Vector4 normal(final Vector4 point) {
//		return normal;
//	}
//
//	public boolean containsPoint(final Vector4 point) {
//		final double planeEq = normal.x * point.x + normal.y * point.y
//				+ normal.z * point.z + d;
//
//		return planeEq < EPSILON && planeEq > -EPSILON;
//	}
//
//	@Override
//	public BB getBB() {
//		return new BB(-Double.MAX_VALUE, Double.MAX_VALUE, getTransform()
//				.getPosition().y, getTransform().getPosition().y,
//				-Double.MAX_VALUE, Double.MAX_VALUE);
//	}
//}
