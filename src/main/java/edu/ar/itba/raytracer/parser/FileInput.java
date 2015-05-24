package edu.ar.itba.raytracer.parser;


import edu.ar.itba.raytracer.*;
import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.light.LightProperties;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.texture.Texture;
import edu.ar.itba.raytracer.vector.Vector4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

public class FileInput {

    private final BufferedReader file;
    private final Scene scene;

    public FileInput(File file) throws FileNotFoundException {
        this.file = new BufferedReader(new FileReader(file));
        this.scene = new Scene(new Color(1,1,1));
    }

    public void parse(){
        String line;
        CameraParser cameraParser = new CameraParser();
        try {
            while((line = file.readLine()) != null){
                String[] elements = line.split(" ");
                if(elements[0].compareTo("LookAt") == 0){
                    cameraParser.parseLookAt(line);
                }else if(elements[0].compareTo("Camera") == 0){
                    cameraParser.parseFov(line);
                }else if(elements[0].compareTo("Film") == 0){
                    cameraParser.parseDimension(line);
                }else if(elements[0].compareTo("WorldBegin") == 0){
                    cameraParser.setCamera(scene);
                    parseWorld();
                }
            }

            final Transform light2Transform = new Transform();
            light2Transform.setPosition(new Vector4(-2, 5, 5, 1));
            final LightProperties light2Properties = new LightProperties(new Color(
                    1f, 1f, 1f));
            scene.addLight(new DirectionalLight(new Vector4(0,-1,1,0), light2Properties));
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
