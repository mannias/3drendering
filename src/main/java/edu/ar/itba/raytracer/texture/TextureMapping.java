package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.vector.Vector4;

public interface TextureMapping {
	
	int[] getPixelCoordinates(final Vector4 localHitPoint, final int hres, final int vres);

}
