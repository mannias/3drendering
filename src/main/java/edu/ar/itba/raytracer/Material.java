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

	public final double transparency;

	public final double refractionIndex;

	public Material(final Color color, final double ka, final double kd,
			final double ks, final double shininess, final double transparency,
			final double refractionIndex) {
		this.color = color;
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.shininess = shininess;
		this.transparency = transparency;
		this.refractionIndex = refractionIndex;
	}

}
