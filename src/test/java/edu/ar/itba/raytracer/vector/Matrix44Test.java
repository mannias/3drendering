package edu.ar.itba.raytracer.vector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class Matrix44Test {

	@Test
	public void testDeterminant0() {
		final Matrix44 m = new Matrix44(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16);

		assertThat(m.det()).isEqualTo(0);
	}

	@Test
	public void testDeterminant() {
		final Matrix44 m = new Matrix44(1, 2, 3, 4, 5, 6, 8, 8, 9, 20, 11, 12,
				13, 14, 40, 16);

		assertThat(m.det()).isEqualTo(-2640);
	}

	@Test
	public void testInvId() {
		assertThat(Matrix44.ID.invert()).isEqualTo(Matrix44.ID);
	}

	@Test
	public void testInv() {
		assertThat(
				new Matrix44(2, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2)
						.invert()).isEqualTo(
				new Matrix44(1, 0, 0, 0, 0, -1, 1, 0, 0, 1, 0, 0, -1, 0, 0, 1));
	}

}
