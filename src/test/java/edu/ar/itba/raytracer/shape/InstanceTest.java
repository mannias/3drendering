package edu.ar.itba.raytracer.shape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import edu.ar.itba.raytracer.vector.Vector4;
import org.junit.Test;

import edu.ar.itba.raytracer.Instance;

public class InstanceTest {

	private static final double EPSILON = 0.00001;

	private Instance instance;

	@Test
	public void testAABBWithNoTransform() {
		instance = new Instance(new Sphere(1));

		assertThat(instance.getAABB().minX).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxX).isCloseTo(1, within(EPSILON));
		assertThat(instance.getAABB().minY).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxY).isCloseTo(1, within(EPSILON));
		assertThat(instance.getAABB().minZ).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxZ).isCloseTo(1, within(EPSILON));
	}

	@Test
	public void testAABBWithTranslation() {
		instance = new Instance(new Sphere(1));
		instance.translate(2, 3, 4);

		assertThat(instance.getAABB().minX).isCloseTo(1, within(EPSILON));
		assertThat(instance.getAABB().maxX).isCloseTo(3, within(EPSILON));
		assertThat(instance.getAABB().minY).isCloseTo(2, within(EPSILON));
		assertThat(instance.getAABB().maxY).isCloseTo(4, within(EPSILON));
		assertThat(instance.getAABB().minZ).isCloseTo(3, within(EPSILON));
		assertThat(instance.getAABB().maxZ).isCloseTo(5, within(EPSILON));
	}

	@Test
	public void testAABBWithScale() {
		instance = new Instance(new Sphere(1));
		instance.scale(2, 3, 4);

		assertThat(instance.getAABB().minX).isCloseTo(-2, within(EPSILON));
		assertThat(instance.getAABB().maxX).isCloseTo(2, within(EPSILON));
		assertThat(instance.getAABB().minY).isCloseTo(-3, within(EPSILON));
		assertThat(instance.getAABB().maxY).isCloseTo(3, within(EPSILON));
		assertThat(instance.getAABB().minZ).isCloseTo(-4, within(EPSILON));
		assertThat(instance.getAABB().maxZ).isCloseTo(4, within(EPSILON));
	}

	@Test
	public void testAABBWithRotationX() {
		instance = new Instance(new Sphere(1));

		instance.rotateX(45);

		assertThat(instance.getAABB().minX).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxX).isCloseTo(1, within(EPSILON));
		assertThat(instance.getAABB().minY).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxY).isCloseTo(1, within(EPSILON));
		assertThat(instance.getAABB().minZ).isCloseTo(-1, within(EPSILON));
		assertThat(instance.getAABB().maxZ).isCloseTo(1, within(EPSILON));
	}

}
