package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Camera;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraParser {

    private Integer fov = 60;
    private Integer width = 640;
    private Integer height = 480;
    private Vector4 up = new Vector4(0,0,1,0);
    private Vector4 position = new Vector4(-3, -3, -1, 1);
    private Vector4 target = new Vector4(0,0,0,0);

    public void parseFov(String line){
        String pattern = "\"float fov\" \\[([^]]+)\\]";
        Matcher m;
        if((m = Pattern.compile(pattern).matcher(line)).find()) {
            fov = Integer.valueOf(m.group(1));
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
        final String lookAt = "LookAt (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)";
        Matcher m;
        if((m = Pattern.compile(lookAt).matcher(line)).find()) {
            position = new Vector3(Integer.valueOf(m.group(1)),Integer.valueOf(m.group(2)),Integer.valueOf(m.group(3)));
            target = new Vector3(Integer.valueOf(m.group(4)),Integer.valueOf(m.group(5)),Integer.valueOf(m.group(6)));
            up = new Vector3(Integer.valueOf(m.group(7)),Integer.valueOf(m.group(8)),Integer.valueOf(m.group(9)));
        }
    }

    public Camera setCamera(Scene scene){
        return scene.addCamera(width, height, fov, position, target, up);
    }
}
