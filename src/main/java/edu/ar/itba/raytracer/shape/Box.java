package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector3;

public class Box extends SceneShape {

	private static BoundedPlane calculateFace(final Vector3 cubeCenter,
			final Vector3 cubeRotation, final Vector3 cubeScale,
			final ShapeProperties cubeProperties,
			final Vector3 originalFaceCenter,
			final Vector3 originalFaceRotation, final Vector3 faceRotation) {
		Transform faceTransform = new Transform();
		faceTransform.setScale(cubeScale);
		faceTransform.setRotation(originalFaceRotation.add(faceRotation));
		final Vector3 faceCenter = originalFaceCenter.sub(cubeCenter)
				.rotate(cubeRotation).add(cubeCenter);
		faceTransform.setPosition(faceCenter);
		return new BoundedPlane(faceTransform, cubeProperties);
	}

	// Faces are numbered like in a dice.
	private final BoundedPlane face1, face2, face3, face4, face5, face6;

	public Box(final Transform transform, final ShapeProperties properties) {
		super(transform, properties);
		final Vector3 scale = transform.getScale();
		final Vector3 center = transform.getPosition();
		final Vector3 rotation = transform.getRotation();
		face1 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x, center.y, center.z
				- scale.z), new Vector3(-90, 0, 0), new Vector3(rotation.x,
				rotation.y, rotation.z));
		face2 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x, center.y + scale.y,
				center.z), new Vector3(0, 0, 0), new Vector3(rotation.x,
				rotation.y, rotation.z));
		face3 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x - scale.x, center.y,
				center.z), new Vector3(0, 0, 90), new Vector3(rotation.y,
				rotation.x, rotation.z));
		face4 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x + scale.x, center.y,
				center.z), new Vector3(90, 90, 0), new Vector3(-rotation.y,
				-rotation.x, rotation.z));
		face5 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x, center.y - scale.y,
				center.z), new Vector3(0, 0, 180), new Vector3(-rotation.x,
				-rotation.y, rotation.z));
		face6 = calculateFace(center, rotation, scale, new ShapeProperties(
				new Color(1, 1, 1)), new Vector3(center.x, center.y, center.z
				+ scale.z), new Vector3(90, 0, 0), new Vector3(rotation.x,
				rotation.y, rotation.z));
	}

	@Override
	public double intersect(final Ray ray) {
		final double i1 = face1.intersect(ray);
		final double i2 = face2.intersect(ray);
		final double i3 = face3.intersect(ray);
		final double i4 = face4.intersect(ray);
		final double i5 = face5.intersect(ray);
		final double i6 = face6.intersect(ray);

		double dist = -1;

		if (i1 != -1) {
			dist = i1;
		}
		if (i2 != -1) {
			if (dist == -1) {
				dist = i2;
			} else {
				dist = Math.min(dist, i2);
			}
		}
		if (i3 != -1) {
			if (dist == -1) {
				dist = i3;
			} else {
				dist = Math.min(dist, i3);
			}
		}
		if (i4 != -1) {
			if (dist == -1) {
				dist = i4;
			} else {
				dist = Math.min(dist, i4);
			}
		}
		if (i5 != -1) {
			if (dist == -1) {
				dist = i5;
			} else {
				dist = Math.min(dist, i5);
			}
		}
		if (i6 != -1) {
			if (dist == -1) {
				dist = i6;
			} else {
				dist = Math.min(dist, i6);
			}
		}

		return dist;
	}

	@Override
	public Vector3 normal(final Vector3 point) {
		if (face1.containsPoint(point)) {
			return face1.normal(point);
		}
		if (face2.containsPoint(point)) {
			return face2.normal(point);
		}
		if (face3.containsPoint(point)) {
			return face3.normal(point);
		}
		if (face4.containsPoint(point)) {
			return face4.normal(point);
		}
		if (face5.containsPoint(point)) {
			return face5.normal(point);
		}
		if (face6.containsPoint(point)) {
			return face6.normal(point);
		}
		throw new AssertionError(
				"You should only call this method for points on the cube's surface. The point "
						+ point + " is not on it.");
	}

}
