package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Metal2 extends Material {

	public Metal2(final Texture kr, final double roughness, final double fresnel) {
		super(ConstantColorTexture.BLACK, kr, kr,
				roughness, ConstantColorTexture.BLACK, 0d);
        setFresnel(fresnel);
	}
}
