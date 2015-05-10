package edu.ar.itba.raytracer;

import static org.junit.Assert.assertSame;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.Mesh;
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

//	@Test
//	public void testAddPlane() {
//        ShapeProperties properties = new ShapeProperties(new Material(new Color(0, 0, 1), new Color(0, 0, 1), new Color(0, 0, 1), 50, 0, 1));
//
//        final Plane plane = new Plane(new Transform(), properties, new Vector3(0, 0, 0));
//        final Instance i7 = new Instance(plane);
//        scene.
//		assertSame(plane, scene.getObjects().iterator().next());
//	}

}
