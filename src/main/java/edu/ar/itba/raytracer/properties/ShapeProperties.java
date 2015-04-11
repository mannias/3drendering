package edu.ar.itba.raytracer.properties;

public class ShapeProperties {

	private final Color color;

	public ShapeProperties(final Color color) {
		this.color = color;
	}

	public ShapeProperties() {
		this(new Color());
	}

	public Color getColor() {
		return color;
	}
	
}
