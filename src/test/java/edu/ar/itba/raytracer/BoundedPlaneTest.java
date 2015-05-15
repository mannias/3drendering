package edu.ar.itba.raytracer;

import static org.junit.Assert.assertEquals;

import edu.ar.itba.raytracer.properties.Color;
import org.junit.Test;

import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.BoundedPlane;
import edu.ar.itba.raytracer.vector.Vector3;

public class BoundedPlaneTest {

	@Test
	public void testBoundedIntersection() {
        ShapeProperties properties = new ShapeProperties(new Material(new Color(0, 0, 1), new Color(0, 0, 1), 1, 50, 0, 1));
		final BoundedPlane boundedPlane = new BoundedPlane(new Transform(),
                properties);
		final Ray ray = new Ray(new Vector3(0, 0, -1),
				new Vector3(-3, 0, 0).sub(new Vector3(0, 0, -1)));
		assertEquals(-1.0, boundedPlane.intersect(ray), 0.00001);
	}

	@Test
	public void testBoundedIntersection1() {
        ShapeProperties properties = new ShapeProperties(new Material(new Color(0, 0, 1), new Color(0, 0, 1), 1, 50, 0, 1));
        final Transform boundedPlaneTransform = new Transform();
		boundedPlaneTransform.setPosition(new Vector3(0, 0, 0));
		boundedPlaneTransform.setRotation(new Vector3(90, 0, 0));

		final BoundedPlane boundedPlane = new BoundedPlane(
				boundedPlaneTransform, properties);
		final Ray ray = new Ray(new Vector3(0, 8, 0),
				new Vector3(0, 0, 0).sub(new Vector3(0, 8, 0)));
		assertEquals(-1.0, boundedPlane.intersect(ray), 0.00001);
	}
}
