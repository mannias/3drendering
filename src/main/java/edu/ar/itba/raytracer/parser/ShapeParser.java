package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.shape.*;
import edu.ar.itba.raytracer.texture.SphericalTextureMapping;
import edu.ar.itba.raytracer.texture.TextureMapping;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.shape.MeshTriangle;
import edu.ar.itba.raytracer.shape.Plane;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.vector.Matrix44;

public class ShapeParser {

    public static Instance Parse(String line, Material material, final Matrix44 transform){
        Instance instance = null;
        if(line.contains("trianglemesh")){
            instance = parseTriangleMesh(line);
        }else if(line.contains("mesh")) {
            instance = parseMesh(line);
        }else if(line.contains("box")){
            instance = parseBox(line);
        }else if(line.contains("plane")){
            instance = parsePlane(line);
        }else if(line.contains("sphere")){
            instance = parseSphere(line);
            setSphericMapping(material);
        }
        instance.material = material;
        instance.transform(transform);
        return instance;
    }

    private static void setSphericMapping(Material material){
        TextureMapping mapping = new SphericalTextureMapping();
        material.ka.setTextureMapping(mapping);
        material.kd.setTextureMapping(mapping);
        material.ks.setTextureMapping(mapping);
        material.transparency.setTextureMapping(mapping);
    }

    private static Instance parseMesh(String line){
        List<Vector4> normals = null, vertex = null;
        List<Vector2> uv = null;
        Instance instance = null;
        final String normalsrx = "\"normal N\" \\[([^]]+)\\]";
        final String vertexrx = "\"point P\" \\[([^]]+)\\]";
        final String triindicesrx = "\"integer triindices\" \\[([^]]+)\\]";
        final String uvrx = "\"float uv\" \\[([^]]+)\\]";
        Matcher m;
        long time = System.currentTimeMillis();
        if((m = Pattern.compile(vertexrx).matcher(line)).find()){
            System.out.println("Match vertex: " + (System.currentTimeMillis() - time));
            long time2 = System.currentTimeMillis();
            vertex = parseVectors(m.group(1));
            System.out.println("Parse Vectors vertex: " + (System.currentTimeMillis() - time2));
        }
        if((m = Pattern.compile(normalsrx).matcher(line)).find()){
            normals = parseVectors(m.group(1));
        }
        if((m = Pattern.compile(uvrx).matcher(line)).find()){
            uv = parseUv(m.group(1));
        }
        if((m = Pattern.compile(triindicesrx).matcher(line)).find()){
            instance = new Instance(new Mesh(calculateTriangles(m.group(1), normals,vertex, uv)));
        }
        System.out.println("Total Parse: " + (System.currentTimeMillis() - time));
        return instance;
    }

    private static Instance parseTriangleMesh(String line) {
        List<Vector4> normals = null, vertex = null;
        List<Vector2> uv = null;
        Instance instance = null;
        final String vertexrx = "\"point P\" \\[([^]]+)\\]";
        final String indicesrx = "\"integer indices\" \\[([^]]+)\\]";
        Matcher m;
        long time = System.currentTimeMillis();
        if((m = Pattern.compile(vertexrx).matcher(line)).find()){
            vertex = parseVectors(m.group(1));
        }
        if((m = Pattern.compile(indicesrx).matcher(line)).find()){
            instance = new Instance(new Mesh(calculateTriangles(m.group(1), normals,vertex, uv)));
        }
        System.out.println("Total Parse: " + (System.currentTimeMillis() - time));
        return instance;
    }

    private static List<Vector4> parseVectors(String line){
        List<Vector4> list = new ArrayList<>();
        long time = System.currentTimeMillis();
        String[] elems = line.split("[\\s]+");
        System.out.println("Split: " + (System.currentTimeMillis() - time));
        for(int i = 0; i+2 < elems.length; i+=3){
            if(i == 0 && elems[i].isEmpty()){
                i++;
            }
            list.add(new Vector3(Double.valueOf(elems[i]), Double.valueOf(elems[i + 1]), Double.valueOf(elems[i + 2])));


        }
        return list;
    }

    private static List<Vector4> parseIndex(String line){

        return null;
    }

    private static List<Vector2> parseUv(String line){
        List<Vector2> list = new ArrayList<>();
        String[] elems = line.split("\\s");
        for(int i = 0; i < elems.length /2 ; i++){
            list.add(new Vector2(Double.valueOf(elems[2*i]), 1- Double.valueOf(elems[2*i + 1])));
        }
        return list;
    }




    private static List<MeshTriangle> calculateTriangles(String line, List<Vector4> normals,
                                                         List<Vector4> vertex, List<Vector2> uv){
        List<MeshTriangle> list = new ArrayList<>();
        String[] elems = line.split("[\\s]+");
        for(int i = 0; i+2 < elems.length; i+=3){
            if(i == 0 && elems[i].isEmpty()){
                i++;
            }
            int loc1 = Integer.valueOf(elems[i]);
            int loc2 = Integer.valueOf(elems[i+1]);
            int loc3 = Integer.valueOf(elems[i+2]);
            if(normals == null){
                list.add(new MeshTriangle(vertex.get(loc1),vertex.get(loc2),vertex.get(loc3)));
            }else if (uv == null){
                list.add(new MeshTriangle(vertex.get(loc1),vertex.get(loc2),vertex.get(loc3),
                        normals.get(loc1),normals.get(loc2),normals.get(loc3)));
            }else {
                list.add(new MeshTriangle(vertex.get(loc1), vertex.get(loc2), vertex.get(loc3),
                        uv.get(loc1), uv.get(loc2), uv.get(loc3),
                        normals.get(loc1), normals.get(loc2), normals.get(loc3)));
            }
        }
        return list;
    }

    private static Instance parsePlane(String line){
        Vector4 n = new Vector4(1,1,1,0);
        String normal = "\"normal N\" \\[(-?\\d+\\.\\d+) (-?\\d+\\.\\d+) (-?\\d+\\.\\d+)\\]";
        Matcher m;
        if((m = Pattern.compile(normal).matcher(line)).find()){
            n = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),Double.valueOf(m.group(3)),0);
        }
        return new Instance(new Plane(n));
    }

    private static Instance parseBox(String line){
        double width = 0d, height = 0d, depth = 0d;
        String name;
        String widths = "\"float width\" \\[(\\d+\\.\\d+)\\]";
        String heights = "\"float height\" \\[(\\d+\\.\\d+)\\]";
        String depths = "\"float depth\" \\[(\\d+\\.\\d+)\\]";
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
        return new Instance(new Box(width,height,depth));
    }

    private static Instance parseSphere(String line){
        double radius = 1.0;
        String radiusPattern = "\"float radius\" \\[(\\d+\\.\\d+)\\]";
        Matcher m;
        if((m = Pattern.compile(radiusPattern).matcher(line)).find()){
            radius = Double.valueOf(m.group(1));
        }
        Instance instance = new Instance(new Sphere(radius));
        return instance;
    }
}
