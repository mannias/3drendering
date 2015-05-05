package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.Plane;
import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.vector.Vector3;

import java.util.DoubleSummaryStatistics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeParser {

    private static Transform transform;
    private static ShapeProperties properties;

    public static SceneShape Parse(String line){
        if(line.contains("box")){

        }else if(line.contains("plane")){
            parsePlane(line);
        }
        return null;
    }

    private static Plane parsePlane(String line){
        Vector3 n = null;
        String name;
        String normal = "\"normal N\" \\[(\\d+\\.\\d+) (\\d+\\.\\d+) (\\d+\\.\\d+)\\]";
        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
        Pattern pattern = Pattern.compile(normal);
        Matcher m = pattern.matcher(line);
        if(m.find()){
            n = new Vector3(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),Double.valueOf(m.group(3)));
        }
        pattern = Pattern.compile(namePattern);
        m = pattern.matcher(line);
        if(m.find()){
            name = m.group(1);
        }
        return new Plane(transform,properties,n);
    }
}
