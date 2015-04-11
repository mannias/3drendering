package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.vector.Vector3;


public class Ray {

	private final Vector3 source;
	private final Vector3 dir;

	public Ray(final Vector3 source, final Vector3 dir) {
		this.source = source;
		this.dir = dir.normalize();
	}

	public Vector3 getSource() {
		return source;
	}

	public Vector3 getDir() {
		return dir;
	}

}
