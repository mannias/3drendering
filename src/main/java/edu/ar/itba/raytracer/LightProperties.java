package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.properties.Color;

public class LightProperties {

	private final Color color;

	public LightProperties(final Color color) {
		this.color = color;
	}

	public LightProperties() {
		this.color = Color.DEFAULT_COLOR;
	}

	public Color getColor() {
		return color;
	}

}
