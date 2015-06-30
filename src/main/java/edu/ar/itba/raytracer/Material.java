package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.materials.MaterialType;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Material {

	public static final double MAX_SHININESS = 128.0;

	public static final Material GOLD = new Material(new ConstantColorTexture(
			0.24725, 0.1995, 0.0745), new ConstantColorTexture(0.75164,
			0.60648, 0.22648), new ConstantColorTexture(0.628281, 0.555802,
			0.366065), 51.2, new ConstantColorTexture(0,0,0), 0);

	public static final Material POLISHED_GOLD = new Material(
			new ConstantColorTexture(0.24725, 0.2245, 0.0645),
			new ConstantColorTexture(0.34615, 0.3143, 0.0903),
			new ConstantColorTexture(0.797357, 0.723991, 0.208006), 83.2, new ConstantColorTexture(0,0,0), 0);

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

	public final double roughness;

    public final double shininess;

	public final Texture transparency;

	public final double refractionIndex;

    public Light light;

    public double fresnel;

    public final MaterialType type;

	public Material(final Texture ka, final Texture kd, final Texture ks,
			final double roughness, final Texture transparency,
			final double refractionIndex) {
        this(ka,kd,ks,roughness,transparency,refractionIndex,null);
	}

    public Material(final Texture ka, final Texture kd, final Texture ks,
                    final double roughness, final Texture transparency,
                    final double refractionIndex, Light light) {

        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.roughness = roughness;
        this.transparency = transparency;
        this.refractionIndex = refractionIndex;
        this.shininess = MAX_SHININESS*roughness;
        this.light = light;
        this.fresnel = 0d;


        if(refractionIndex > 1) {
            this.type = MaterialType.Glass;
        }else if(roughness == 0){
            this.type = MaterialType.Matte;
        }else if(roughness == MAX_SHININESS){
            this.type = MaterialType.Specular;
        }else{
            this.type = MaterialType.Glossy;
        }
    }


    public void setLight(Light light){
        this.light = light;
    }

    public void setFresnel(double fresnel){
        this.fresnel = fresnel;
    }

}
