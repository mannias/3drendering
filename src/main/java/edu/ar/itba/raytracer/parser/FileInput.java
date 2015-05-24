package edu.ar.itba.raytracer.parser;


import edu.ar.itba.raytracer.*;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInput {

    private final BufferedReader file;
    private final Scene scene;
    private final HashMap<String, Texture> textureMap = new HashMap<>();
    private final HashMap<String, Material> materialMap = new HashMap<>();

    public FileInput(File file) throws FileNotFoundException {
        this.file = new BufferedReader(new FileReader(file));
        this.scene = new Scene(new Color(1,1,1));
    }

    public void parse(){
        String line;
        CameraParser cameraParser = new CameraParser();
        try {
            while((line = file.readLine()) != null){
                String[] elements = line.split("\\s");
                if(elements[0].compareTo("LookAt") == 0){
                    cameraParser.parseLookAt(mergeLine(line, file));
                }else if(elements[0].compareTo("Camera") == 0){
                    cameraParser.parseFov(mergeLine(line, file));
                }else if(elements[0].compareTo("Film") == 0){
                    cameraParser.parseDimension(mergeLine(line, file));
                }else if(elements[0].compareTo("WorldBegin") == 0){
                    cameraParser.setCamera(scene);
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
        String line, filename;
        final String filerx = "Include \"([^\"]+)\"";
        Matcher m;
        while(!(line = file.readLine()).contains("WorldEnd")){
            if(line.contains("AttributeBegin")){
                parseAttribute(file);
            }else if(line.contains("Include")){
                if((m = Pattern.compile(filerx).matcher(line)).find()) {
                    filename = m.group(1);
                    if(filename.contains("lxm")){
                        parseLxm(filename);
                    }else if(filename.contains("lxo")){
                        parseLxo(filename);
                    }
                }
            }
        }
    }

    private void parseLxm(String filename){
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while((line = reader.readLine()) != null) {
                if(line.contains("Texture")) {
                    TextureParser.parseTexture(mergeLine(line, reader), textureMap);
                }else if(line.contains("MakeNamedMaterial")){
                    MaterialParser.parseNamedMaterial(mergeLine(line, reader),materialMap,textureMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLxo(String filename){
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while((line = reader.readLine()) != null) {
                if(line.contains("AttributeBegin")) {
                    parseAttribute(reader);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void parseAttribute(BufferedReader reader) throws IOException {
        String line;
        Material material = null;
        Instance instance = null;
        while(!(line = reader.readLine()).contains("AttributeEnd")){
            if(line.contains("NamedMaterial")){
                material = MaterialParser.getNamedMaterial(line,materialMap);
            }else if(line.contains("Material")){
                material = MaterialParser.Parse(mergeLine(line, reader), textureMap);
            }else if(line.contains("Shape") && line.contains("mesh")){
                instance = ShapeParser.ParseMesh(mergeLine(line, reader));
            }else if(line.contains("Shape")){
                instance = ShapeParser.Parse(mergeLine(line, reader), material);
            }else if(line.contains("Texture")){
                TextureParser.parseTexture(mergeLine(line, reader), textureMap);
            }else if(line.contains("LightSource")){
                scene.addLight(LightParser.parseLight(mergeLine(line, reader)));
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
                TransformationParser.parseTranslate(mergeLine(line, file),instance);
            }else if(line.contains("Rotate")){
                TransformationParser.parseRotate(mergeLine(line, file), instance);
            }else if(line.contains("Scale")){
                TransformationParser.parseScale(mergeLine(line, file),instance);
            }
        }
    }

    private String mergeLine(String actual, BufferedReader reader) throws IOException {
        StringBuilder line = new StringBuilder(actual);
        String read;
        while((read = reader.readLine()) != null && !read.isEmpty()){
            line.append(read);
        }
        return line.toString();
    }
}
