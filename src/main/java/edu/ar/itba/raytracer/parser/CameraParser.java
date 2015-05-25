package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraParser {

    private static Integer fov = null;
    private static Integer width = null;
    private static Integer height = null;
    private static Vector4 up = null;
    private static Vector4 position = null;
    private static Vector4 target = null;

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

    public static void parseLookAt(String line){
        final String lookAt = "lookAt (\\d+)";
        Matcher m;
        if((m = Pattern.compile(lookAt).matcher(line)).find()) {
            position = new Vector3(Integer.valueOf(m.group(1)),Integer.valueOf(m.group(2)),Integer.valueOf(m.group(3)));
            target = new Vector3(Integer.valueOf(m.group(4)),Integer.valueOf(m.group(5)),Integer.valueOf(m.group(6)));
            up = new Vector3(Integer.valueOf(m.group(7)),Integer.valueOf(m.group(8)),Integer.valueOf(m.group(9)));
        }
    }

    public static Camera getCamera(Scene scene, final Matrix44 transform){
        Transform cameraTransform = new Transform();
        cameraTransform.setPosition(new Vector4(-1.5,0,0,1));
        cameraTransform.setRotation(new Vector4(0, 90, 0,0));
        // TODO: Enganchar con los parametros del main.
        return scene.addCamera(width, height, fov, position, target, up, transform,1,5);
    }
}
