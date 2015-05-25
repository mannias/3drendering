package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Matrix44;

import java.io.IOException;
import java.util.Stack;

public class TransformationParser {

    public static void parseTransformation(String line, final Stack<Matrix44> transforms) throws IOException {
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
        }
    }
}
