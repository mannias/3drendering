package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import edu.ar.itba.raytracer.parser.FileInput;
import edu.ar.itba.raytracer.properties.RayTracerParameters;
import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.shape.MeshTriangle;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class Main {



	public static void main(String[] args) throws Exception {
		final RayTracerParameters parameters = new RayTracerParameters();
		final JCommander jCommander = new JCommander(parameters);
		jCommander.setProgramName("Ray tracer");
		jCommander.setAcceptUnknownOptions(true);
		try {
			jCommander.parse(args);
		} catch (final ParameterException e) {
			jCommander.usage();
			throw e;
		}

		final String output = parameters.output;
		final String extension;
		if (output == null) {
			final String input = parameters.input;
			final int inputExtension = input.lastIndexOf('.');
			if (inputExtension == -1) {
				parameters.output = input + ".png";
			} else {
				parameters.output = input.substring(0, inputExtension) + ".png";
			}
			extension = "png";
		} else {
			final int outputExtension = output.lastIndexOf('.');
			if (outputExtension == -1) {
				extension = "png";
			} else {
				extension = output.substring(outputExtension + 1);
			}
		}

		BufferedImage image = null;



		final Scene scene = new FileInput(new File(parameters.input)).parse(parameters);

		// We support multiple cameras, but we'll only be taking pictures from
		// one.
		final Camera c = scene.getCameras().iterator().next();
		final long globalStart = System.nanoTime();
		for (int i = 0; i < parameters.benchmark; i++) {
			final long start = System.nanoTime();
			int it = 0;
//			while(true) {
			image = c.render();
//			if (it  % 100 == 0) {
//			ImageIO.write(image, extension, new File(parameters.output));
//			}
			System.out.println(it);
			it++;
//			}
//			if (parameters.time) {
//				System.out.println("Time for render " + i + ": "
//						+ (System.nanoTime() - start) / 1e6 + " ms");
//			}
		}
		final long end = System.nanoTime();

		System.out.println("Total time: " + (end - globalStart) / 1e6 + " ms");
		System.out.println("Average time: " + (end - globalStart) * 1.0
				/ parameters.benchmark / 1e6 + " ms");

		ImageIO.write(image, extension, new File(parameters.output));
		System.out.println("Last image saved to " + parameters.output);
	}
}
