package edu.ar.itba.raytracer.parser;

import java.io.File;
import java.io.FileNotFoundException;

public class test {

    public static void main(String[] args) throws FileNotFoundException {
        FileInput input = new FileInput(new File("testParser"));
        input.parse();
    }
}
