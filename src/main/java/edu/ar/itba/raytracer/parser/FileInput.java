package edu.ar.itba.raytracer.parser;


import edu.ar.itba.raytracer.GeometricObject;
import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.texture.Texture;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;

public class FileInput {

    private final BufferedReader file;
    private final Scene scene;

    public FileInput(File file) throws FileNotFoundException {
        this.file = new BufferedReader(new FileReader(file));
        this.scene = new Scene();
    }

    public void parse(){
        String line;
        try {
            while((line = file.readLine()) != null){
                String[] elements = line.split(" ");
                if(elements[0].compareTo("LookAt") == 0){

                }else if(elements[0].compareTo("Camera") == 0){
                    CameraParser.parseFov(line);
                }else if(elements[0].compareTo("Film") == 0){
                    CameraParser.parseDimension(line);
                }else if(elements[0].compareTo("WorldBegin") == 0){
                    CameraParser.getCamera(scene);
                    parseWorld();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseWorld() throws IOException {
        String line;
        while((line = file.readLine()).compareTo("WorldEnd") != 0){
            String[] elements = line.split(" ");
            if(elements[0].compareTo("AttributeBegin") == 0){
                parseAttribute();
            }
        }
    }

    private void parseAttribute() throws IOException {
        String line;
        Material material = null;
        Instance instance = null;
        HashMap<String, Texture> textureMap = new HashMap<>();
        while((line = file.readLine()).compareTo("AttributeEnd") != 0){
            if(line.contains("Material")){
                material = MaterialParser.Parse(line, textureMap);
            }else if(line.contains("Shape")){
                instance = ShapeParser.Parse(line,material);
            }else if(line.contains("Texture")){
                TextureParser.parseTexture(line,textureMap);
            }else if(line.contains("LightSource")){

            }
        }
        scene.add(instance);
    }
}
