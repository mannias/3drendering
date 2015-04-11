package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.properties.Color;

public class Material {

	public static final double MAX_SHININESS = 1000;

	public final Color color;

	/**
	 * Ambient reflection constant.
	 */
	public final double ka;
	/**
	 * Diffuse reflection constant.
	 */
	public final double kd;
	/**
	 * Specular reflection constant.
	 */
	public final double ks;

	public final double shininess;

	public Material(final Color color, final double ka, final double kd,
			final double ks, final double shininess) {
		this.color = color;
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.shininess = shininess;
	}

}
