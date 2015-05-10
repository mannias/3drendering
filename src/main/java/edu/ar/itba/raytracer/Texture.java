package edu.ar.itba.raytracer;

import java.io.File;

public class Texture {

    private String name;
    private File file;

    public Texture(String name, File file){
        this.name = name;
        this.file = file;
    }
}
