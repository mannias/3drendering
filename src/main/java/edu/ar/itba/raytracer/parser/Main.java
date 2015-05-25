package edu.ar.itba.raytracer.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by matiasdomingues on 5/24/15.
 */
public class Main {
    public static void main(String[] argv) throws IOException {
        FileInput input = new FileInput(new File("pikachu/untitled.lxs"));
        input.parse();

    }
}
