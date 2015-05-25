package edu.ar.itba.raytracer.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.light.AmbientLight;

import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.light.PointLight;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class LightParser {

    final static String colorrx = "\"color L\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
    final static String from = "\"point from\" \\[(-?\\d?\\.\\d+) (-?\\d?\\.\\d+) (-?\\d?\\.\\d+)\\]";
    final static String to = "\"point to\" \\[(-?\\d?\\.\\d+) (-?\\d?\\.\\d+) (-?\\d?\\.\\d+)\\]";

    public static Light parseLight(final String line, final Matrix44 transform){
        Light light = null;
        if(line.contains("distant")){
            light = parseDistantLight(line, transform);
        }else if(line.contains("infinite")){
            light = parseInfiniteLight(line);
        }else if(line.contains("point")){
            //beware, the element itself contains the world point
            light = parsePointLight(line, transform);
        }
        return light;
    }

    private static Light parseInfiniteLight(String line) {
        Matcher m;
        Color lightColor = new Color(1,1,1);
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        
        return new AmbientLight(lightColor);
	}

	private static Light parseDistantLight(String line, Matrix44 transform) {
        Matcher m;
        Color lightColor = new Color(1,1,1);
        Vector4 fromPoint = new Vector4(0,0,0,1), toPoint = new Vector4(1,1,1,1);
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(from).matcher(line)).find()) {
            fromPoint = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)), 1);
        }
        if((m = Pattern.compile(to).matcher(line)).find()) {
            toPoint = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)), 1);
        }
        
        return new DirectionalLight(fromPoint, toPoint, transform, lightColor);
	}

	private static Light parsePointLight(final String line, final Matrix44 transform){
        Matcher m;
        Vector4 locationPoint = new Vector4(0,0,0,1);
        Color lightColor = new Color(1,1,1);
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(from).matcher(line)).find()) {
            locationPoint = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),
                    Double.valueOf(m.group(3)),1);
        }
        return new PointLight(locationPoint, transform, lightColor);
    }
}
