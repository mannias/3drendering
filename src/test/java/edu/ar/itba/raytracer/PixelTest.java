package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
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
		cameraTransform.setPosition(new Vector4(-7, 0, -5, 1));
		cameraTransform.setRotation(new Vector4(0, 45, 0, 0));
		final Camera camera = scene.addCamera(width, height, 60,
				cameraTransform);

		Instance i = new Instance(new Sphere2());
		i.material = new Material(new Color(1, 0, 0), 1, 1, 1, 500, 0, 1);
		i.translate(0, 0, 0);
		// i.rotateY(50);
		// i.rotateZ(-45);
		i.scale(2, 2, 2);
		i.rotateX(90);

		// i.rotateX(2);
		scene.add(i);

		Instance i1 = new Instance(new Sphere2());
		i1.material = new Material(new Color(0, 1, 0), 1, 1, 1, 999, 0, 1);
		i1.translate(0, 0, -3);
		// i1.rotateY(90);
//		i1.rotateZ(45);
		i1.scale(2, 1, 1);
		// i1.rotateX(2);
		scene.add(i1);

		final long start = System.currentTimeMillis();
		KdTree tree = KdTree.from(scene);
		System.out.println("Finished building tree in "
				+ (System.currentTimeMillis() - start));

		final Transform lightTransform = new Transform();
		lightTransform.setPosition(new Vector4(0, 5, 0, 1));
		final LightProperties lightProperties = new LightProperties(new Color(
				1f, 1f, 1f));
		scene.addLight(lightTransform, lightProperties);

		final Transform lightTransform2 = new Transform();
		lightTransform2.setPosition(new Vector4(-5, 5, -2, 0));
		final LightProperties lightProperties2 = new LightProperties(new Color(
				1, 0, 0));
		scene.addLight(lightTransform2, lightProperties2);

		scene.setTree(tree);

		return camera;
	}
}
