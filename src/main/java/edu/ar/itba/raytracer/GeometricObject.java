package edu.ar.itba.raytracer;

public abstract class GeometricObject {
	
	public Material material;
	
	public abstract RayCollisionInfo hit(final Ray ray);
	

}
