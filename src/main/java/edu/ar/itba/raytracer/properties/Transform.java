package edu.ar.itba.raytracer.properties;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class Transform {

	private Vector4 position;
	private Vector4 rotation;
	private Vector4 scale;

	public Matrix44 translationTransform;
	public Matrix44 invTranslationTransform;
	public Matrix44 rotationTransform;
	public Matrix44 invRotationTransform;
	public Matrix44 scaleTransform;
	public Matrix44 invScaleTransform;

	public Transform() {
		setPosition(new Vector4(0, 0, 0, 0));
		setRotation(new Vector4(0, 0, 0, 0));
		setScale(new Vector4(1, 1, 1, 0));
	}

	public Vector4 getPosition() {
		return position;
	}

	public Vector4 getRotation() {
		return rotation;
	}

	public Vector4 getScale() {
		return scale;
	}

	public void setPosition(Vector4 position) {
		translationTransform = new Matrix44(1, 0, 0, position.x, 0, 1, 0,
				position.y, 0, 0, 1, position.z, 0, 0, 0, 1);
		invTranslationTransform = new Matrix44(1, 0, 0, -position.x, 0, 1, 0,
				-position.y, 0, 0, 1, -position.z, 0, 0, 0, 1);
		this.position = position;
	}

	public void setRotation(Vector4 rotation) {
		final Matrix44 rotX = new Matrix44(1, 0, 0, 0, 0, cos(rotation.x),
				-sin(rotation.x), 0, 0, sin(rotation.x), cos(rotation.x), 0, 0,
				0, 0, 1);
		final Matrix44 rotY = new Matrix44(cos(rotation.y), 0, sin(rotation.y),
				0, 0, 1, 0, 0, -sin(rotation.y), 0, cos(rotation.y), 0, 0, 0,
				0, 1);
		final Matrix44 rotZ = new Matrix44(cos(rotation.z), -sin(rotation.z),
				0, 0, sin(rotation.z), cos(rotation.z), 0, 0, 0, 0, 1, 0, 0, 0,
				0, 1);

		final Matrix44 invRotX = new Matrix44(1, 0, 0, 0, 0, cos(rotation.x),
				sin(rotation.x), 0, 0, -sin(rotation.x), cos(rotation.x), 0, 0,
				0, 0, 1);
		final Matrix44 invRotY = new Matrix44(cos(rotation.y), 0,
				-sin(rotation.y), 0, 0, 1, 0, 0, sin(rotation.y), 0,
				cos(rotation.y), 0, 0, 0, 0, 1);
		final Matrix44 invRotZ = new Matrix44(cos(rotation.z), sin(rotation.z),
				0, 0, -sin(rotation.z), cos(rotation.z), 0, 0, 0, 0, 1, 0, 0,
				0, 0, 1);

		rotationTransform = rotX.multiply(rotY).multiply(rotZ);
		invRotationTransform = invRotZ.multiply(invRotY).multiply(invRotX);

		this.rotation = rotation;
	}

	public void setScale(Vector4 scale) {
		scaleTransform = new Matrix44(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0,
				scale.z, 0, 0, 0, 0, 1);
		invScaleTransform = new Matrix44(1 / scale.x, 0, 0, 0, 0, 1 / scale.y,
				0, 0, 0, 0, 1 / scale.z, 0, 0, 0, 0, 1);
		this.scale = scale;
	}

}
