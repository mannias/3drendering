package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Instance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformationParser {

    public static void parseTranslate(String line, Instance instance){
        Matcher m;
        String translate = "Translate (-?\\d?\\.\\d+) (-?\\d?\\.\\d+) (-?\\d?\\.\\d+)";
        if((m = Pattern.compile(translate).matcher(line)).find()){
            instance.translate(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
    }

    public static void parseScale(String line, Instance instance){
        Matcher m;
        String translate = "Scale (\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)";
        if((m = Pattern.compile(translate).matcher(line)).find()){
            instance.scale(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)), Double.valueOf(m.group(3)));
        }
    }

    public static void parseRotate(String line, Instance instance){
        Matcher m;
        String translate = "Rotate (-?\\d?\\.\\d+) (\\d) (\\d) (\\d)";
        if((m = Pattern.compile(translate).matcher(line)).find()){
            Double deg = Double.valueOf(m.group(1));
            if(Integer.valueOf(m.group(2)) == 1){
                instance.rotateX(deg);
            }
            if(Integer.valueOf(m.group(3)) == 1){
                instance.rotateY(deg);
            }
            if(Integer.valueOf(m.group(4)) == 1){
                instance.rotateZ(deg);
            }
        }
    }


}
