package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;

public class Glass extends Material{

    public Glass(Color reflectivity, Color transmited, double refractionIndex){
        super(new Color(1.0,1.0,1.0),new Color(1.0,1.0,1.0),new Color(1.0,1.0,1.0),0d,0d,refractionIndex);
    }
}
