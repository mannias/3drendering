package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;

public interface Texture {

	Color getColor(final RayCollisionInfo collisionInfo);
	
}
