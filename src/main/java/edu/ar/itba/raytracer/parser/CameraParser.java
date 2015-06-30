package edu.ar.itba.raytracer.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.properties.RayTracerParameters;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class CameraParser {

    private double fov = 60;
    private int width = 640;
    private int height = 480;
    private Vector4 up = new Vector4(0,0,1,0);
    private Vector4 position = new Vector4(1, 1, 1, 1);
    private Vector4 target = new Vector4(0,0,0,1);

    public void parseFov(String line){
        String pattern = "\"float fov\" \\[([^]]+)\\]";
        Matcher m;
        if((m = Pattern.compile(pattern).matcher(line)).find()) {
            fov = Double.valueOf(m.group(1));
        }
    }

    public void parseDimension(String line){
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

    public void parseLookAt(String line){
        final String lookAt = "LookAt (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) " +
                "(-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+)";
        Matcher m;
        if((m = Pattern.compile(lookAt).matcher(line)).find()) {
            position = new Vector4(Double.valueOf(m.group(1)),Double.valueOf(m.group(2)),Double.valueOf(m.group(3)),1);
            target = new Vector4(Double.valueOf(m.group(4)),Double.valueOf(m.group(5)),Double.valueOf(m.group(6)),1);
            up = new Vector3(Double.valueOf(m.group(7)),Double.valueOf(m.group(8)),Double.valueOf(m.group(9)));
        }
    }


    public Camera setCamera(Scene scene, final Matrix44 transform, RayTracerParameters parameters){
        return scene.addCamera(width, height, fov, position, target, up, transform, parameters);
    }
}
