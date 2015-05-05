package edu.ar.itba.raytracer;

public interface Intersectable {

	double intersect(final Ray ray);
	
	boolean intersectionExists(final Ray ray);

}
