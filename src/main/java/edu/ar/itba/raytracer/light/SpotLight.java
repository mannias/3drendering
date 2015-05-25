package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class SpotLight extends PositionLight {

	private final Vector4 dir;
	private final Vector4 invDir;
	private final double coneAngle;
	private final double coneDeltaAngle;

	public SpotLight(final Color color, final Vector4 from, final Vector4 to,
			final double coneAngle, final double coneDeltaAngle,
			final Matrix44 transform) {
		super(color, from, transform);
		final Vector4 dir = new Vector4(to);
		dir.sub(from);

		this.dir = transform.multiplyVec(dir);
		this.dir.normalize();

		invDir = new Vector4(this.dir);
		invDir.scalarMult(-1);

		this.coneAngle = coneAngle;
		this.coneDeltaAngle = coneDeltaAngle;
	}

	@Override
	public Color getIntensity(final Vector4 hitPoint) {
		final Vector4 v = new Vector4(hitPoint);
		v.sub(position);
		v.normalize();

		final double angle = Math.acos(v.dot(dir)) * 180 / Math.PI;

		if (angle > coneAngle) {
			return new Color(0, 0, 0);
		}

		if (angle <= coneDeltaAngle) {
			return color;
		}

		final double pctg = Math.pow(dir.dot(v), coneAngle - coneDeltaAngle);

		return new Color(Math.max(0, color.getRed() * pctg), Math.max(0,
				color.getGreen() * pctg), Math.max(0, color.getBlue() * pctg));
	}

	@Override
	public Vector4 getDirection(final Vector4 hitPoint) {
		return invDir;
	}

}
