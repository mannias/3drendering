package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.shape.SceneShape;

import java.io.*;

public class FileInput {

    BufferedReader file;

    public FileInput(File file) throws FileNotFoundException {
        this.file = new BufferedReader(new FileReader(file));
    }

    public void parse(){
        String line;
        Scene scene = new Scene();
        try {
            while((line = file.readLine()) != null){
                String[] elements = line.split(" ");
                if(elements[0].compareTo("LookAt") == 0){

                }else if(elements[0].compareTo("Camera") == 0){
                    CameraParser.Parse(line, scene);
                }else if(elements[0].compareTo("Film") == 0){

                }else if(elements[0].compareTo("WorldBegin") == 0){
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
        while((line = file.readLine()).compareTo("AttributeEnd") != 0){
            if(line.contains("Material")){

            }else if(line.contains("Shape")){
                SceneShape shape = ShapeParser.Parse(line);
            }else if(line.contains("Texture")){

            }else if(line.contains("LightSource")){

            }
        }
    }
}
