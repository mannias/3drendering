package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.vector.Vector4;


public class Ray {

	private final Vector4 source;
	public Vector4 dir;

	public Ray(final Vector4 source, final Vector4 dir) {
		this.source = source;
		this.dir = dir;
		dir.normalize();
	}

	public Vector4 getSource() {
		return source;
	}

	public Vector4 getDir() {
		return dir;
	}

}
