package edu.ar.itba.raytracer.parser;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.texture.Texture;
import edu.ar.itba.raytracer.vector.Matrix44;

public class FileInput {

	private final BufferedReader file;
	private final Scene scene;

	public FileInput(File file) throws FileNotFoundException {
		this.file = new BufferedReader(new FileReader(file));
		this.scene = new Scene();
	}

	public void parse() {
		String line;
		final Stack<Matrix44> transforms = new Stack<>();
		transforms.push(Matrix44.ID);

		Matrix44 cameraTransform = Matrix44.ID;
		
		try {
			while ((line = file.readLine()) != null) {
				String[] elements = line.split(" ");
				if (elements[0].equals("LookAt")) {
					CameraParser.parseLookAt(line);
				} else if (elements[0].equals("Camera")) {
					cameraTransform = transforms.peek();
					CameraParser.parseFov(line);
				} else if (elements[0].equals("Film")) {
					CameraParser.parseDimension(line);
				} else if (elements[0].equals("WorldBegin")) {
					CameraParser.getCamera(scene, cameraTransform);
					parseWorld(transforms);
				} else {
					parseTransformation(elements, transforms);
				}
			}

			final long start = System.currentTimeMillis();
			KdTree tree = KdTree.from(scene);
			System.out.println("Finished building tree in "
					+ (System.currentTimeMillis() - start));
			scene.setTree(tree);
			int i = 0;
			for (Camera camera : scene.getCameras()) {
				final BufferedImage image = camera.render();
				ImageIO.write(image, "png", new File("pic" + i++ + ".png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseWorld(final Stack<Matrix44> transforms) throws IOException {
		String line;
		while (!(line = file.readLine()).contains("WorldEnd")) {
			if (line.contains("AttributeBegin")) {
				parseAttribute(transforms);
			}
		}
	}

	private void parseAttribute(final Stack<Matrix44> transforms) throws IOException {
		String line;
		Material material = null;
		Instance instance = null;
		HashMap<String, Texture> textureMap = new HashMap<>();
		while (!(line = file.readLine()).contains("AttributeEnd")) {
			if (line.contains("Material")) {
				material = MaterialParser.Parse(line, textureMap);
			} else if (line.contains("Shape") && line.contains("mesh")) {
				instance = ShapeParser.ParseMesh(line, material, file, transforms.peek());
			} else if (line.contains("Shape")) {
				instance = ShapeParser.Parse(line, material, transforms.peek());
			} else if (line.contains("Texture")) {
				TextureParser.parseTexture(line, textureMap);
			} else if (line.contains("LightSource")) {
				scene.addLight(LightParser.parseLight(line, transforms.peek()));
			} else if (line.contains("TransformBegin")) {
				transforms.push(transforms.peek());
			} else if (line.contains("TransformEnd")) {
				transforms.pop();
			} else {
				parseTransformation(line.split(" "), transforms);
			}
		}
		if (instance != null && material != null) {
			instance.material = material;
			scene.add(instance);
		}
	}

	private static void parseTransformation(final String[] elements, final Stack<Matrix44> transforms) throws IOException {
		if (elements[0].equals("Identity")) {
			transforms.pop();
			transforms.push(Matrix44.ID);
		} else if (elements[0].equals("Scale")) {
			transforms.push(GeometricObject.scaleMatrix(
					Double.parseDouble(elements[1]),
					Double.parseDouble(elements[2]),
					Double.parseDouble(elements[3])).multiply(
					transforms.pop()));
		} else if (elements[0].equals("Translate")) {
			transforms.push(GeometricObject.translationMatrix(
					Double.parseDouble(elements[1]),
					Double.parseDouble(elements[2]),
					Double.parseDouble(elements[3])).multiply(
					transforms.pop()));
		} else if (elements[0].equals("Rotate")) {
			if (elements[2].equals("1")) {
				transforms.push(GeometricObject.rotationXMatrix(Double
						.parseDouble(elements[1])));
			} else if (elements[3].equals("1")) {
				transforms.push(GeometricObject.rotationYMatrix(Double
						.parseDouble(elements[1])));
			} else if (elements[4].equals("1")) {
				transforms.push(GeometricObject.rotationZMatrix(Double
						.parseDouble(elements[1])));
			}
		}
	}
}
