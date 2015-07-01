package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class PointLight extends PositionLight {


	public PointLight(final Vector4 position, final Matrix44 transform,
			final Color color, final double gain) {
		super(color.scalarMult(gain), position, transform);
	}

	public Vector4 getDirection(final Vector4 hitPoint) {
		final Vector4 ret = new Vector4(position);
		ret.sub(hitPoint);
		ret.normalize();
		return ret;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		return color;
	}

}
