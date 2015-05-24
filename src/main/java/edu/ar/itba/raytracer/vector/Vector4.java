package edu.ar.itba.raytracer.vector;

public class Vector4 {
	
	public static final Vector4 I = new Vector4(1,0,0,0);
	public static final Vector4 J = new Vector4(0,1,0,0);
	public static final Vector4 K = new Vector4(0,0,1,0);
	public static final Vector4 NI = new Vector4(-1,0,0,0);
	public static final Vector4 NJ = new Vector4(0,-1,0,0);
	public static final Vector4 NK = new Vector4(0,0,-1,0);

	
	public double x, y, z, w;

	// private double norm;

	public Vector4(final double x, final double y, final double z,
			final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public double[] getElemsAsArray() {
		return new double[] { x, y, z, w };
	}

	public Vector4(final Vector4 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}

	public Vector4 add(final Vector4 other) {
		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;
		return this;
	}

	public Vector4 sub(final Vector4 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		w -= other.w;
		return this;
	}

	public double dot(final Vector4 other) {
		return x * other.x + y * other.y + z * other.z + w * other.w;
	}

	public Vector4 cross(final Vector4 other) {
		return new Vector4(y * other.z - z * other.y,
				z * other.x - x * other.z, x * other.y - y * other.x, w);
	}

	public Vector4 scalarMult(final double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		w *= scalar;
		return this;
	}

	public Vector4 normalize() {
		final double norm = (double) Math.sqrt(x * x + y * y + z * z);
		if (norm == 0) {
			throw new IllegalStateException(
					"Cannot normalize vector with norm = 0");
		}

		final double invNorm = 1 / norm;

		x *= invNorm;
		y *= invNorm;
		z *= invNorm;
		w *= invNorm;
		return this;
	}

	public double distanceTo(final Vector4 point) {
		final double xDiff = x - point.x;
		final double yDiff = y - point.y;
		final double zDiff = z - point.z;
		final double wDiff = w - point.w;

		return (double) Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff
				+ wDiff * wDiff);
	}

	public Vector4 rotate(final Vector4 rotation) {
		final double xRotation = (double) (rotation.x * Math.PI / 180.0f);
		final double yRotation = (double) (rotation.y * Math.PI / 180.0f);
		final double zRotation = (double) (rotation.z * Math.PI / 180.0f);

		final double sinx = (double) Math.sin(xRotation);
		final double cosx = (double) Math.cos(xRotation);
		final double siny = (double) Math.sin(yRotation);
		final double cosy = (double) Math.cos(yRotation);
		final double sinz = (double) Math.sin(zRotation);
		final double cosz = (double) Math.cos(zRotation);

		final double x = this.x * cosz * cosy + this.y
				* (-sinz * cosx + cosz * siny * sinx) + this.z
				* (sinz * sinx + cosz * siny * cosx);
		final double y = this.x * sinz * cosy + this.y
				* (cosz * cosx + sinx * siny * sinz) + this.z
				* (-cosz * sinx + sinz * siny * cosx);
		final double z = -this.x * siny + this.y * cosy * sinx + this.z * cosy
				* cosx;

		return new Vector4(x, y, z, 1);
	}

	public double getNorm() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	// Generated by Eclipse.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	// Generated by Eclipse.
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Vector4)) {
			return false;
		}
		Vector4 other = (Vector4) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) {
			return false;
		}
		return true;
	}

	// Generated by Eclipse.
	@Override
	public String toString() {
		return "Vector3 [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
