package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Material;

import edu.ar.itba.raytracer.materials.Glass;
import edu.ar.itba.raytracer.materials.Matte;
import edu.ar.itba.raytracer.materials.Metal2;
import edu.ar.itba.raytracer.materials.Mirror;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialParser {

    public static Material Parse(String line,Map<String,Texture> textureMap){
        Material mat = null;
        if(line.contains("matte")){
            mat = parseMatte(line, textureMap);
        }else if(line.contains("glass")){
            mat = parseGlass(line, textureMap);
        }else if(line.contains("mirror")){
            mat = parseMirror(line, textureMap);
        }else if(line.contains("metal2")){
            mat = parseMetal2(line, textureMap);
        }
        return mat;
    }

    private static Material parseMatte(String line, Map<String,Texture> textureMap){
        Texture diffuseColor = new ConstantColorTexture(new Color(1,1,1));
        String kdcolor = "\"color Kd\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String kdtexture = "\"texture Kd\" \"([^\"]+)\"";
        Matcher m;
        if((m = Pattern.compile(kdcolor).matcher(line)).find()) {
            diffuseColor = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(kdtexture).matcher(line)).find()) {
            diffuseColor = textureMap.get(m.group(1));
        }
        return new Matte(diffuseColor);
    }

    private static Material parseGlass(String line, Map<String,Texture> textureMap){
        Color reflectivity = new Color(1.0,1.0,1.0), transmited =  new Color(1.0,1.0,1.0);
        double refractionIndex = 1.5;
        String kr = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String kt = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String index = "\"float index\" (\\d?\\.\\d+)";
        Matcher m;
        if((m = Pattern.compile(kr).matcher(line)).find()) {
            reflectivity = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(kt).matcher(line)).find()) {
            transmited = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(index).matcher(line)).find()) {
            refractionIndex = Double.valueOf(m.group(1));
        }
        return new Glass(refractionIndex);
    }

    private static Material parseMirror(String line, Map<String,Texture> textureMap){
        Texture reflectivity = new ConstantColorTexture(new Color(1,1,1));
        String kr = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String krtexture = "\"texture Kr\" \"([^\"]+)\"";
        Matcher m = Pattern.compile(kr).matcher(line);
        if(m.find()) {
            reflectivity = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(krtexture).matcher(line)).find()) {
            reflectivity = textureMap.get(m.group(1));
        }
        return new Mirror(reflectivity);
    }

    private static Material parseMetal2(String line, Map<String,Texture> textureMap){
        double roughness = 0.001;
        Texture reflectivity = new ConstantColorTexture(new Color(1,1,1));
        Matcher m;
        final String roughnessregexp = "\"float roughness\" (\\d?\\.\\d+)";
        final String kr = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        String krtexture = "\"texture Kr\" \"([^\"]+)\"";
        if((m = Pattern.compile(roughnessregexp).matcher(line)).find()) {
            roughness = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(kr).matcher(line)).find()) {
            reflectivity = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(krtexture).matcher(line)).find()) {
            reflectivity = textureMap.get(m.group(1));
        }
        return new Metal2(reflectivity, roughness);
    }
}
