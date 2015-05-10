package edu.ar.itba.raytracer.shape;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.light.LightProperties;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public class Main {

	public static class RayTracerParameters {
		@Parameter(names = "-i", required = true, description = "Nombre del archivo de entrada (definición de la escena)")
		private String input;
		@Parameter(names = "-o", description = "Nombre del archivo de salida, incluyendo su extensión. "
				+ "En caso de no indicarlo usará el nombre del archivo de input reemplazando la extensión "
				+ "y usando el formato PNG.")
		private String output = input;
		@Parameter(names = "-time", description = "Mostrará el tiempo empleado en el render")
		private boolean time = false;
		@Parameter(names = "-aa", required = true, description = "Cantidad de muestras de antialiasing.")
		private int aaSamples;
		@Parameter(names = "-benchmark", description = "Realizar el render completo n veces consecutivas.")
		private int benchmark = 1;
		@Parameter(names = "-d", description = "Define el ray depth de reflejos y refracciones.")
		private int rayDepth = 1;
	}

	public static void main(String[] args) throws IOException {
		final RayTracerParameters parameters = new RayTracerParameters();
		final JCommander jCommander = new JCommander(parameters);
		jCommander.setProgramName("Ray tracer");
		try {
			jCommander.parse(args);
		} catch (final ParameterException e) {
			jCommander.usage();
			throw e;
		}

		if (parameters.output == null) {
			final String input = parameters.input;
			final int extension = input.lastIndexOf('.');
			if (extension == -1) {
				parameters.output = input + ".png";
			} else {
				parameters.output = input.substring(0, extension) + ".png";
			}
		}

		System.out.println(parameters.output);

		final int height = 480;
		final int width = 640;
		BufferedImage image = null;
		for (int i = 0; i < parameters.benchmark; i++) {
			image = loadTestScene().render(width, height);
		}

		ImageIO.write(image, "png", new File("pic.png"));
	}

	private static Camera loadTestScene() {
		final Scene scene = new Scene(new Color(1, 1, 1));
		final Transform cameraTransform = new Transform();
		cameraTransform.setPosition(new Vector4(0, 0, -9, 1));
		cameraTransform.setRotation(new Vector4(0, 0, 0, 0));
		final Camera camera = scene.addCamera(640, 480, 60, cameraTransform);

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

		final List<Triangle> triangles = new ArrayList<>();
		triangles.add(new Triangle(new Vector4(-.5, -.5, -.5, 1), new Vector4(
				.5, -.5, -.5, 1), new Vector4(-.5, .5, -.5, 1), new Vector4(0,
				0, -1, 0)));
		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
				-.5, .5, -.5, 1), new Vector4(.5, .5, -.5, 1), new Vector4(0,
				0, -1, 0)));
		triangles.add(new Triangle(new Vector4(.5, .5, -.5, 1), new Vector4(.5,
				.5, .5, 1), new Vector4(.5, -.5, .5, 1),
				new Vector4(1, 0, 0, 0)));
		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
				.5, .5, -.5, 1), new Vector4(.5, -.5, .5, 1), new Vector4(1, 0,
				0, 0)));
		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
				-.5, -.5, -.5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(
				-1, 0, 0, 0)));
		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
				-.5, .5, .5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(-1,
				0, 0, 0)));
		triangles.add(new Triangle(new Vector4(-.5, -.5, .5, 1), new Vector4(
				.5, -.5, .5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0, 0,
				1, 0)));
		triangles.add(new Triangle(new Vector4(.5, -.5, .5, 1), new Vector4(
				-.5, .5, .5, 1), new Vector4(.5, .5, .5, 1), new Vector4(0, 0,
				1, 0)));
		triangles.add(new Triangle(new Vector4(-.5, -.5, -.5, 1), new Vector4(
				.5, -.5, -.5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(0,
				-1, 0, 0)));
		triangles.add(new Triangle(new Vector4(.5, -.5, -.5, 1), new Vector4(
				.5, -.5, .5, 1), new Vector4(-.5, -.5, .5, 1), new Vector4(0,
				-1, 0, 0)));
		triangles.add(new Triangle(new Vector4(-.5, .5, -.5, 1), new Vector4(
				.5, .5, -.5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0,
				-1, 0, 0)));
		triangles.add(new Triangle(new Vector4(.5, .5, -.5, 1), new Vector4(.5,
				.5, .5, 1), new Vector4(-.5, .5, .5, 1), new Vector4(0, -1, 0,
				0)));

		final Instance i7 = new Instance(new Mesh(triangles));
		i7.material = new Material(new Color(0, 0, 1), new Color(0, 0, 1), 1, 50, 0, 1);
		// i7.translate(4, 1, 0);
		i7.rotateX(90);
		scene.add(i7);

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
		lightTransform2.setPosition(new Vector4(0, 0, -6, 1));
		final LightProperties lightProperties2 = new LightProperties(new Color(
				1, 1, 1));
		scene.addLight(lightTransform2, lightProperties2);

		scene.setTree(tree);

		return camera;
	}

}
