package edu.ar.itba.raytracer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.ar.itba.raytracer.shape.Plane;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.vector.Vector3;

@RunWith(Parameterized.class)
public class IntersectionTest {
//
//	@Parameters
//	public static Collection<Object[]> data() {
//		return Arrays
//				.asList(new Object[][] {
//						{
//								new Ray(new Vector3(10, 0, 0), new Vector3(
//										-1, 0, 0)),
//								new Plane(new Vector3(0, 0, 0), new Vector3(
//										1, 0, 0)), 10 },
//						{
//								new Ray(new Vector3(0, 5, 0), new Vector3(0,
//										-1, 0)),
//								new Plane(new Vector3(0, 0, 0), new Vector3(
//										0, 1, 0)), 5 },
//						{
//								new Ray(new Vector3(1, 1, 1), new Vector3(1,
//										1, 1)),
//								new Plane(new Vector3(0, 0, 0), new Vector3(
//										1, 1, 1)), -1 },
//						{
//								new Ray(new Vector3(0, 0, 0), new Vector3(0,
//										0, 1)),
//								new Sphere(new Vector3(0, 0, 2), 1), 1 } });
//	}
//
//	private final Ray ray;
//	private final Intersectable obj;
//
//	private final double expected;
//
//	public IntersectionTest(final Ray ray, final Intersectable obj,
//			final double expected) {
//		this.ray = ray;
//		this.obj = obj;
//		this.expected = expected;
//	}
//
//	@Test
//	public void testIntersection() {
//		assertEquals(expected, obj.intersect(ray), 0.001);
//	}
}
