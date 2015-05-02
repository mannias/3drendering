package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.shape.Sphere2;
import edu.ar.itba.raytracer.vector.Vector4;

public class PixelTest {

	@Test
	public void testPicture() throws IOException, InterruptedException {
		final int height = 480;
		final int width = 640;
		Camera camera = createScene(width, height);
		final BufferedImage image = camera.render(width, height);

		ImageIO.write(image, "png", new File("pic.png"));
	}

	private Camera createScene(final int width, final int height) {
		final Scene scene = new Scene(new Color(0.2, 0.2, 0.2));
		final Transform cameraTransform = new Transform();
		cameraTransform.setPosition(new Vector4(0, 0, -10, 0));
		cameraTransform.setRotation(new Vector4(0, 0, 0, 0));
		final Camera camera = scene.addCamera(width, height, 60,
				cameraTransform);

		// final Transform planeTransform = new Transform();
		// planeTransform.setPosition(new Vector33(0, -5, 0));
		// planeTransform.setRotation(new Vector33(0, 0, 0));
		// scene.addPlane(planeTransform, new ShapeProperties(new Material(
		// new Color(.1f, .1f, 1f), 1, 1, 0, 0, 0, 1.52)));

//		final ShapeProperties sphereProperties = new ShapeProperties(
//				new Material(new Color(1f, .1f, 1f), 1, 1, 1, 999, 0, 1.52));
//		scene.addSphere(new Vector33(-3, 0, 0), 2, sphereProperties);
		
		
		Instance i = new Instance(new Sphere2());
		i.translate(1, 0, 0);
		scene.add(i);
		
		//
		// final ShapeProperties sphere2Properties = new ShapeProperties(
		// new Material(new Color(0f, 0f, 0f), 1, 1, 0, 50, 1, 1.52));
		// scene.addSphere(new Vector33(-1, 0f, -5), 2, sphere2Properties);
		//
		// final ShapeProperties sphere3Properties = new ShapeProperties(
		// new Material(new Color(0f, 1f, 0f), 1, 1, 1, 999, 0, 1.52));
		// scene.addSphere(new Vector33(1, 0f, -10), 2, sphere3Properties);
		//
		// final ShapeProperties sphere4Properties = new ShapeProperties(
		// new Material(new Color(1f, 1f, 0f), 1, 1, 1, 999, 0, 1.52));
		// scene.addSphere(new Vector33(13, 5f, -10), 2, sphere4Properties);

		// final ShapeProperties boxProperties = new ShapeProperties(new
		// Color(1f,
		// .1f, 1f));
		// final Transform boxTransform = new Transform();
		// boxTransform.setPosition(new Vector3(2, 0, 3));
		// boxTransform.setRotation(new Vector3(45, 45, 0));
		// scene.addBox(boxTransform, boxProperties);

		// final ShapeProperties boundedPlaneProperties = new ShapeProperties(
		// new Color(1f, .1f, 1f));
		// final Transform boundedPlaneTransform = new Transform();
		// boundedPlaneTransform.setPosition(new Vector3(-4, 0, 2));
		// boundedPlaneTransform.setRotation(new Vector3(-135, -90, 45));
		// boundedPlaneTransform.setScale(new Vector3(1, 1, 2));
		// // boundedPlaneTransform.setScale(new Vector3(10, 10, 10));
		// scene.addBoundedPlane(boundedPlaneTransform, boundedPlaneProperties);

		// final Transform boundedPlane2Transform = new Transform();
		// boundedPlane2Transform.setPosition(new Vector3(-2, 0, 0));
		// boundedPlane2Transform.setRotation(new Vector3(45, 90, 0));
		// // boundedPlaneTransform.setScale(new Vector3(10, 10, 10));
		// scene.addBoundedPlane(boundedPlane2Transform,
		// boundedPlaneProperties);

		// final int triangles = 5000;
		// for (int i = 0; i < triangles; i++) {
		// final Vector4 vertex0 = new Vector4(Math.random() * 20 - 10,
		// Math.random() * 20 - 10, Math.random() * 20 - 10, 0);
		// final Vector4 edge1 = new Vector4(Math.random() * 2 - 1,
		// Math.random() * 2 - 1, Math.random() * 2 - 1, 0);
		// edge1.normalize();
		// final Vector4 edge2 = new Vector4(Math.random() * 2 - 1,
		// Math.random() * 2 - 1, Math.random() * 2 - 1, 0);
		// edge2.normalize();
		// final Vector4 v1 = new Vector4(vertex0);
		// v1.add(edge1);
		// final Vector4 v2 = new Vector4(vertex0);
		// v2.add(edge2);
		//
		// scene.addTriangle(vertex0, v1, v2);
		// }
		// final double z = 0;
		// scene.addTriangle(new Vector33(1, 0, z), new Vector33(0, 1, z),
		// new Vector33(-1, 0, z));

		// int spheres = 500;
		// double x = 0;
		// for (int i = 0; i < spheres; i++) {
		// Vector33 center = new Vector33(Math.random() * 21 - 10,
		// Math.random() * 11 - 5, Math.random() * 21 - 10);
		// x += center.x;
		// scene.addSphere(center, 1,
		// new ShapeProperties(new Material(new Color(1f, 1f, 1f), 1,
		// 1, 1, 999, 0, 1.52)));
		// }

		final long start = System.currentTimeMillis();
		KdTree tree = KdTree.from(scene);
		System.out.println("Finished building tree in "
				+ (System.currentTimeMillis() - start));

		final Transform lightTransform = new Transform();
		lightTransform.setPosition(new Vector4(0, 10, 0, 0));
		final LightProperties lightProperties = new LightProperties(new Color(
				1f, 1f, 1f));
		scene.addLight(lightTransform, lightProperties);

		final Transform lightTransform2 = new Transform();
		lightTransform2.setPosition(new Vector4(-5, 5, -2, 0));
		final LightProperties lightProperties2 = new LightProperties(new Color(
				1, 0, 0));
		scene.addLight(lightTransform2, lightProperties2);

		// final Transform lightTransform3 = new Transform();
		// lightTransform3.setPosition(new Vector33(0, 0, -25));
		// final LightProperties lightProperties3 = new LightProperties(new
		// Color(
		// 1, 0, 0));
		// scene.addLight(lightTransform3, lightProperties3);

		scene.setTree(tree);

		return camera;
	}
}
