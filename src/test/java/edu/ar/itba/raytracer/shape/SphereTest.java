package edu.ar.itba.raytracer.shape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import edu.ar.itba.raytracer.vector.Vector4;

public class SphereTest {

	private Sphere2 sphere;

	@Before
	public void setup() {
		sphere = new Sphere2();
	}

	@Test
	public void testExtremePoints() {
		final Collection<Vector4> extremePoints = sphere.getExtremePoints();
		assertThat(extremePoints).hasSize(6);
		assertThat(extremePoints).extracting("x", "y", "z", "w").contains(
				tuple(1.0, 0.0, 0.0, 1.0), tuple(-1.0, 0.0, 0.0, 1.0),
				tuple(0.0, 1.0, 0.0, 1.0), tuple(0.0, -1.0, 0.0, 1.0),
				tuple(0.0, 0.0, 1.0, 1.0), tuple(0.0, 0.0, -1.0, 1.0));
	}
}
