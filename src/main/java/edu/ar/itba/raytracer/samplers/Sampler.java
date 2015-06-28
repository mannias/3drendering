package edu.ar.itba.raytracer.samplers;

import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.*;

public class Sampler {

    protected final int samples;
    protected final List<Vector3> sampleList = new Stack<>();

    public Sampler(int samples) {
        this.samples = samples;
    }

    private static long sum = 0;
    public static Vector4 cosWeightedHemisphere(Vector4 normal) {
        long start = System.currentTimeMillis();
        double Xi1 = Math.random();
        double Xi2 = Math.random();
        double theta = Math.acos(Math.sqrt(1.0 - Xi1));
        double phi = 2.0 * Math.PI * Xi2;
        double xs = Math.sin(theta) * Math.cos(phi);
        double ys = Math.cos(theta);
        double zs = Math.sin(theta) * Math.sin(phi);

        Vector4 y = new Vector4(normal);
        Vector4 h = new Vector4(y);
        if (Math.abs(h.x) <= Math.abs(h.y) && Math.abs(h.x) <= Math.abs(h.z))
            h.x = 1.0;
        else if (Math.abs(h.y) <= Math.abs(h.x) && Math.abs(h.y) <= Math.abs(h.z))
            h.y = 1.0;
        else
            h.z = 1.0;
        Vector4 x = (h.cross(y)).normalize();
        Vector4 z = (x.cross(y)).normalize();
        Vector4 direction = x.scalarMult(xs).add(y.scalarMult(ys)).add(z.scalarMult(zs));
        direction.w = 0;

        if (direction.dot(normal) < 0) {
            direction.scalarMult(-1);
        }
        sum += System.currentTimeMillis() - start;
        return direction.normalize();
    }

    public static void printsum(){
        System.out.println(sum);
    }
}

