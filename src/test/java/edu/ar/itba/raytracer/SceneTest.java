package edu.ar.itba.raytracer;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import edu.ar.itba.raytracer.shape.Plane;
import edu.ar.itba.raytracer.vector.Vector3;

public class SceneTest {

	private Scene scene;

	@Before
	public void setUp() {
		scene = new Scene();
	}

	@Test
	public void testAddCamera() {
		final Camera camera = scene.addCamera(10, 10, 45);
		assertSame(camera, scene.getCameras().iterator().next());
	}

	@Test
	public void testAddPlane() {
		final Plane plane = scene.addPlane(new Vector3(0, 0, 0), new Vector3(0,
				0, 0));
		assertSame(plane, scene.getObjects().iterator().next());
	}

}
