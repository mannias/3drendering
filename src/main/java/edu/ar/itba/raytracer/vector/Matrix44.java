package edu.ar.itba.raytracer.vector;

import java.util.Arrays;

public class Matrix44 {

	public static final Matrix44 ID = new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 1);

	private double[][] m = new double[4][4];

	public Matrix44(double m00, double m01, double m02, double m03, double m10,
			double m11, double m12, double m13, double m20, double m21,
			double m22, double m23, double m30, double m31, double m32,
			double m33) {
		m[0][0] = m00;
		m[0][1] = m01;
		m[0][2] = m02;
		m[0][3] = m03;
		m[1][0] = m10;
		m[1][1] = m11;
		m[1][2] = m12;
		m[1][3] = m13;
		m[2][0] = m20;
		m[2][1] = m21;
		m[2][2] = m22;
		m[2][3] = m23;
		m[3][0] = m30;
		m[3][1] = m31;
		m[3][2] = m32;
		m[3][3] = m33;
	}

	public Vector4 multiplyVec(final Vector4 vec) {
		final double[] res = new double[4];

		res[0] = m[0][0] * vec.x + m[0][1] * vec.y + m[0][2] * vec.z + m[0][3]
				* vec.w;
		res[1] = m[1][0] * vec.x + m[1][1] * vec.y + m[1][2] * vec.z + m[1][3]
				* vec.w;
		res[2] = m[2][0] * vec.x + m[2][1] * vec.y + m[2][2] * vec.z + m[2][3]
				* vec.w;
		res[3] = m[3][0] * vec.x + m[3][1] * vec.y + m[3][2] * vec.z + m[3][3]
				* vec.w;

		return new Vector4(res[0], res[1], res[2], res[3]);
	}

	public Matrix44() {

	}

	public Matrix44 multiply(final Matrix44 matrix) {
		final Matrix44 n = new Matrix44();

		n.m[0][0] = m[0][0] * matrix.m[0][0] + m[0][1] * matrix.m[1][0]
				+ m[0][2] * matrix.m[2][0] + m[0][3] * matrix.m[3][0];
		n.m[0][1] = m[0][0] * matrix.m[0][1] + m[0][1] * matrix.m[1][1]
				+ m[0][2] * matrix.m[2][1] + m[0][3] * matrix.m[3][1];
		n.m[0][2] = m[0][0] * matrix.m[0][2] + m[0][1] * matrix.m[1][2]
				+ m[0][2] * matrix.m[2][2] + m[0][3] * matrix.m[3][2];
		n.m[0][3] = m[0][0] * matrix.m[0][3] + m[0][1] * matrix.m[1][3]
				+ m[0][2] * matrix.m[2][3] + m[0][3] * matrix.m[3][3];
		n.m[1][0] = m[1][0] * matrix.m[0][0] + m[1][1] * matrix.m[1][0]
				+ m[1][2] * matrix.m[2][0] + m[1][3] * matrix.m[3][0];
		n.m[1][1] = m[1][0] * matrix.m[0][1] + m[1][1] * matrix.m[1][1]
				+ m[1][2] * matrix.m[2][1] + m[1][3] * matrix.m[3][1];
		n.m[1][2] = m[1][0] * matrix.m[0][2] + m[1][1] * matrix.m[1][2]
				+ m[1][2] * matrix.m[2][2] + m[1][3] * matrix.m[3][2];
		n.m[1][3] = m[1][0] * matrix.m[0][3] + m[1][1] * matrix.m[1][3]
				+ m[1][2] * matrix.m[2][3] + m[1][3] * matrix.m[3][3];
		n.m[2][0] = m[2][0] * matrix.m[0][0] + m[2][1] * matrix.m[1][0]
				+ m[2][2] * matrix.m[2][0] + m[2][3] * matrix.m[3][0];
		n.m[2][1] = m[2][0] * matrix.m[0][1] + m[2][1] * matrix.m[1][1]
				+ m[2][2] * matrix.m[2][1] + m[2][3] * matrix.m[3][1];
		n.m[2][2] = m[2][0] * matrix.m[0][2] + m[2][1] * matrix.m[1][2]
				+ m[2][2] * matrix.m[2][2] + m[2][3] * matrix.m[3][2];
		n.m[2][3] = m[2][0] * matrix.m[0][3] + m[2][1] * matrix.m[1][3]
				+ m[2][2] * matrix.m[2][3] + m[2][3] * matrix.m[3][3];
		n.m[3][0] = m[3][0] * matrix.m[0][0] + m[3][1] * matrix.m[1][0]
				+ m[3][2] * matrix.m[2][0] + m[3][3] * matrix.m[3][0];
		n.m[3][1] = m[3][0] * matrix.m[0][1] + m[3][1] * matrix.m[1][1]
				+ m[3][2] * matrix.m[2][1] + m[3][3] * matrix.m[3][1];
		n.m[3][2] = m[3][0] * matrix.m[0][2] + m[3][1] * matrix.m[1][2]
				+ m[3][2] * matrix.m[2][2] + m[3][3] * matrix.m[3][2];
		n.m[3][3] = m[3][0] * matrix.m[0][3] + m[3][1] * matrix.m[1][3]
				+ m[3][2] * matrix.m[2][3] + m[3][3] * matrix.m[3][3];

		return n;
	}

	@Override
	public String toString() {
		final String format = "%g %g %g %g\n";
		return new StringBuilder(String.format(format, m[0][0], m[0][1],
				m[0][2], m[0][3]))
				.append(String.format(format, m[1][0], m[1][1], m[1][2],
						m[1][3]))
				.append(String.format(format, m[2][0], m[2][1], m[2][2],
						m[2][3]))
				.append(String.format(format, m[3][0], m[3][1], m[3][2],
						m[3][3])).toString();
	}

	public Matrix44 traspose() {
		return new Matrix44(m[0][0], m[1][0], m[2][0], m[3][0], m[0][1],
				m[1][1], m[2][1], m[3][1], m[0][2], m[1][2], m[2][2], m[3][2],
				m[0][3], m[1][3], m[2][3], m[3][3]);
	}

	public Matrix44 add(final Matrix44 other) {
		return new Matrix44(m[0][0] + other.m[0][0], m[1][0] + other.m[1][0],
				m[2][0] + other.m[2][0], m[3][0] + other.m[3][0], m[0][1]
						+ other.m[0][1], m[1][1] + other.m[1][1], m[2][1]
						+ other.m[2][1], m[3][1] + other.m[3][1], m[0][2]
						+ other.m[0][2], m[1][2] + other.m[1][2], m[2][2]
						+ other.m[2][2], m[3][2] + other.m[3][2], m[0][3]
						+ other.m[0][3], m[1][3] + other.m[1][3], m[2][3]
						+ other.m[2][3], m[3][3] + other.m[3][3]);
	}

	public Matrix44 sub(final Matrix44 other) {
		return this.add(other.scalarMult(-1));
	}

	public Matrix44 scalarMult(final double scalar) {
		return new Matrix44(m[0][0] * scalar, m[1][0] * scalar, m[2][0]
				* scalar, m[3][0] * scalar, m[0][1] * scalar, m[1][1] * scalar,
				m[2][1] * scalar, m[3][1] * scalar, m[0][2] * scalar, m[1][2]
						* scalar, m[2][2] * scalar, m[3][2] * scalar, m[0][3]
						* scalar, m[1][3] * scalar, m[2][3] * scalar, m[3][3]
						* scalar);
	}

	public double det() {
		return m[0][0] * threeByThreeDet(0, 0) - m[0][1]
				* threeByThreeDet(0, 1) + m[0][2] * threeByThreeDet(0, 2)
				- m[0][3] * threeByThreeDet(0, 3);
	}

	private double threeByThreeDet(final int row, final int col) {
		final double[][] minor = new double[3][3];

		int rowMinor = 0;
		for (int i = 0; i < 4; i++) {
			if (i == row) {
				continue;
			}
			int colMinor = 0;
			for (int j = 0; j < 4; j++) {
				if (j == col) {
					continue;
				}
				minor[rowMinor][colMinor] = m[i][j];
				colMinor++;
			}
			rowMinor++;
		}

		return minor[0][0] * minor[1][1] * minor[2][2] + minor[0][1]
				* minor[1][2] * minor[2][0] + minor[0][2] * minor[1][0]
				* minor[2][1] - minor[0][2] * minor[1][1] * minor[2][0]
				- minor[0][0] * minor[1][2] * minor[2][1] - minor[0][1]
				* minor[1][0] * minor[2][2];
	}

	public Matrix44 invert() {
		// final Matrix44 traspose = traspose();
		// final Matrix44 this2 = this.multiply(this);
		// final Matrix44 this3 = this2.multiply(this);
		// final Matrix44 traspose2 = traspose.multiply(traspose);
		// final Matrix44 traspose3 = traspose2.multiply(traspose);
		//
		// return traspose3
		// .sub(traspose.multiply(this2.traspose()).scalarMult(3))
		// .add(this3.traspose().scalarMult(2))
		// .scalarMult(1 / 6)
		// .sub(this.scalarMult(1 / 2)
		// .multiply(traspose2.sub(this2.traspose()))
		// .add(this2.multiply(traspose)).sub(this3))
		// .scalarMult(1 / det());

		final double b00 = m[1][2] * m[2][3] * m[3][1] - m[1][3] * m[2][2]
				* m[3][1] + m[1][3] * m[2][1] * m[3][2] - m[1][1] * m[2][3]
				* m[3][2] - m[1][2] * m[2][1] * m[3][3] + m[1][1] * m[2][2]
				* m[3][3];
		final double b10 = m[0][3] * m[2][2] * m[3][1] - m[0][2] * m[2][3]
				* m[3][1] - m[0][3] * m[2][1] * m[3][2] + m[0][1] * m[2][3]
				* m[3][2] + m[0][2] * m[2][1] * m[3][3] - m[0][1] * m[2][2]
				* m[3][3];
		final double b20 = m[0][2] * m[1][3] * m[3][1] - m[0][3] * m[1][2]
				* m[3][1] + m[0][3] * m[1][1] * m[3][2] - m[0][1] * m[1][3]
				* m[3][2] - m[0][2] * m[1][1] * m[3][3] + m[0][1] * m[1][2]
				* m[3][3];
		final double b30 = m[0][3] * m[1][2] * m[2][1] - m[0][2] * m[1][3]
				* m[2][1] - m[0][3] * m[1][1] * m[2][2] + m[0][1] * m[1][3]
				* m[2][2] + m[0][2] * m[1][1] * m[2][3] - m[0][1] * m[1][2]
				* m[2][3];
		final double b01 = m[1][3] * m[2][2] * m[3][0] - m[1][2] * m[2][3]
				* m[3][0] - m[1][3] * m[2][0] * m[3][2] + m[1][0] * m[2][3]
				* m[3][2] + m[1][2] * m[2][0] * m[3][3] - m[1][0] * m[2][2]
				* m[3][3];
		final double b11 = m[0][2] * m[2][3] * m[3][0] - m[0][3] * m[2][2]
				* m[3][0] + m[0][3] * m[2][0] * m[3][2] - m[0][0] * m[2][3]
				* m[3][2] - m[0][2] * m[2][0] * m[3][3] + m[0][0] * m[2][2]
				* m[3][3];
		final double b21 = m[0][3] * m[1][2] * m[3][0] - m[0][2] * m[1][3]
				* m[3][0] - m[0][3] * m[1][0] * m[3][2] + m[0][0] * m[1][3]
				* m[3][2] + m[0][2] * m[1][0] * m[3][3] - m[0][0] * m[1][2]
				* m[3][3];
		final double b31 = m[0][2] * m[1][3] * m[2][0] - m[0][3] * m[1][2]
				* m[2][0] + m[0][3] * m[1][0] * m[2][2] - m[0][0] * m[1][3]
				* m[2][2] - m[0][2] * m[1][0] * m[2][3] + m[0][0] * m[1][2]
				* m[2][3];
		final double b02 = m[1][1] * m[2][3] * m[3][0] - m[1][3] * m[2][1]
				* m[3][0] + m[1][3] * m[2][0] * m[3][1] - m[1][0] * m[2][3]
				* m[3][1] - m[1][1] * m[2][0] * m[3][3] + m[1][0] * m[2][1]
				* m[3][3];
		final double b12 = m[0][3] * m[2][1] * m[3][0] - m[0][1] * m[2][3]
				* m[3][0] - m[0][3] * m[2][0] * m[3][1] + m[0][0] * m[2][3]
				* m[3][1] + m[0][1] * m[2][0] * m[3][3] - m[0][0] * m[2][1]
				* m[3][3];
		final double b22 = m[0][1] * m[1][3] * m[3][0] - m[0][3] * m[1][1]
				* m[3][0] + m[0][3] * m[1][0] * m[3][1] - m[0][0] * m[1][3]
				* m[3][1] - m[0][1] * m[1][0] * m[3][3] + m[0][0] * m[1][1]
				* m[3][3];
		final double b32 = m[0][3] * m[1][1] * m[2][0] - m[0][1] * m[1][3]
				* m[2][0] - m[0][3] * m[1][0] * m[2][1] + m[0][0] * m[1][3]
				* m[2][1] + m[0][1] * m[1][0] * m[2][3] - m[0][0] * m[1][1]
				* m[2][3];
		final double b03 = m[1][2] * m[2][1] * m[3][0] - m[1][1] * m[2][2]
				* m[3][0] - m[1][2] * m[2][0] * m[3][1] + m[1][0] * m[2][2]
				* m[3][1] + m[1][1] * m[2][0] * m[3][2] - m[1][0] * m[2][1]
				* m[3][2];
		final double b13 = m[0][1] * m[2][2] * m[3][0] - m[0][2] * m[2][1]
				* m[3][0] + m[0][2] * m[2][0] * m[3][1] - m[0][0] * m[2][2]
				* m[3][1] - m[0][1] * m[2][0] * m[3][2] + m[0][0] * m[2][1]
				* m[3][2];
		final double b23 = m[0][2] * m[1][1] * m[3][0] - m[0][1] * m[1][2]
				* m[3][0] - m[0][2] * m[1][0] * m[3][1] + m[0][0] * m[1][2]
				* m[3][1] + m[0][1] * m[1][0] * m[3][2] - m[0][0] * m[1][1]
				* m[3][2];
		final double b33 = m[0][1] * m[1][2] * m[2][0] - m[0][2] * m[1][1]
				* m[2][0] + m[0][2] * m[1][0] * m[2][1] - m[0][0] * m[1][2]
				* m[2][1] - m[0][1] * m[1][0] * m[2][2] + m[0][0] * m[1][1]
				* m[2][2];

		return new Matrix44(b00, b01, b02, b03, b10, b11, b12, b13, b20, b21,
				b22, b23, b30, b31, b32, b33).scalarMult(1/det());
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Matrix44)) {
			return false;
		}
		final Matrix44 otherM = (Matrix44) other;

		return Arrays.equals(m[0], otherM.m[0])
				&& Arrays.equals(m[1], otherM.m[1])
				&& Arrays.equals(m[2], otherM.m[2])
				&& Arrays.equals(m[3], otherM.m[3]);

	}

}
