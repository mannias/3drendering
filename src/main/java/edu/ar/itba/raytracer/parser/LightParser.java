package edu.ar.itba.raytracer.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.light.*;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class LightParser {

    final static String colorrx = "\"color L\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
    final static String from = "\"point from\" \\[(-?\\d?\\.\\d+) (-?\\d?\\.\\d+) (-?\\d?\\.\\d+)\\]";
    final static String to = "\"point to\" \\[(-?\\d?\\.\\d+) (-?\\d?\\.\\d+) (-?\\d?\\.\\d+)\\]";
    final static String coneanglerx = "\"float coneangle\" (-?\\d?\\.\\d+)";
    final static String conedeltaanglerx = "\"float conedeltaangle\" (-?\\d?\\.\\d+)";

    public static Light parseLight(final String line, final Matrix44 transform){
        Light light = null;
        if(line.contains("distant")){
            light = parseDistantLight(line, transform);
        }else if(line.contains("infinite")) {
            light = parseInfiniteLight(line);
        }else if(line.contains("spot")) {
            light = parseSpotLight(line, transform);
        }else if(line.contains("area")){
            light = parseAreaLight(line,transform);
        }else if(line.contains("point")){
            //beware, all the elements contain the world point, so it' kind of a default now
            light = parsePointLight(line, transform);
        }
        return light;
    }

    private static Light parseAreaLight(String line, Matrix44 transform){
        Matcher m;
        Color lightColor = new Color(1,1,1);
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        return new AreaLight(lightColor);
    }

    private static Light parseSpotLight(String line, Matrix44 transform){
        Matcher m;
        Color lightColor = new Color(1,1,1);
        Vector4 fromPoint = new Vector4(0,0,0,1), toPoint = new Vector4(1,1,1,1);
        double coneangle = 30d, conedeltaangle = 5d;
        if((m = Pattern.compile(colorrx).matcher(line)).find()) {
            lightColor = new Color(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
        if((m = Pattern.compile(from).matcher(line)).find()) {
            fromPoint = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)), 1);
        }
        if((m = Pattern.compile(to).matcher(line)).find()) {
            toPoint = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)), 1);
        }
        if((m = Pattern.compile(coneanglerx).matcher(line)).find()) {
            coneangle = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(conedeltaanglerx).matcher(line)).find()) {
            conedeltaangle = Double.valueOf(m.group(1));
        }
        return new SpotLight(lightColor, fromPoint, toPoint, coneangle, conedeltaangle, transform);

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
