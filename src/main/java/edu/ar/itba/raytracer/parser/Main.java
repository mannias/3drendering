package edu.ar.itba.raytracer.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by matiasdomingues on 5/24/15.
 */
public class Main {
	public static void main(String[] argv) throws IOException {
		for (int i = 0; i < 5; i++) {
			FileInput input = new FileInput(new File("charizard\\untitled.lxs"));
			input.parse();
		}

	}
}
