package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector4;

public class Box2 extends GeometricObject {


    private Vector4 normal;

    @Override
    public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
        return null;
    }

    @Override
    public AABB getAABB() {
        return null;
    }
}
