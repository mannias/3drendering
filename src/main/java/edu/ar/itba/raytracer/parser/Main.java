package edu.ar.itba.raytracer.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by matiasdomingues on 5/24/15.
 */
public class Main {
<<<<<<< HEAD
    public static void main(String[] argv) throws IOException {
        FileInput input = new FileInput(new File("charizard/untitled.lxs"));
        input.parse();
=======
	public static void main(String[] argv) throws IOException {
		for (int i = 0; i < 5; i++) {
			FileInput input = new FileInput(new File("charizard\\untitled.lxs"));
			input.parse();
		}
>>>>>>> f73c8c778030bba078776d189d1d88109362da95

	}
}
