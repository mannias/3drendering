package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;

public class CheckedTexture implements Texture{

    private final Color color1;
    private final Color color2;
    private final int width;
    private final TextureMapping mapping;

    public CheckedTexture(final Color color1, final Color color2, final int width, final TextureMapping mapping){
        this.color1 = color1;
        this.color2 = color2;
        this.width = width;
        this.mapping = mapping;
    }

    @Override
    public Color getColor(RayCollisionInfo collisionInfo) {
        int[] loc = mapping.getPixelCoordinates(collisionInfo.getLocalCollisionPoint(),2 * width,2 * width);
        if((loc[0] < width && loc[1] < width) || loc[0] > width && loc[1] > width){
            return color1;
        }else{
            return color2;
        }
    }
}
