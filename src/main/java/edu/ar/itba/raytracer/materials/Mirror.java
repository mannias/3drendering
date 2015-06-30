package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Mirror extends Material {

	public Mirror(Texture reflectivity) {
		super(ConstantColorTexture.BLACK, ConstantColorTexture.BLACK,
				reflectivity, 0d,
				ConstantColorTexture.BLACK, 0d);
	}

	public Mirror() {
		this(new ConstantColorTexture(1, 1, 1));
	}
}
