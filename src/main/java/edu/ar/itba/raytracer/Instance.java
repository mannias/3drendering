package edu.ar.itba.raytracer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.List;

import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class Instance extends GeometricObject {

	private final GeometricObject obj;
	private Matrix44 invMatrix = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 1);
	private Matrix44 invMatrixT = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 1);
	private Matrix44 matrix = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0,
			0, 0, 0, 1);

	public Instance(final GeometricObject obj) {
		this.obj = obj;
	}

	public void translate(final double x, final double y, final double z) {
		final Matrix44 translationMatrix = translationMatrix(x, y, z);
		final Matrix44 invTranslationMatrix = new Matrix44(1, 0, 0, -x, 0, 1,
				0, -y, 0, 0, 1, -z, 0, 0, 0, 1);

		matrix = translationMatrix.multiply(matrix);
		invMatrix = invMatrix.multiply(invTranslationMatrix);
		invMatrixT = invMatrix.traspose();
	}

	public void rotateX(final double x) {
		final double rad = degToRad(x);
		final Matrix44 rotX = rotationXMatrix(x);
		final Matrix44 invRotX = new Matrix44(1, 0, 0, 0, 0, cos(rad),
				sin(rad), 0, 0, -sin(rad), cos(rad), 0, 0, 0, 0, 1);
		rotateAroundAxis(rotX, invRotX);
	}

	public void rotateY(final double y) {
		final double rad = degToRad(y);
		final Matrix44 rotY = rotationYMatrix(y);
		final Matrix44 invRotY = new Matrix44(cos(rad), 0, -sin(rad), 0, 0, 1,
				0, 0, sin(rad), 0, cos(rad), 0, 0, 0, 0, 1);
		rotateAroundAxis(rotY, invRotY);
	}

	public void rotateZ(final double z) {
		final double rad = degToRad(z);
		final Matrix44 rotZ = new Matrix44(cos(rad), -sin(rad), 0, 0, sin(rad),
				cos(rad), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		final Matrix44 invRotZ = new Matrix44(cos(rad), sin(rad), 0, 0,
				-sin(rad), cos(rad), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		rotateAroundAxis(rotZ, invRotZ);
	}

	private void rotateAroundAxis(final Matrix44 rot, final Matrix44 invRot) {
		matrix = rot.multiply(matrix);
		invMatrix = invMatrix.multiply(invRot);
		invMatrixT = invMatrix.traspose();
	}

	public void scale(final double x, final double y, final double z) {
		final Matrix44 scaleMatrix = new Matrix44(x, 0, 0, 0, 0, y, 0, 0, 0, 0,
				z, 0, 0, 0, 0, 1);
		final Matrix44 invScaleMatrix = new Matrix44(1 / x, 0, 0, 0, 0, 1 / y,
				0, 0, 0, 0, 1 / z, 0, 0, 0, 0, 1);

		matrix = scaleMatrix.multiply(matrix);
		invMatrix = invMatrix.multiply(invScaleMatrix);
		invMatrixT = invMatrix.traspose();
	}

	public void transform(final Matrix44 transform) {
		matrix = transform;
		invMatrix = matrix.invert();
		invMatrixT = invMatrix.traspose();
	}

	public RayCollisionInfo hit(final Ray ray, final CustomStack stack,
			final int top) {
		final Vector4 invOrigin = invMatrix.multiplyVec(ray.getSource());
		final Vector4 invDir = invMatrix.multiplyVec(ray.getDir());

		final Ray invRay = new Ray(invOrigin, invDir);

		final RayCollisionInfo collision = obj.hit(invRay, stack, top);
		if (collision == null) {
			return null;
		}
		collision.normal = invMatrixT.multiplyVec(collision.normal);
		collision.normal.normalize();
		collision.worldCollisionPoint = matrix.multiplyVec(collision
				.getLocalCollisionPoint());
		collision.distance = collision.worldCollisionPoint.distanceTo(ray
				.getSource());

		// if (obj.material != null) {
		// material = obj.material;
		// }

		collision.obj = this;

		return collision;

	}

	@Override
	public AABB getAABB() {
		final AABB untransformedAABB = obj.getAABB();
		final List<Vector4> corners = untransformedAABB.getCorners();
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (final Vector4 corner : corners) {
			final Vector4 transformedCorner = matrix.multiplyVec(corner);
			if (transformedCorner.x < minX) {
				minX = transformedCorner.x;
			}
			if (transformedCorner.x > maxX) {
				maxX = transformedCorner.x;
			}
			if (transformedCorner.y < minY) {
				minY = transformedCorner.y;
			}
			if (transformedCorner.y > maxY) {
				maxY = transformedCorner.y;
			}
			if (transformedCorner.z < minZ) {
				minZ = transformedCorner.z;
			}
			if (transformedCorner.z > maxZ) {
				maxZ = transformedCorner.z;
			}
		}

		return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
	}

}
