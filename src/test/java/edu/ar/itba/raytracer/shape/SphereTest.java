package edu.ar.itba.raytracer.shape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.*;

import edu.ar.itba.raytracer.vector.Vector3;
import org.junit.Before;
import org.junit.Test;

import edu.ar.itba.raytracer.vector.Vector4;

public class SphereTest {

	private Sphere sphere;

	@Before
	public void setup() {
		sphere = new Sphere(1);

	}

	@Test
	public void testExtremePoints() {
		final Vector4[] extremePoints = sphere.getPerfectSplits().getAllExtremePoints();
		assertThat(extremePoints).hasSize(6);
		assertThat(extremePoints).extracting("x", "y", "z", "w").contains(
				tuple(1.0, 0.0, 0.0, 1.0), tuple(-1.0, 0.0, 0.0, 1.0),
				tuple(0.0, 1.0, 0.0, 1.0), tuple(0.0, -1.0, 0.0, 1.0),
				tuple(0.0, 0.0, 1.0, 1.0), tuple(0.0, 0.0, -1.0, 1.0));
	}
}