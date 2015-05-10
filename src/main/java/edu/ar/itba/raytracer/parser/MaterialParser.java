package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.Texture;
import edu.ar.itba.raytracer.materials.Glass;
import edu.ar.itba.raytracer.materials.Matte;
import edu.ar.itba.raytracer.materials.Metal2;
import edu.ar.itba.raytracer.materials.Mirror;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialParser {

    public static Material Parse(String line, List<Texture> textures){
        Material mat = null;
        if(line.contains("matte")){
            mat = parseMatte(line);
        }else if(line.contains("glass")){
            mat = parseGlass(line);
        }else if(line.contains("mirror")){
            mat = parseMirror(line);
        }else if(line.contains("metal2")){
            mat = parseMetal2(line);
        }
        return mat;
    }

    private static Material parseMatte(String line){
        Color diffuseColor = null;
        String normal = "\"color Kd\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        Pattern pattern = Pattern.compile(normal);
        Matcher m = pattern.matcher(line);
        if(m.find()) {
            diffuseColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        return new Matte(diffuseColor);
    }

    private static Material parseGlass(String line){
        Color reflectivity = new Color(1.0,1.0,1.0), transmited =  new Color(1.0,1.0,1.0);
        double refractionIndex = 1.5;
        String kr = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String kt = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String index = "\"float index\" (\\d?\\.\\d+)";
        Matcher m = Pattern.compile(kr).matcher(line);
        if(m.find()) {
            reflectivity = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }

        m = Pattern.compile(kt).matcher(line);
        if(m.find()) {
            transmited = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }

        m = Pattern.compile(index).matcher(line);
        if(m.find()) {
            refractionIndex = Double.valueOf(m.group(1));
        }
        return new Glass(reflectivity,transmited,refractionIndex);
    }

    private static Material parseMirror(String line){
        Color reflectivity = new Color(1.0,1.0,1.0);
        String kr = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        Matcher m = Pattern.compile(kr).matcher(line);
        if(m.find()) {
            reflectivity = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        return new Mirror(reflectivity);
    }

    private static Material parseMetal2(String line){
        double uroughness = 0.001,vroughness = 0.001;
        Matcher m;
        String matcher = "\"float uroughness\" (\\d?\\.\\d+)";
        if((m = Pattern.compile(matcher).matcher(line)).find()) {
            uroughness = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(matcher).matcher(line)).find()) {
            vroughness = Double.valueOf(m.group(1));
        }
        return new Metal2(uroughness,vroughness);
    }
}
