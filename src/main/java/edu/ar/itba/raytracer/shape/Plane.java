package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

public class Plane extends GeometricObject {

    protected static final double EPSILON = 0.000001f;
    protected final Vector4 normal;

    public Plane(Vector4 normal){
        this.normal = normal;
    }

    @Override
    public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
        // calls.incrementAndGet();
        final double vd = normal.dot(ray.getDir());
        if (vd == 0) {
            // No intersection.
            return null;
        }

        final double v0 = -(normal.dot(ray.getSource()) - normal.dot(new Vector4(0,0,0,0)));
        final double t = v0 / vd;
        if (t < 0) {
            // Intersection happens behind source.
            return null;
        }

        // intersections.incrementAndGet();
        RayCollisionInfo rci = new RayCollisionInfo(this,ray,t);
        rci.normal = normal;
        return rci;
    }

    @Override
    public AABB getAABB() {
        double minX = Double.MIN_VALUE;
        double maxX = Double.MAX_VALUE;
        double minY = Double.MIN_VALUE;
        double maxY = Double.MAX_VALUE;
        double minZ = Double.MIN_VALUE;
        double maxZ = Double.MAX_VALUE;
        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
