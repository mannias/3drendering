package edu.ar.itba.raytracer.properties;

import edu.ar.itba.raytracer.Material;

public class ShapeProperties {

	private final Material material;

	public ShapeProperties(final Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
	
}
