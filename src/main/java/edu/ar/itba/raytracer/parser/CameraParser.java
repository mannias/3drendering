package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Scene;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraParser {

    final static int height = 480;
    final static int width = 640;

    public static void Parse(String line, Scene scene){
        String pattern = "\"float fov\" \\[([^]]+)\\]";
        Pattern fov = Pattern.compile(pattern);
        Matcher m = fov.matcher(line);
        if(m.find()){
            scene.addCamera(width, height, Integer.valueOf(m.group(1)));
        }
    }
}
