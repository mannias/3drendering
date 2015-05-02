package edu.ar.itba.raytracer.vector;

public class Matrix44 {

	private double m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23,
			m30, m31, m32, m33;

	public Matrix44(double m00, double m01, double m02, double m03, double m10,
			double m11, double m12, double m13, double m20, double m21,
			double m22, double m23, double m30, double m31, double m32,
			double m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public Vector4 multiply(final Vector4 vec) {
		final double x = m00 * vec.x + m01 * vec.y + m02 * vec.z + m03 * vec.w;
		final double y = m10 * vec.x + m11 * vec.y + m12 * vec.z + m13 * vec.w;
		final double z = m20 * vec.x + m21 * vec.y + m22 * vec.z + m23 * vec.w;
		final double w = m30 * vec.x + m31 * vec.y + m32 * vec.z + m33 * vec.w;

		return new Vector4(x, y, z, w);
	}

	public Matrix44 multiply(final Matrix44 matrix) {
		final double n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33;

		n00 = m00 * matrix.m00 + m01 * matrix.m10 + m02 * matrix.m20 + m03
				* matrix.m30;
		n01 = m00 * matrix.m01 + m01 * matrix.m11 + m02 * matrix.m21 + m03
				* matrix.m31;
		n02 = m00 * matrix.m02 + m01 * matrix.m12 + m02 * matrix.m22 + m03
				* matrix.m32;
		n03 = m00 * matrix.m03 + m01 * matrix.m13 + m02 * matrix.m23 + m03
				* matrix.m33;
		n10 = m10 * matrix.m00 + m11 * matrix.m10 + m12 * matrix.m20 + m13
				* matrix.m30;
		n11 = m10 * matrix.m01 + m11 * matrix.m11 + m12 * matrix.m21 + m13
				* matrix.m31;
		n12 = m10 * matrix.m02 + m11 * matrix.m12 + m12 * matrix.m22 + m13
				* matrix.m32;
		n13 = m10 * matrix.m03 + m11 * matrix.m13 + m12 * matrix.m23 + m13
				* matrix.m33;
		n20 = m20 * matrix.m00 + m21 * matrix.m10 + m22 * matrix.m20 + m23
				* matrix.m30;
		n21 = m20 * matrix.m01 + m21 * matrix.m11 + m22 * matrix.m21 + m23
				* matrix.m31;
		n22 = m20 * matrix.m02 + m21 * matrix.m12 + m22 * matrix.m22 + m23
				* matrix.m32;
		n23 = m20 * matrix.m03 + m21 * matrix.m13 + m22 * matrix.m23 + m23
				* matrix.m33;
		n30 = m30 * matrix.m00 + m31 * matrix.m10 + m32 * matrix.m20 + m33
				* matrix.m30;
		n31 = m30 * matrix.m01 + m31 * matrix.m11 + m32 * matrix.m21 + m33
				* matrix.m31;
		n32 = m30 * matrix.m02 + m31 * matrix.m12 + m32 * matrix.m22 + m33
				* matrix.m32;
		n33 = m30 * matrix.m03 + m31 * matrix.m13 + m32 * matrix.m23 + m33
				* matrix.m33;

		return new Matrix44(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,
				n22, n23, n30, n31, n32, n33);

	}

}
