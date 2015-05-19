package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.vector.Vector4;

/**
 * Created by matiasdomingues on 5/15/15.
 */
public class PlaneTextureMapping implements TextureMapping{
    @Override
    public int[] getPixelCoordinates(Vector4 localHitPoint, int hres, int vres) {
        int[] loc = new int[2];
        loc[0] = (int)localHitPoint.x % vres;
        loc[1] = (int)localHitPoint.y % hres;
        return loc;
    }
}
