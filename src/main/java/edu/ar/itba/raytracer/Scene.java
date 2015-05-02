package edu.ar.itba.raytracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.BoundedPlane;
import edu.ar.itba.raytracer.shape.Box;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.shape.Plane;
import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.shape.Triangle;
import edu.ar.itba.raytracer.vector.Vector4;

public class Scene {

	private final Set<SceneShape> objects = new HashSet<>();
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

	public Camera addCamera(final int width, final int height, final double fov) {
		final Camera camera = new Camera(this, width, height, fov);
		cameras.add(camera);
		return camera;
	}

	public Camera addCamera(final int width, final int height,
			final double fov, final Transform transform) {
		final Camera camera = new Camera(this, width, height, fov, transform);
		cameras.add(camera);
		return camera;
	}

	public Plane addPlane() {
		final Plane plane = new Plane();
		objects.add(plane);
		return plane;
	}

	public Plane addPlane(final Transform transform,
			final ShapeProperties properties) {
		final Plane plane = new Plane(transform, properties);
		objects.add(plane);
		return plane;
	}

	public Plane addPlane(final ShapeProperties properties) {
		final Plane plane = new Plane(properties);
		objects.add(plane);
		return plane;
	}

	public Plane addBoundedPlane(final Transform transform,
			final ShapeProperties properties) {
		final BoundedPlane plane = new BoundedPlane(transform, properties);
		objects.add(plane);
		return plane;
	}

	public Sphere addSphere(final Vector4 center, final double radius) {
		final Sphere sphere = new Sphere(center, radius);
		objects.add(sphere);
		return sphere;
	}

	public Sphere addSphere(final Vector4 center, final double radius,
			final ShapeProperties properties) {
		final Sphere sphere = new Sphere(center, radius, properties);
		objects.add(sphere);
		return sphere;
	}

	public Box addBox(final Transform transform,
			final ShapeProperties properties) {
		final Box box = new Box(transform, properties);
		objects.add(box);
		return box;
	}

	public Triangle addTriangle(final Vector4 point0, final Vector4 point1,
			final Vector4 point2) {
		final Triangle triangle = new Triangle(point0, point1, point2);
		objects.add(triangle);
		return triangle;
	}

	public Light addLight(final Transform transform,
			final LightProperties properties) {
		final Light light = new Light(transform, properties);
		lights.add(light);
		return light;
	}

	private Collection<Instance> gobjects = new ArrayList<>();

	public Set<SceneShape> getObjects() {
		return objects;
	}
	
	public Collection<Instance> getGObjects() {
		return gobjects;
	}

	public Set<Camera> getCameras() {
		return cameras;
	}

	public Set<Light> getLights() {
		return lights;
	}

	// public RayCollisionInfo traceRay(final Vector33 from, final Vector33
	// through) {
	// final Ray ray = new Ray(from, through.sub(from));
	//
	// double minDistance = -1;
	// SceneShape intersected = null;
	// for (final SceneShape obj : getObjects()) {
	// final double distance = obj.intersect(ray);
	// if (distance != -1 && (distance < minDistance || minDistance == -1)) {
	// minDistance = distance;
	// intersected = obj;
	// }
	// }
	//
	// if (intersected == null) {
	// return RayCollisionInfo.noCollision(ray);
	// }
	//
	// return new RayCollisionInfo(intersected, ray, minDistance);
	// }

	public boolean isIlluminati(final Vector4 point, Light light,
			CustomStack stack) {

		// final RayCollisionInfo rci = traceRay(point, light.getTransform()
		// .getPosition());
		// return !rci.collisionDetected()
		// || rci.getDistance() > point.distanceTo(light.getTransform()
		// .getPosition());

		Vector4 aux = new Vector4(light.getTransform().getPosition());
		aux.sub(point);
		Ray ray = new Ray(point, aux);
		return !tree.intersectionExists(
				point.distanceTo(light.getTransform().getPosition()), ray,
				stack);
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
