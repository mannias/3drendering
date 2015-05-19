package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class BoundedPlane2{

//    private final Vector4 point;
//    private final Vector4 a;
//    private final Vector4 b;
//
//    public BoundedPlane2(final Vector4 point, final Vector4 a, final Vector4 b, final Vector4 normal) {
//        super(normal);
//        this.point = point;
//        this.a = a;
//        this.b = b;
//    }
//
//    @Override
//    public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
//        final double dist = super.hit(ray,stack,top).distance;
//
//        // If the ray doesn't intersect with the infinite plane, then it won't
//        // intersect with this bounded plane.
//        if (dist == -1) {
//            return null;
//        }
//
//        final Vector4 raySource = ray.getSource();
//        final Vector4 rayDir = ray.getDir();
//        final Vector4 tmin = new Vector3(0,0,0);
//        final Vector4 tmax = new Vector3(0,0,0);
//
//        double t = (point.sub(raySource)).dot(normal)/(rayDir.dot(normal));
//
//    }
//
//    @Override
//    public AABB getAABB() {
//        double minX = Math.min(p0.x, p1.x);
//        double maxX = Math.max(p0.x, p1.x);
//        double minY = Math.min(p0.y, p1.y);
//        double maxY = Math.max(p0.y, p1.y);
//        double minZ = Math.min(p0.z, p1.z);
//        double maxZ = Math.max(p0.z, p1.z);
//        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
//    }
}
