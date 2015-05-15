package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;

public class Mirror extends Material {

    public Mirror(Color reflectivity){
        super(new Color(0,0,0),reflectivity,1,0d,0d,0d);
    }

    public Mirror(){
        super(new Color(0,0,0),new Color(0,0,0),1,1,0d,0d);
    }
}
