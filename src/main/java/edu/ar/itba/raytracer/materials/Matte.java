package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;

public class Matte extends Material{

    public Matte(Color kd){
        super(new Color(0,0,0),kd,0,0,0,0);
    }
}
