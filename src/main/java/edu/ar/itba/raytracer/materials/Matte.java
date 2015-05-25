package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Matte extends Material {

	public Matte(Color kd) {
		super(ConstantColorTexture.BLACK, new ConstantColorTexture(kd),
				ConstantColorTexture.BLACK, 0, ConstantColorTexture.BLACK, 0);
	}

	public Matte(Texture tex) {
		super(tex, tex, ConstantColorTexture.BLACK, 0,
				ConstantColorTexture.BLACK, 0);
	}
}
