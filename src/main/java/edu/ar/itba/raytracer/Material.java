package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.texture.Texture;

public class Material {

	public static final double MAX_SHININESS = 1000;

	/**
	 * Ambient reflection constant.
	 */
	public final Texture ka;
	/**
	 * Diffuse reflection constant.
	 */
	public final Texture kd;
	/**
	 * Specular reflection constant.
	 */
	public final double ks;

	public final double shininess;

	public final double transparency;

	public final double refractionIndex;

	public Material(final Texture ka, final Texture kd,
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
