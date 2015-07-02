package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class DirectionalLight extends Light {

	private final Vector4 dir;

	public DirectionalLight(final Vector4 from, final Vector4 to, final Matrix44 transform,
			final Color color, final double gain) {
		super(color.scalarMult(gain));
		final Vector4 dir = new Vector4(from);
		dir.sub(to);
		this.dir = transform.multiplyVec(dir);
		this.dir.normalize();
	}

	@Override
	public Vector4 getDirection(final Vector4 hitPoint) {
		return dir;
	}

	@Override
	public Color getIntensity(final RayCollisionInfo hitPoint) {
		return color;
	}

}
