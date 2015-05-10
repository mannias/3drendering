package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.properties.Color;

public class Material {

	public static final double MAX_SHININESS = 1000;
	/**
	 * Ambient reflection constant.
	 */
	public final Color ka;
	/**
	 * Diffuse reflection constant.
	 */
	public final Color kd;
	/**
	 * Specular reflection constant.
	 */
	public final double ks;

	public final double shininess;

	public final double transparency;

	public final double refractionIndex;

	public Material(final Color ka, final Color kd,
			final double ks, final double shininess, final double transparency,
			final double refractionIndex) {
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.shininess = shininess;
		this.transparency = transparency;
		this.refractionIndex = refractionIndex;
	}

}
