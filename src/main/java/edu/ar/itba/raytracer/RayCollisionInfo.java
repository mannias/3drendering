package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.vector.Vector3;

public class RayCollisionInfo {

	public static RayCollisionInfo noCollision(final Ray ray) {
		return new RayCollisionInfo(null, ray, 0);
	}

	private final SceneShape obj;
	private final Ray ray;
	private final double distance;
	private final Vector3 collisionPoint;

	public RayCollisionInfo(SceneShape obj, Ray ray, double distance) {
		this.obj = obj;
		this.ray = ray;
		this.distance = distance;
		final Vector3 raySource = ray.getSource();
		final Vector3 rayDir = ray.getDir();
		collisionPoint = new Vector3(raySource.x + distance * rayDir.x,
				raySource.y + distance * rayDir.y, raySource.z + distance
						* rayDir.z);
	}

	public boolean collisionDetected() {
		return obj != null;
	}

	public SceneShape getObj() {
		return obj;
	}

	public Ray getRay() {
		return ray;
	}

	public double getDistance() {
		return distance;
	}

	public Vector3 getCollisionPoint() {
		return collisionPoint;
	}

}
