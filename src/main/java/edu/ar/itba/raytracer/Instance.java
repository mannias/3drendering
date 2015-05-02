package edu.ar.itba.raytracer;

import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class Instance extends GeometricObject {

	private final GeometricObject obj;
	private Matrix44 invMatrix = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 1);

	public Instance(final GeometricObject obj) {
		this.obj = obj;
	}

	public void translate(final double x, final double y, final double z) {
		final Matrix44 invTranslationMatrix = new Matrix44(1, 0, 0, -x, 0, 1,
				0, -y, 0, 0, 1, -z, 0, 0, 0, 1);

		invMatrix = invTranslationMatrix.multiply(invMatrix);
	}

	public RayCollisionInfo hit(final Ray ray) {
		final Vector4 invOrigin = invMatrix.multiply(ray.getSource());
		final Vector4 invDir = invMatrix.multiply(ray.getDir());
		
		final RayCollisionInfo collision = obj.hit(ray);
		if (collision.collisionDetected()) {
			collision.normal = invMatrix.multiply(collision.normal);
			collision.normal.normalize();
			
			if (obj.material != null) {
				material = obj.material;
			}
			
			return collision;
		}
		
		return RayCollisionInfo.noCollision(ray);

	}
}
