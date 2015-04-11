package edu.ar.itba.raytracer.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class Vector3Test {

	@Test
	public void testDot() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(6, 7, 8);

		assertEquals(86.0, v1.dot(v2), 0.0000001);
	}

	@Test
	public void testDot1() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(0, 0, 0);

		assertEquals(0.0, v1.dot(v2), 0.0000001);
	}

	@Test
	public void testAdd() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(0, 0, 0);

		Vector3 res = v1.add(v2);
		assertEquals(v1, res);
		assertNotSame(v1, res);
	}

	@Test
	public void testAdd2() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(6, 7, 8);

		Vector3 res = v1.add(v2);
		assertEquals(new Vector3(9, 11, 13), res);
		assertNotSame(v1, res);
	}

	@Test
	public void testSub() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(0, 0, 0);

		Vector3 res = v1.sub(v2);
		assertEquals(v1, res);
		assertNotSame(v1, res);
	}

	@Test
	public void testSub2() {
		Vector3 v1 = new Vector3(6, 7, 8);
		Vector3 v2 = new Vector3(3, 4, 5);

		Vector3 res = v1.sub(v2);
		assertEquals(new Vector3(3, 3, 3), res);
		assertNotSame(v1, res);
	}

	@Test
	public void testScalarProduct() {
		Vector3 v1 = new Vector3(3, 4, 5);

		Vector3 res = v1.scalarMult(5);
		assertEquals(new Vector3(15, 20, 25), res);
		assertNotSame(v1, res);
	}

	@Test
	public void testScalarProduct2() {
		Vector3 v1 = new Vector3(3, 4, 5);

		Vector3 res = v1.scalarMult(0);
		assertEquals(new Vector3(0, 0, 0), res);
		assertNotSame(v1, res);
	}

	@Test
	public void testNormalize() {
		Vector3 v1 = new Vector3(1, 0, 0);

		Vector3 res = v1.normalize();
		assertEquals(v1, res);
		assertNotSame(v1, res);
	}

	@Test
	public void testNormalize1() {
		Vector3 v1 = new Vector3(0, 1, 0);

		Vector3 res = v1.normalize();
		assertEquals(v1, res);
		assertNotSame(v1, res);
	}

	@Test
	public void testNormalize2() {
		Vector3 v1 = new Vector3(0, 0, 1);

		Vector3 res = v1.normalize();
		assertEquals(v1, res);
		assertNotSame(v1, res);
	}

	@Test
	public void testNormalize3() {
		Vector3 v1 = new Vector3(1, 1, 1);

		Vector3 res = v1.normalize();
		assertEquals(
				new Vector3(1 / Math.sqrt(3), 1 / Math.sqrt(3),
						1 / Math.sqrt(3)), res);
		assertNotSame(v1, res);
	}

	@Test
	public void testDistanceTo() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(3, 4, 5);
		
		assertEquals(0, v1.distanceTo(v2), 0.0000001);
	}
	
	@Test
	public void testDistanceTo2() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(3, 4, 6);
		
		assertEquals(1.0, v1.distanceTo(v2), 0.0000001);
	}
	
	@Test
	public void testDistanceTo3() {
		Vector3 v1 = new Vector3(3, 4, 5);
		Vector3 v2 = new Vector3(5, 6, 7);
		
		assertEquals(Math.sqrt(12), v1.distanceTo(v2), 0.0000001);
	}
	

}
