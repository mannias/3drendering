package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinTask;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;

public class PixelTest {

	@Test
	public void testPicture() throws IOException, InterruptedException {
		final int height = 480;
		final int width = 640;
		Camera camera = createScene(width, height);
		//
		// final int pixels = width * height;
		// final int cores = Runtime.getRuntime().availableProcessors();
		// // final int pixelsPerCore = pixels / cores;
		//
		// final ForkJoinTask<?>[] threads = new ForkJoinTask[cores];
		// final Runnable[] runnables = new Runnable[cores];
		// // final AtomicInteger pixel = new AtomicInteger();
		// for (int i = 0; i < cores; i++) {
		// final int startPixel = i * pixels / cores;
		// final int endPixel;
		// if (i == cores - 1) {
		// endPixel = pixels;
		// } else {
		// endPixel = (i + 1) * pixels / cores;
		// }
		// runnables[i] = new Runnable() {
		// @Override
		// public void run() {
		// final long start = System.currentTimeMillis();
		// for (int i = startPixel; i < endPixel; i++) {
		// camera.castRay(i % width, i / width);
		// // j++;
		// // assertEquals(plane.getColor(), color);
		// }
		// System.out.println("TIME THREAD "
		// + (System.currentTimeMillis() - start) + " FOR "
		// + (endPixel - startPixel));
		// }
		// };
		// threads[i] = ForkJoinTask.adapt(runnables[i]);
		// }
		// final long start = System.currentTimeMillis();
		// for (ForkJoinTask<?> fjt : threads) {
		// fjt.fork();
		// }
		//
		// for (ForkJoinTask<?> fjt : threads) {
		// fjt.join();
		// }
		// System.out.println(System.currentTimeMillis() - start);
		//
		// final BufferedImage image = camera.takePicture();

		final BufferedImage image = camera.render(width, height);

		ImageIO.write(image, "png", new File("pic.png"));
	}

	private Camera createScene(final int width, final int height) {
		final Scene scene = new Scene(new Color(0.2, 0.2, 0.2));
		final Transform cameraTransform = new Transform();
		cameraTransform.setPosition(new Vector3(-1, 0, -20));
		cameraTransform.setRotation(new Vector3(4, 0, 0));
		final Camera camera = scene.addCamera(width, height, 50,
				cameraTransform);

		final Transform planeTransform = new Transform();
		planeTransform.setPosition(new Vector3(0, -5, 0));
		planeTransform.setRotation(new Vector3(0, 0, 0));
		scene.addPlane(planeTransform, new ShapeProperties(new Color(.1f, .1f,
				1f)));
		// scene.addPlane(new Vector3(0, -2, 0), new Vector3(0, 1, 0),
		// new ShapeProperties(new Color(0, 255, 0)));

		final ShapeProperties sphereProperties = new ShapeProperties(new Color(
				1f, .1f, 1f));
		scene.addSphere(new Vector3(-5, 0, 2), 2, sphereProperties);

		final ShapeProperties sphere2Properties = new ShapeProperties(
				new Color(1f, .1f, 1f));
		scene.addSphere(new Vector3(-1, 0f, -3), 2, sphere2Properties);

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

		// final int triangles = 10000;
		// for (int i = 0; i < triangles; i++) {
		// final Vector3 vertex0 = new Vector3(Math.random() * 20 - 10,
		// Math.random() * 20 - 10, Math.random() * 20 - 10);
		// final Vector3 edge1 = new Vector3(Math.random() * 2 - 1,
		// Math.random() * 2 - 1, Math.random() * 2 - 1).normalize();
		// final Vector3 edge2 = new Vector3(Math.random() * 2 - 1,
		// Math.random() * 2 - 1, Math.random() * 2 - 1).normalize();
		// scene.addTriangle(vertex0, vertex0.add(edge1), vertex0.add(edge2));
		// }
		// final double z = 0;
		// scene.addTriangle(new Vector3(1, 0, z), new Vector3(0, 1, z),
		// new Vector3(-1, 0, z));

		final Transform lightTransform = new Transform();
		lightTransform.setPosition(new Vector3(0, 10, 0));
		final LightProperties lightProperties = new LightProperties(new Color(
				1f, 1f, 1f));
		scene.addLight(lightTransform, lightProperties);
		
		final Transform lightTransform2 = new Transform();
		lightTransform2.setPosition(new Vector3(-5, 5, -2));
		final LightProperties lightProperties2 = new LightProperties(new Color(
				0, 1f, 0));
		scene.addLight(lightTransform2, lightProperties2);
		
		return camera;
	}
}
