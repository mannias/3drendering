package edu.ar.itba.raytracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.ar.itba.raytracer.light.AmbientLight;
import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.light.PositionLight;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.RayTracerParameters;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class Scene {

	private Collection<GeometricObject> gobjects = new ArrayList<>();
	private final Set<Camera> cameras = new HashSet<>();
	private final Set<Light> lights = new HashSet<>();

	private Color ambientLight = new Color(0,0,0);

	private KdTree tree;

	public Scene() {
	}

	public void setTree(KdTree tree) {
		this.tree = tree;
	}

	public Camera addCamera(final int width, final int height,
			final double fov, final Vector4 position, final Vector4 lookAt,
			final Vector4 up, final Matrix44 transform, RayTracerParameters parameters) {
		final Camera camera = new Camera(this, width, height, fov, position,
				lookAt, up, transform, parameters);
		cameras.add(camera);
		return camera;
	}

	public void addLight(final Light light) {
		if (light instanceof AmbientLight) {
			ambientLight = ambientLight.add(light.color);
		} else {
			lights.add(light);
		}
	}

	public Collection<GeometricObject> getGObjects() {
		return gobjects;
	}

	public Set<Camera> getCameras() {
		return cameras;
	}

	public Set<Light> getLights() {
		return lights;
	}

	public boolean isIlluminati(final Vector4 point, Light light,
			CustomStack stack) {
		if (light instanceof DirectionalLight) {
			return true;
		}
		final PositionLight pointLight = (PositionLight) light;
        Vector4 position = pointLight.getPosition();
		Vector4 aux = new Vector4(position);
		aux.sub(point);
		Ray ray = new Ray(point, aux);
        GeometricObject obj = tree.intersectionExists(point.distanceTo(position), ray, stack, 0);
        if(obj == null || obj.material.light != null){
            return true;
        }
		return false;
	}

	public Color getAmbientLight() {
		return ambientLight;
	}

	public KdTree getTree() {
		return tree;
	}

	public void add(final Instance obj) {
		gobjects.add(obj);
	}

}
