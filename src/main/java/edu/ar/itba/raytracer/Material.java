package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Material {

	public static final double MAX_SHININESS = 128;

	public static final Material GOLD = new Material(new ConstantColorTexture(
			0.24725, 0.1995, 0.0745), new ConstantColorTexture(0.75164,
			0.60648, 0.22648), new ConstantColorTexture(0.628281, 0.555802,
			0.366065), 51.2, 0, 0);

	public static final Material POLISHED_GOLD = new Material(
			new ConstantColorTexture(0.24725, 0.2245, 0.0645),
			new ConstantColorTexture(0.34615, 0.3143, 0.0903),
			new ConstantColorTexture(0.797357, 0.723991, 0.208006), 83.2, 0, 0);

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
	public final Texture ks;

	public final double shininess;

	public final double transparency;

	public final double refractionIndex;

	public Material(final Texture ka, final Texture kd, final Texture ks,
			final double shininess, final double transparency,
			final double refractionIndex) {
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.shininess = shininess;
		this.transparency = transparency;
		this.refractionIndex = refractionIndex;
	}

}
