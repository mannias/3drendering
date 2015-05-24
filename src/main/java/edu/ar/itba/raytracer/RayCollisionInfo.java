package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Vector4;

public class RayCollisionInfo {

	public GeometricObject obj;
	private final Ray ray;
	public double distance;
	private final Vector4 localCollisionPoint;
	public Vector4 worldCollisionPoint;
	public Vector4 normal;
	public double u,v;

	public RayCollisionInfo(GeometricObject obj, Ray ray, double distance) {
		this.obj = obj;
		this.ray = ray;
		this.distance = distance;
		final Vector4 raySource = ray.getSource();
		final Vector4 rayDir = ray.getDir();
		localCollisionPoint = new Vector4(raySource.x + distance * rayDir.x,
				raySource.y + distance * rayDir.y, raySource.z + distance
						* rayDir.z, 1);
	}

	public boolean collisionDetected() {
		return obj != null;
	}

	public GeometricObject getObj() {
		return obj;
	}

	public Ray getRay() {
		return ray;
	}

	public double getDistance() {
		return distance;
	}
	
	public Vector4 getWorldCollisionPoint() {
		return worldCollisionPoint;
	}

	public Vector4 getLocalCollisionPoint() {
		return localCollisionPoint;
	}

}
