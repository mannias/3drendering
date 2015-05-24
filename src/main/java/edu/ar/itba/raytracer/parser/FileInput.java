package edu.ar.itba.raytracer.parser;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.Texture;

public class FileInput {

    private final BufferedReader file;
    private final Scene scene;

    public FileInput(File file) throws FileNotFoundException {
        this.file = new BufferedReader(new FileReader(file));
        this.scene = new Scene(new Color(1,1,1));
    }

    public void parse(){
        String line;
        try {
            while((line = file.readLine()) != null){
                String[] elements = line.split(" ");
                if(elements[0].compareTo("LookAt") == 0){
                    CameraParser.parseLookAt(line);
                }else if(elements[0].compareTo("Camera") == 0){
                    CameraParser.parseFov(line);
                }else if(elements[0].compareTo("Film") == 0){
                    CameraParser.parseDimension(line);
                }else if(elements[0].compareTo("WorldBegin") == 0){
                    CameraParser.getCamera(scene);
                    parseWorld();
                }
            }

            final long start = System.currentTimeMillis();
            KdTree tree = KdTree.from(scene);
            System.out.println("Finished building tree in "
                    + (System.currentTimeMillis() - start));
            scene.setTree(tree);
            int i = 0;
            for(Camera camera: scene.getCameras()) {
                final BufferedImage image = camera.render();
                ImageIO.write(image, "png", new File("pic" + i++ + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseWorld() throws IOException {
        String line;
        while(!(line = file.readLine()).contains("WorldEnd")){
            if(line.contains("AttributeBegin")){
                parseAttribute();
            }
        }
    }

    private void parseAttribute() throws IOException {
        String line;
        Material material = null;
        Instance instance = null;
        HashMap<String, Texture> textureMap = new HashMap<>();
        while(!(line = file.readLine()).contains("AttributeEnd")){
            if(line.contains("Material")){
                material = MaterialParser.Parse(line, textureMap);
            }else if(line.contains("Shape") && line.contains("mesh")){
                instance = ShapeParser.ParseMesh(line,material,file);
            }else if(line.contains("Shape")){
                instance = ShapeParser.Parse(line, material);
            }else if(line.contains("Texture")){
                TextureParser.parseTexture(line,textureMap);
            }else if(line.contains("LightSource")){
                scene.addLight(LightParser.parseLight(line));
            }else if(line.contains("TransformBegin")){
                if(instance == null){
                    throw new IllegalArgumentException("Incorrect parameter order");
                }
                parseTransformation(instance);
            }
        }
        if(instance != null && material != null) {
            instance.material = material;
            scene.add(instance);
        }
    }

    private void parseTransformation(Instance instance) throws IOException {
        String line;
        while(!(line = file.readLine()).contains("TransformEnd")){
            if(line.contains("Translate")){
                TransformationParser.parseTranslate(line,instance);
            }else if(line.contains("Rotate")){
                TransformationParser.parseRotate(line, instance);
            }else if(line.contains("Scale")){
                TransformationParser.parseScale(line,instance);
            }
        }
    }
}
