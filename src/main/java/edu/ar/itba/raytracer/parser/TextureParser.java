package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.texture.ImageTexture;
import edu.ar.itba.raytracer.texture.Texture;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureParser {

    public static void parseTexture(String line, Map<String,Texture> textureMap, Path parent) throws IOException {
        String name = null;
        String filename = null;
        String namerx = "Texture \"([^\"]+)\"";
        String filenamerx = "\"string filename\" \\[\"([^\"]+)\"\\]";
        Matcher m;
        if((m = Pattern.compile(namerx).matcher(line)).find()) {
            name = m.group(1);
        }
        if((m = Pattern.compile(filenamerx).matcher(line)).find()){
            filename = m.group(1);
        }
        if(filename != null && name != null){
			textureMap.put(name,new ImageTexture(ImageIO.read(parent.resolveSibling(filename).toFile())));
        }
    }
}
