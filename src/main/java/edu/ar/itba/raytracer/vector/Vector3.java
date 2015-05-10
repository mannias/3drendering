package edu.ar.itba.raytracer.vector;

public class Vector3 extends Vector4{

    public Vector3(final double x, final double y, final double z){
        super(x, y, z, 0);
    }

    public Vector3(final Vector3 other){
        super(other.x,other.y,other.z,0);
    }
}
