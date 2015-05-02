package edu.ar.itba.raytracer.properties;

public final class Color {

	public static final Color DEFAULT_COLOR = new Color(0, 0, 0);

	private double r, g, b;

	public Color() {
		this(DEFAULT_COLOR);
	}

	public Color(final double r, final double g, final double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(final Color c) {
		r = c.r;
		g = c.g;
		b = c.b;
	}

	public double getRed() {
		return r;
	}

	public double getGreen() {
		return g;
	}

	public double getBlue() {
		return b;
	}

	public void mult(final Color other) {
		r *= other.r;
		g *= other.g;
		b *= other.b;
	}

	public Color add(final Color other) {
		return new Color(Math.min(1, r + other.r), Math.min(1, g + other.g),
				Math.min(1, b + other.b));
	}

	public void scalarMult(final double scalar) {
		r = Math.max(0, Math.min(1, scalar * r));
		g = Math.max(0, Math.min(1, scalar * g));
		b = Math.max(0, Math.min(1, scalar * b));
	}

	// Generated by Eclipse
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Color)) {
			return false;
		}
		Color other = (Color) obj;
		if (b != other.b) {
			return false;
		}
		if (g != other.g) {
			return false;
		}
		if (r != other.r) {
			return false;
		}
		return true;
	}

}