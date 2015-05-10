package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Scene;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraParser {

    private static Integer fov = null;
    private static Integer width = null;
    private static Integer height = null;

    public static void parseFov(String line){
        String pattern = "\"float fov\" \\[([^]]+)\\]";
        Matcher m;
        if((m = Pattern.compile(pattern).matcher(line)).find()) {
            fov = Integer.valueOf(m.group(1));
        }
    }

    public static void parseDimension(String line){
        Matcher m;
        String xpattern = "\"integer xresolution\" \\[([^]]+)\\]";
        if((m = Pattern.compile(xpattern).matcher(line)).find()) {
            width = Integer.valueOf(m.group(1));
        }
        String ypattern = "\"integer yresolution\" \\[([^]]+)\\]";
        if((m = Pattern.compile(ypattern).matcher(line)).find()) {
            height = Integer.valueOf(m.group(1));
        }
    }

    public static Camera getCamera(Scene scene){
        return new Camera(scene,width,height,fov);
    }

    public static void parseLookAt(String line){

    }
}
