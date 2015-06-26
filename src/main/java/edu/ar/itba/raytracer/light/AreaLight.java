package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Vector4;

public class AreaLight extends Light{

    public AreaLight(Color color) {
        super(color);
    }

    @Override
    public Vector4 getDirection(Vector4 hitPoint) {
        return null;
    }

    @Override
    public Color getIntensity(Vector4 hitPoint) {
        return color;
    }
}
