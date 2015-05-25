package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Matte extends Material{

    public Matte(Color kd){
        super(new ConstantColorTexture(new Color(0,0,0)),new ConstantColorTexture(kd),
                new ConstantColorTexture(new Color(0,0,0)),0,0,0);
    }

    public Matte(Texture tex){
        super(tex, tex,
                new ConstantColorTexture(new Color(0,0,0)),0,0,0);
    }
}
