package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public abstract class PositionLight extends Light {

	public final Vector4 position;

	public PositionLight(final Color color, final Vector4 position, final Matrix44 transform) {
		super(color);
		this.position = transform.multiplyVec(position);
	}

}