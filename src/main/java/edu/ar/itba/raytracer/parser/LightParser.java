package edu.ar.itba.raytracer.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.light.PointLight;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;

public class LightParser {

    public static Light parseLight(String line){
        Light light = null;
        if(line.contains("point")){
            light = parsePointLight(line);
        }else if(line.contains("distant")){

        }else if(line.contains("infinite")){

        }
        return light;
    }

    private static Light parsePointLight(String line){
        final String colorrx = "\"color l\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        final String locationrx = "\"point from\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
        Matcher m;
        Vector3 locationPoint = new Vector3(0,0,0);
        Color lightColor = new Color(1,1,1);
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(locationrx).matcher(line)).find()) {
            locationPoint = new Vector3(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        Transform transform = new Transform();
        transform.setPosition(locationPoint);
        return new PointLight(locationPoint, Matrix44.ID, lightColor);
    }
}
