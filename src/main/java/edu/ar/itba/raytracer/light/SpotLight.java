package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class SpotLight extends PositionLight {

	private final Vector4 dir;
	private final double coneAngle;
	private final double coneDeltaAngle;

	public SpotLight(final Color color, final Vector4 from, final Vector4 to,
			final double coneAngle, final double coneDeltaAngle,
			final Matrix44 transform) {
		super(color, from, transform);
		final Vector4 dir = new Vector4(from);
		dir.sub(to);

		this.dir = transform.multiplyVec(dir);
		this.dir.normalize();
		
		this.coneAngle = coneAngle;
		this.coneDeltaAngle = coneDeltaAngle;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		final Vector4 v = new Vector4(position);
		v.sub(hitPoint);
		v.normalize();

		final double angle = Math.acos(v.dot(dir)) * 180 / Math.PI;
		if (angle > coneAngle) {
			return new Color(0, 0, 0);
		}
		if (angle > coneDeltaAngle) {
			final double portion = 1 - ((angle - coneDeltaAngle) / (coneAngle - coneDeltaAngle));
			return new Color(Math.max(0, color.getRed() * portion), Math.max(0,
					color.getGreen() * portion), Math.max(0, color.getBlue()
					* portion));
		} else
			return this.color;
	}

	@Override
	public Vector4 getDirection(final Vector4 hitPoint) {
		return dir;
	}

}
