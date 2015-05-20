package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;

public class Glass extends Material{

    public Glass(double refractionIndex){
        super( new ConstantColorTexture(new Color(0,0,0)) ,
                new ConstantColorTexture(new Color(0.588235, 0.670588, 0.729412)),
                new ConstantColorTexture(new Color(0.9, 0.9, 0.9)),0d,1d,refractionIndex);
    }
}
