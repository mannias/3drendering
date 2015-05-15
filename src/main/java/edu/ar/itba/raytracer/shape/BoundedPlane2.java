package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class BoundedPlane2 extends GeometricObject{

    private final Vector4 p0;
    private final Vector4 p1;

    public BoundedPlane2(final Vector4 p0, final Vector4 p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    @Override
    public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
//        final double dist = super.hit(ray,stack,top).distance;
//
//        // If the ray doesn't intersect with the infinite plane, then it won't
//        // intersect with this bounded plane.
//        if (dist == -1) {
//            return null;
//        }

        final Vector4 raySource = ray.getSource();
        final Vector4 rayDir = ray.getDir();
        final Vector4 tmin = new Vector3(0,0,0);
        final Vector4 tmax = new Vector3(0,0,0);

        double a  = 1d/rayDir.x;
        if(a >= 0 ){
            tmin.x = (p0.x - raySource.x) * a;
            tmax.x = (p1.x - raySource.x) * a;
        }else{
            tmin.x = (p1.x - raySource.x) * a;
            tmax.x = (p0.x - raySource.x) * a;
        }

        double b = 1d/rayDir.y;
        if( b >= 0 ){
            tmin.y = (p0.y - raySource.y) * b;
            tmax.y = (p1.y - raySource.y) * b;
        }else{
            tmin.y = (p1.y - raySource.y) * b;
            tmax.y = (p0.y - raySource.y) * b;
        }

        double c = 1d/rayDir.z;
        if( b >= 0 ){
            tmin.z = (p0.z - raySource.z) * b;
            tmax.z = (p1.z - raySource.z) * b;
        }else{
            tmin.z = (p1.z - raySource.z) * b;
            tmax.z = (p0.z - raySource.z) * b;
        }

        return null;
    }

    @Override
    public AABB getAABB() {
        double minX = Math.min(p0.x, p1.x);
        double maxX = Math.max(p0.x, p1.x);
        double minY = Math.min(p0.y, p1.y);
        double maxY = Math.max(p0.y, p1.y);
        double minZ = Math.min(p0.z, p1.z);
        double maxZ = Math.max(p0.z, p1.z);
        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
