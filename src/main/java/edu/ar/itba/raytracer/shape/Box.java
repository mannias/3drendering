package edu.ar.itba.raytracer.shape;

import java.util.Arrays;
import java.util.Collection;

import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class Box extends Mesh {

	private static Collection<MeshTriangle> triangles(final double w,
			final double h, final double d) {
		return Arrays.asList(new MeshTriangle(new Vector4(-w / 2, -h / 2,
				-d / 2, 1), new Vector4(w / 2, -h / 2, -d / 2, 1), new Vector4(
				-w / 2, h / 2, -d / 2, 1), new Vector2(1, 1),
				new Vector2(0, 1), new Vector2(1, 0), Vector4.NK, Vector4.NK,
				Vector4.NK), new MeshTriangle(new Vector4(w / 2, -h / 2,
				-d / 2, 1), new Vector4(-w / 2, h / 2, -d / 2, 1), new Vector4(
				w / 2, h / 2, -d / 2, 1), new Vector2(0, 1), new Vector2(1, 0),
				new Vector2(0, 0), Vector4.NK, Vector4.NK, Vector4.NK),
				new MeshTriangle(new Vector4(w / 2, h / 2, -d / 2, 1),
						new Vector4(w / 2, h / 2, d / 2, 1), new Vector4(w / 2,
								-h / 2, d / 2, 1), new Vector2(1, 0),
						new Vector2(0, 0), new Vector2(0, 1), Vector4.I,
						Vector4.I, Vector4.I), new MeshTriangle(new Vector4(
						w / 2, -h / 2, -d / 2, 1), new Vector4(w / 2, h / 2,
						-d / 2, 1), new Vector4(w / 2, -h / 2, d / 2, 1),
						new Vector2(1, 1), new Vector2(1, 0),
						new Vector2(0, 1), Vector4.I, Vector4.I, Vector4.I),
				new MeshTriangle(new Vector4(-w / 2, h / 2, -d / 2, 1),
						new Vector4(-w / 2, -h / 2, -d / 2, 1), new Vector4(
								-w / 2, -h / 2, d / 2, 1), new Vector2(0, 0),
						new Vector2(0, 1), new Vector2(1, 1), Vector4.NI,
						Vector4.NI, Vector4.NI), new MeshTriangle(new Vector4(
						-w / 2, h / 2, -d / 2, 1), new Vector4(-w / 2, h / 2,
						d / 2, 1), new Vector4(-w / 2, -h / 2, d / 2, 1),
						new Vector2(0, 0), new Vector2(1, 0),
						new Vector2(1, 1), Vector4.NI, Vector4.NI, Vector4.NI),
				new MeshTriangle(new Vector4(-w / 2, -h / 2, d / 2, 1),
						new Vector4(w / 2, -h / 2, d / 2, 1), new Vector4(
								-w / 2, h / 2, d / 2, 1), new Vector2(0, 1),
						new Vector2(1, 1), new Vector2(0, 0), Vector4.K,
						Vector4.K, Vector4.K), new MeshTriangle(new Vector4(
						w / 2, -h / 2, d / 2, 1), new Vector4(-w / 2, h / 2,
						d / 2, 1), new Vector4(w / 2, h / 2, d / 2, 1),
						new Vector2(1, 1), new Vector2(0, 0),
						new Vector2(1, 0), Vector4.K, Vector4.K, Vector4.K),
				new MeshTriangle(new Vector4(-w / 2, -h / 2, -d / 2, 1),
						new Vector4(w / 2, -h / 2, -d / 2, 1), new Vector4(
								-w / 2, -h / 2, d / 2, 1), new Vector2(0, 1),
						new Vector2(1, 1), new Vector2(0, 0), Vector4.NJ,
						Vector4.NJ, Vector4.NJ), new MeshTriangle(new Vector4(
						w / 2, -h / 2, -d / 2, 1), new Vector4(w / 2, -h / 2,
						d / 2, 1), new Vector4(-w / 2, -h / 2, d / 2, 1),
						new Vector2(1, 1), new Vector2(1, 0),
						new Vector2(0, 0), Vector4.NJ, Vector4.NJ, Vector4.NJ),
				new MeshTriangle(new Vector4(-w / 2, h / 2, -d / 2, 1),
						new Vector4(w / 2, h / 2, -d / 2, 1), new Vector4(
								-w / 2, h / 2, d / 2, 1), new Vector2(0, 0),
						new Vector2(1, 0), new Vector2(0, 1), Vector4.J,
						Vector4.J, Vector4.J), new MeshTriangle(new Vector4(
						w / 2, h / 2, -d / 2, 1), new Vector4(w / 2, h / 2,
						d / 2, 1), new Vector4(-w / 2, h / 2, d / 2, 1),
						new Vector2(1, 0), new Vector2(1, 1),
						new Vector2(0, 1), Vector4.J, Vector4.J, Vector4.J));
	}

	public Box(final double w, final double h, final double d) {
		super(triangles(w, h, d));
	}
}
