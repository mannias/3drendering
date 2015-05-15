package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.*;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeParser {

    private static Transform transform = new Transform();
    private static ShapeProperties properties = null;

    public static SceneShape Parse(String line, Material material){
        properties = new ShapeProperties(material);
        if(line.contains("Rotate")) {
            setRotate(line);
        }else if(line.contains("box")){
            parseBox(line);
        }else if(line.contains("plane")){
            parsePlane(line);
        }else if(line.contains("sphere")){
            parseSphere(line);
        }else if(line.contains("mesh")){
            parseMesh(line);
        }

        return null;
    }

    private static void setRotate(String line){
        Vector4 n = null;
        String normal = "Rotate (\\d+) (\\d+) (\\d+)";
        Matcher m;
        if((m = Pattern.compile(normal).matcher(line)).find()){
            transform.setRotation(new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),Double.valueOf(m.group(3)),0));
        }
    }

    private static Plane parsePlane(String line){
        Vector4 n = null;
        String name;
        String normal = "\"normal N\" \\[(\\d+\\.\\d+) (\\d+\\.\\d+) (\\d+\\.\\d+)\\]";
        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
        Matcher m;
        if((m = Pattern.compile(normal).matcher(line)).find()){
            n = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),Double.valueOf(m.group(3)),0);
        }
        if((m = Pattern.compile(namePattern).matcher(line)).find()){
            name = m.group(1);
        }
        return new Plane(transform,properties,n);
    }

    private static Box parseBox(String line){
        double width = 0d, height = 0d, depth = 0d;
        String name;
        String widths = "\"float width\" \\[(\\d+\\.\\d+)\\]";
        String heights = "\"float height\" \\[(\\d+\\.\\d+)\\]";
        String depths = "\"float depth\" \\[(\\d+\\.\\d+)\\]";
        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
        Matcher m;
        if((m = Pattern.compile(widths).matcher(line)).find()){
            width = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(heights).matcher(line)).find()){
            height = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(depths).matcher(line)).find()){
            depth = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(namePattern).matcher(line)).find()){
            name = m.group(1);
        }
        //TODO: add real parameters
        return new Box(transform,properties);
    }

    private static Sphere2 parseSphere(String line){
        double radius = 1.0;
        String name;
        String radiusPattern = "\"float radius\" \\[(\\d+\\.\\d+)\\]";
        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
        Matcher m;
        if((m = Pattern.compile(radiusPattern).matcher(line)).find()){
            radius = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(namePattern).matcher(line)).find()){
            name = m.group(1);
        }
        //TODO: check center
        return new Sphere2(radius);
    }

    private static Mesh parseMesh(String line){
        //TODO: I have no idea how this works
        return null;
    }
}
