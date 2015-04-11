package edu.ar.itba.raytracer.properties;

import edu.ar.itba.raytracer.vector.Vector3;

public class Transform {

	private Vector3 position;
	private Vector3 rotation;
	private Vector3 scale;

	public Transform() {
		setPosition(new Vector3(0, 0, 0));
		setRotation(new Vector3(0, 0, 0));
		setScale(new Vector3(1, 1, 1));
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setRotation(Vector3 rotation) {
		this.rotation = rotation;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

}
