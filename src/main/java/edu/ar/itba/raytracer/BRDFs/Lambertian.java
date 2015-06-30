package edu.ar.itba.raytracer.BRDFs;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.samplers.Sampler;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.vector.Vector4;

public class Lambertian implements BRDF {

    @Override
    public Color getIrradiance(RayCollisionInfo collision, Scene scene, CustomStack stack, Vector4 cameraPosition) {
        return collision.getObj().material.kd.getColor(collision).scalarMult(1/Math.PI);
    }
}
