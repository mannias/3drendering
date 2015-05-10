package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Intersectable;
import edu.ar.itba.raytracer.SceneElement;
import edu.ar.itba.raytracer.properties.ShapeProperties;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public abstract class SceneShape extends SceneElement implements Intersectable {

	private final ShapeProperties properties;

	public SceneShape(final Transform transform,
			final ShapeProperties properties) {
		super(transform);
		this.properties = properties;

	}

	public SceneShape(final ShapeProperties properties) {
		this(new Transform(), properties);
	}

	public ShapeProperties getProperties() {
		return properties;
	}

	public abstract Vector4 normal(final Vector4 point);
	
	public static class BB {
		public double minX, maxX, minY, maxY, minZ, maxZ;

		public BB(double minX, double maxX, double minY, double maxY,
				double minZ, double maxZ) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
			this.minZ = minZ;
			this.maxZ = maxZ;
		}
		
		public double getArea() {
			final double width = maxX - minX;
			final double height = maxY - minY;
			final double depth = maxZ - minZ;
			return width * width + height * height + depth * depth;
		}
	}

	
	public abstract BB getBB();
	
}
