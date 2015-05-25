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
import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.shape.MeshTriangle;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class Main {

	@Parameters
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

		final int height = 480;
		final int width = 640;
		BufferedImage image = null;
		final Scene scene = new FileInput(new File(parameters.input)).parse(
				parameters.aaSamples, parameters.rayDepth);

		// We support multiple cameras, but we'll only be taking pictures from
		// one.
		final Camera c = scene.getCameras().iterator().next();
		for (int i = 0; i < parameters.benchmark; i++) {
			image = c.render(width, height);
		}

		ImageIO.write(image, extension, new File(parameters.output));
	}

	@Deprecated
	private static Mesh parseObj(final String path) throws Exception {
		try (final Scanner scanner = new Scanner(Paths.get(path));) {
			final List<Vector4> vertexes = new ArrayList<>();
			final List<Vector4> vertexNormals = new ArrayList<>();
			final List<Vector2> vertexTextures = new ArrayList<>();
			final Collection<List<Integer>> normalMap = new ArrayList<>();
			final List<MeshTriangle> triangles = new ArrayList<>();
			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			double minZ = Double.MAX_VALUE;
			double maxX = -Double.MAX_VALUE;
			double maxY = -Double.MAX_VALUE;
			double maxZ = -Double.MAX_VALUE;
			int i = 0;
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				final String[] tokens = line.split("\\s+");
				if (tokens[0].equals("v")) {
					final double x = Double.parseDouble(tokens[1]);
					final double y = Double.parseDouble(tokens[2]);
					final double z = Double.parseDouble(tokens[3]);
					if (x > maxX) {
						maxX = x;
					}
					if (x < minX) {
						minX = x;
					}
					if (y < minY) {
						minY = y;
					}
					if (z < minZ) {
						minZ = z;
					}
					if (y > maxY) {
						maxY = y;
					}
					if (z > maxZ) {
						maxZ = z;
					}
					System.out.println(i + " " + x + " " + y + " " + z);
					i++;
					vertexes.add(new Vector4(x, y, z, 1));
				} else if (tokens[0].equals("f")) {
					if (tokens.length > 4) {
						System.out.println(Arrays.toString(tokens));
						throw new AssertionError();
					}
					final String[] p1s = tokens[1].split("/");
					final String[] p2s = tokens[2].split("/");
					final String[] p3s = tokens[3].split("/");

					final int p1 = Integer.parseInt(p1s[0]);
					final int p2 = Integer.parseInt(p2s[0]);
					final int p3 = Integer.parseInt(p3s[0]);

					final int pt1, pt2, pt3;
					if (!p1s[1].equals("")) {
						pt1 = Integer.parseInt(p1s[1]);
						pt2 = Integer.parseInt(p2s[1]);
						pt3 = Integer.parseInt(p3s[1]);
					} else {
						pt1 = pt2 = pt3 = 0;
					}
					final int pn1 = Integer.parseInt(p1s[2]);
					final int pn2 = Integer.parseInt(p2s[2]);
					final int pn3 = Integer.parseInt(p3s[2]);

					normalMap.add(Arrays.asList(p1, p2, p3, pt1, pt2, pt3, pn1,
							pn2, pn3));
				} else if (tokens[0].equals("vn")) {
					final double p1 = Double.parseDouble(tokens[1]);
					final double p2 = Double.parseDouble(tokens[2]);
					final double p3 = Double.parseDouble(tokens[3]);
					vertexNormals.add(new Vector4(p1, p2, p3, 0));
				} else if (tokens[0].equals("vt")) {
					final double p1 = Double.parseDouble(tokens[1]);
					final double p2 = Double.parseDouble(tokens[2]);
					vertexTextures.add(new Vector2(p1, p2));
				}
			}

			for (final List<Integer> c : normalMap) {
				triangles
						.add(new MeshTriangle(vertexes.get(c.get(0) - 1),
								vertexes.get(c.get(1) - 1), vertexes.get(c
										.get(2) - 1),
								!vertexTextures.isEmpty() ? vertexTextures
										.get(c.get(3)) : null, !vertexTextures
										.isEmpty() ? vertexTextures.get(c
										.get(4)) : null, !vertexTextures
										.isEmpty() ? vertexTextures.get(c
										.get(5)) : null, vertexNormals.get(c
										.get(6) - 1), vertexNormals.get(c
										.get(7) - 1), vertexNormals.get(c
										.get(8) - 1)));
			}

			System.out.println("Loaded obj with " + triangles.size()
					+ " triangles.");

			return new Mesh(triangles);
		}
	}

}
