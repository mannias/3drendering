package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Matrix44;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformationParser {

    public static void parseTransformation(String line, final Stack<Matrix44> transforms) throws IOException {
        Matcher m;
        final String longTransform = "Transform \\[(-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+)" +
                " (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+)" +
                " (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+) (-?\\d+.\\d+)\\]";
        String[] elements = line.split("//s");
        if (elements[0].equals("Identity")) {
            transforms.pop();
            transforms.push(Matrix44.ID);
        } else if (elements[0].equals("Scale")) {
            transforms.push(GeometricObject.scaleMatrix(
                    Double.parseDouble(elements[1]),
                    Double.parseDouble(elements[2]),
                    Double.parseDouble(elements[3])).multiply(
                    transforms.pop()));
        } else if (elements[0].equals("Translate")) {
            transforms.push(GeometricObject.translationMatrix(
                    Double.parseDouble(elements[1]),
                    Double.parseDouble(elements[2]),
                    Double.parseDouble(elements[3])).multiply(
                    transforms.pop()));
        } else if (elements[0].equals("Rotate")) {
            if (elements[2].equals("1")) {
                transforms.push(GeometricObject.rotationXMatrix(Double
                        .parseDouble(elements[1])));
            } else if (elements[3].equals("1")) {
                transforms.push(GeometricObject.rotationYMatrix(Double
                        .parseDouble(elements[1])));
            } else if (elements[4].equals("1")) {
                transforms.push(GeometricObject.rotationZMatrix(Double
                        .parseDouble(elements[1])));
            }
        }else if((m = Pattern.compile(longTransform).matcher(line)).find()){
            Matrix44 mat = new Matrix44(Double.valueOf(m.group((1))), Double.valueOf(m.group((2))),
                    Double.valueOf(m.group((3))), Double.valueOf(m.group((4))), Double.valueOf(m.group((5))),
                    Double.valueOf(m.group((6))), Double.valueOf(m.group((7))), Double.valueOf(m.group((8))),
                    Double.valueOf(m.group((9))), Double.valueOf(m.group((10))), Double.valueOf(m.group((11))),
                    Double.valueOf(m.group((12))), Double.valueOf(m.group((13))), Double.valueOf(m.group((14))),
                    Double.valueOf(m.group((15))), Double.valueOf(m.group((16))));
            transforms.push(mat.multiply(transforms.pop()));
        }
    }
}
