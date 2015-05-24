package edu.ar.itba.raytracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.light.PointLight;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class Scene {

	private Collection<GeometricObject> gobjects = new ArrayList<>();
	private final Set<Camera> cameras = new HashSet<>();
	private final Set<Light> lights = new HashSet<>();

	private final Color ambientLight;

	private KdTree tree;

	public Scene(final Color ambientLight) {
		this.ambientLight = ambientLight;
	}

	public void setTree(KdTree tree) {
		this.tree = tree;
	}

	public Scene() {
		this(Color.DEFAULT_COLOR);
	}

	public Camera addCamera(final int width, final int height,
			final double fov, final Vector4 position, final Vector4 lookAt,
			final Vector4 up, final Matrix44 transform) {
		final Camera camera = new Camera(this, width, height, fov, position,
				lookAt, up, transform);
		cameras.add(camera);
		return camera;
	}

	public void addLight(final Light light) {
		lights.add(light);
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
		final PointLight pointLight = (PointLight) light;
		Vector4 aux = new Vector4(pointLight.position);
		aux.sub(point);
		Ray ray = new Ray(point, aux);
		return !tree.intersectionExists(
				point.distanceTo(pointLight.position), ray,
				stack, 0);
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
