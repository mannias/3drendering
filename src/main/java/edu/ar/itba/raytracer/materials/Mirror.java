package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Mirror extends Material {

    public Mirror(Texture reflectivity){
        super(new ConstantColorTexture(new Color(0,0,0)),
                new ConstantColorTexture(new Color(0,0,0)),
                reflectivity,999,0d,0d);
    }

    public Mirror(){
        super(new ConstantColorTexture(new Color(0,0,0)),
                new ConstantColorTexture(new Color(0,0,0)),
                new ConstantColorTexture(1,1,1),999,0d,0d);
    }
}
