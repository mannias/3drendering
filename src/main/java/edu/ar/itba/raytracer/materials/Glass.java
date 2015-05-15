package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;

public class Glass extends Material{

    public Glass(Color reflected, Color transmited, double refractionIndex){
        super(reflected , transmited,0,0d,1,refractionIndex);
    }
}
