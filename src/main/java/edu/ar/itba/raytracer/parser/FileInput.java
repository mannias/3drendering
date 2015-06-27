package edu.ar.itba.raytracer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.materials.Matte;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.Texture;
import edu.ar.itba.raytracer.vector.Matrix44;

public class FileInput {

	private final BufferedReader file;
	private final Scene scene;
	private final File originalFile;
	private final HashMap<String, Texture> textureMap = new HashMap<>();
	private final HashMap<String, Material> materialMap = new HashMap<>();
	final Stack<Matrix44> transforms = new Stack<>();
	
	final Path path;

	public FileInput(File file) throws FileNotFoundException, IOException {
		this.file = new BufferedReader(new FileReader(file));
		originalFile = file;
		this.scene = new Scene();
		
		path = file.toPath();
	}

	public Scene parse(final int aaSamples, final int rayDepth) throws IOException {
		String line;
		transforms.push(Matrix44.ID);
		Matrix44 cameraTransform = Matrix44.ID;
		CameraParser cameraParser = new CameraParser();
		while ((line = file.readLine()) != null) {
			if (line.contains("LookAt")) {
				cameraParser.parseLookAt(mergeLine(line, file));
			} else if (line.contains("Camera")) {
				cameraTransform = transforms.peek();
				cameraParser.parseFov(mergeLine(line, file));
			} else if (line.contains("Film")) {
				cameraParser.parseDimension(mergeLine(line, file));
			} else if (line.contains("WorldBegin")) {
				cameraParser.setCamera(scene, cameraTransform, aaSamples, rayDepth);
				parseWorld();
			}
		}

		final long start = System.currentTimeMillis();
		KdTree tree = KdTree.from(scene);
		System.out.println("Finished building tree in "
				+ (System.currentTimeMillis() - start));

		scene.setTree(tree);
		
		return scene;
	}

	private void parseWorld() throws IOException {
		String line, filename;
		final String filerx = "Include \"([^\"]+)\"";
		Matcher m;
		while (!(line = file.readLine()).contains("WorldEnd")) {
			if (line.contains("AttributeBegin")
					|| line.contains("TransformBegin")) {
				parseAttribute(file);
			} else if (line.contains("Include")) {
				if ((m = Pattern.compile(filerx).matcher(line)).find()) {
					filename = m.group(1);
					if (filename.contains("lxm")) {
						parseLxm(filename);
					} else if (filename.contains("lxo")) {
						parseLxo(filename);
					}
				}
			}
		}
	}

	private void parseLxm(String filename) {
		
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					path.resolveSibling(filename).toFile()));
			while ((line = reader.readLine()) != null) {
				if (line.contains("Texture")) {
					TextureParser.parseTexture(mergeLine(line, reader),
							textureMap, path);
				} else if (line.contains("MakeNamedMaterial")) {
					MaterialParser.parseNamedMaterial(mergeLine(line, reader),
							materialMap, textureMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseLxo(String filename) {
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					path.resolveSibling(filename).toFile()));
			while ((line = reader.readLine()) != null) {
				if (line.contains("AttributeBegin")) {
					parseAttribute(reader);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void parseAttribute(BufferedReader reader) throws IOException {
		String line;
        //default material
		Material material = new Matte(new Color(1,1,1));
		transforms.push(transforms.peek());
		while (!(line = reader.readLine()).contains("AttributeEnd")) {
			if (line.contains("NamedMaterial")) {
				material = MaterialParser.getNamedMaterial(line, materialMap);
			} else if (line.contains("Material")) {
				material = MaterialParser.Parse(mergeLine(line, reader),
						textureMap);
			} else if (line.contains("Shape")) {
				scene.add(ShapeParser.Parse(mergeLine(line, reader), material,
						transforms.peek()));
			} else if (line.contains("Texture")) {
				TextureParser.parseTexture(mergeLine(line, reader), textureMap, path);
			} else if(line.contains("AreaLightSource")) {
	                material.setLight(LightParser.parseLight(mergeLine(line, reader), transforms.peek()));
			} else if (line.contains("LightSource")) {
				scene.addLight(LightParser.parseLight(mergeLine(line, reader),
						transforms.peek()));
			} else if (line.contains("TransformBegin")) {
				parseAttribute(reader);
			} else if (line.contains("TransformEnd")) {
				break;
			} else if (line.contains("AttributeBegin")) {
				parseAttribute(reader);
			} else {
				TransformationParser.parseTransformation(line, transforms);
			}
		}
		transforms.pop();
	}

    private String mergeLine(String actual, BufferedReader reader) throws IOException {
        StringBuilder line = new StringBuilder(actual.replaceAll("[\\t]+", ""));
        String read;
        while ((read = reader.readLine()) != null && !read.isEmpty()) {
            line.append(read.replaceAll("[\\t]+", " "));
        }
        return line.toString();
    }

}
