package edu.ar.itba.raytracer.shape;

import java.util.Arrays;
import java.util.Collection;

import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class Plane extends Mesh {

	private static Collection<MeshTriangle> triangles(final Vector4 normal) {
		// Using
		// http://math.stackexchange.com/questions/180418/calculate-rotation-matrix-to-align-vector-a-to-vector-b-in-3d
		final Vector4 a = new Vector4(0, 1, 0, 0);
		final Vector4 b = new Vector4(normal);
		b.normalize();
		final Vector4 v = a.cross(b);
		final double s = v.getNorm();
		final double c = a.dot(b);
		final Matrix44 id = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
				0, 0, 1);
		final Matrix44 vx = new Matrix44(0, -v.z, v.y, 0, v.z, 0, -v.x, 0,
				-v.y, v.x, 0, 0, 0, 0, 0, 0);

		final Matrix44 r;
		if (s == 0) {
			r = id;
		} else {
			r = id.add(vx).add(vx.multiply(vx).scalarMult((1 - c) / s / s));
		}

		return Arrays.asList(
				new MeshTriangle(r.multiplyVec(new Vector4(-.5, 0, -.5, 1)), r
						.multiplyVec(new Vector4(-.5, 0, .5, 1)), r
						.multiplyVec(new Vector4(.5, 0, -.5, 1)), new Vector2(
						1, 1), new Vector2(1, 0), new Vector2(0, 1), normal,
						normal, normal),
				new MeshTriangle(r.multiplyVec(new Vector4(-.5, 0, .5, 1)), r
						.multiplyVec(new Vector4(.5, 0, .5, 1)), r
						.multiplyVec(new Vector4(.5, 0, -.5, 1)), new Vector2(
						1, 0), new Vector2(0, 0), new Vector2(0, 1), normal,
						normal, normal));
	}

	public Plane(final Vector4 normal) {
		super(triangles(normal));
	}

}
