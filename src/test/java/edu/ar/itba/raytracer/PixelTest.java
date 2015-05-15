package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.ar.itba.raytracer.shape.*;
import edu.ar.itba.raytracer.vector.Vector3;
import org.junit.Test;

import edu.ar.itba.raytracer.light.LightProperties;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
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
		final Scene scene = new Scene(new Color(1, 1, 1));
		final Transform cameraTransform = new Transform();
		cameraTransform.setPosition(new Vector4(0, 0, -7, 1));
		cameraTransform.setRotation(new Vector4(0, 0, 0, 0));
		final Camera camera = scene.addCamera(width, height, 60,
				cameraTransform);

		// Instance i = new Instance(new Sphere2());
		// i.material = new Material(new Color(1, 0, 0), 1, 1, 1, 1, 0, 1);
		// i.translate(-2.5, 0, 0);
		// scene.add(i);
		//
		// Instance i1 = new Instance(new Sphere2());
		// i1.material = new Material(new Color(1, .5, 0), 1, 1, 1, 1, 0, 1);
		// scene.add(i1);
		//
		// Instance i2 = new Instance(new Sphere2());
		// i2.material = new Material(new Color(1, 1, 0), 1, 1, 1, 1, 0, 1);
		// i2.translate(2.5, 0, 0);
		// scene.add(i2);
		//
		// Instance i3 = new Instance(new Sphere2());
		// i3.material = new Material(new Color(0, 1, 0), 1, 1, 1, 1, 0, 1);
		// i3.translate(5, 0, 0);
		// scene.add(i3);

		// Instance i5 = new Instance(new Triangle(new Vector4(0, 0, -4, 1),
		// new Vector4(1, 0, -4, 1), new Vector4(0, 1, -4, 1)));
		// i5.material = new Material(new Color(0, 0, 1), 1, 1, 1, 50, 0, 1);
		// // i2.translate(-2, 0, 0);
		// // i5.rotateY(20);
		// i5.scale(2, 1, 1);
		// scene.add(i5);

//		final List<Triangle> triangles = new ArrayList<>();
//		triangles.add(new Triangle(new Vector4(-.5, -.5, -.5, 1), new Vector4(
//				.5, -.5, -.5, 1), new Vector4(-.5, .5, -.5, 1), new Vector4(0,
//				0, -1, 0)));
//		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
//				-.5, .5, -.5, 1), new Vector4(.5, .5, -.5, 1), new Vector4(0,
//				0, -1, 0)));
//		triangles.add(new Triangle(new Vector4(.5, .5, -.5, 1), new Vector4(.5,
//				.5, .5, 1), new Vector4(.5, -.5, .5, 1),
//				new Vector4(1, 0, 0, 0)));
//		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
//				.5, .5, -.5, 1), new Vector4(.5, -.5, .5, 1), new Vector4(1, 0,
//				0, 0)));
//		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
//				-.5, -.5, -.5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(
//				-1, 0, 0, 0)));
//		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
//				-.5, .5, .5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(-1,
//				0, 0, 0)));
//		triangles.add(new Triangle(new Vector4(-.5, -.5, .5, 1), new Vector4(
//				.5, -.5, .5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0, 0,
//				1, 0)));
//		triangles.add(new Triangle(new Vector4(.5, -.5, .5, 1), new Vector4(
//				-.5, .5, .5, 1), new Vector4(.5, .5, .5, 1), new Vector4(0, 0,
//				1, 0)));
//		triangles.add(new Triangle(new Vector4(-.5, -.5, -.5, 1), new Vector4(
//				.5, -.5, -.5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(0,
//				-1, 0, 0)));
//		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
//				.5, -.5, .5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(0,
//				-1, 0, 0)));
//		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
//				.5, .5, -.5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0,
//				-1, 0, 0)));
//		triangles.add(new Triangle(new Vector4(.5, .5, -.5, 1), new Vector4(.5,
//				.5, .5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0, -1, 0,
//				0)));
//
//		final Instance i7 = new Instance(new Mesh(triangles));
//		i7.material = new Material(new Color(0, 0, 1), new Color(0, 0, 1), 1d, 50, 0, 1);
//		// i7.translate(4, 1, 0);
//		i7.rotateX(90);
//		scene.add(i7);

        final Instance i10 = new Instance(new Triangle(new Vector4(0,0,0,0), new Vector4(1,1,0,0), new Vector4(2,0,0,0), new Vector4(0,0,-1,0)));
        i10.translate(0,0,0);
        i10.material = new Material(new Color(0,0,0), new Color(0.2775, 0.2775, 0.2775), 0.774597, 560, 0, 0);
        scene.add(i10);

//        final Instance i9 = new Instance(new Sphere2(1));
//        i9.translate(1,1,-3);
//        i9.material = new Material(new Color(0,0,0), new Color(0.2775, 0.2775, 0.2775), 0.774597, 560, 0, 0);
//        scene.add(i9);

        final Instance i8 = new Instance(new Sphere2(1));
        i8.translate(-1,-1,-2);
        i8.material = new Material(new Color(0, 0, 0), new Color(0.9, 0.9, 0.9), 1, 750, 1, 1.62);
        scene.add(i8);
//
//        final Instance i10 = new Instance(new Plane2(new Vector4(0,0,-1,0)));
//        i10.material = new Material(new Color(0, 0, 0.0), new Color(.1,.6,.1), .5,50 , 0, 0);
//        scene.add(i10);

        final long start = System.currentTimeMillis();
		KdTree tree = KdTree.from(scene);
		System.out.println("Finished building tree in "
				+ (System.currentTimeMillis() - start));

		// final Transform lightTransform = new Transform();
		// lightTransform.setPosition(new Vector4(0, 5, 0, 1));
		// final LightProperties lightProperties = new LightProperties(new
		// Color(
		// 1f, 1f, 1f));
		// scene.addLight(lightTransform, lightProperties);

		final Transform lightTransform2 = new Transform();
		lightTransform2.setPosition(new Vector4(0, 0, -7, 1));
		final LightProperties lightProperties2 = new LightProperties(new Color(
                1, 1, 1));
		scene.addLight(lightTransform2, lightProperties2);
//        final Transform lightTransform3 = new Transform();
//        lightTransform2.setPosition(new Vector4(7, 8, 0, 1));
//        scene.addLight(lightTransform3, new LightProperties(new Color(1, 1, 1)));

		scene.setTree(tree);

		return camera;
	}
}
