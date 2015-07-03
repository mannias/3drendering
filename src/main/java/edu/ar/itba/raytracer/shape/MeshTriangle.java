package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;

public class MeshTriangle extends GeometricObject {

	private final static double EPSILON = 0.00001;

	private final Vector4 vertex0;
	private final Vector4 vertex1;
	private final Vector4 vertex2;

	private final Vector4 n0;
	private final Vector4 n1;
	private final Vector4 n2;

	private final Vector2 uv0;
	private final Vector2 uv1;
	private final Vector2 uv2;

	private final Vector4 e1;
	private final Vector4 e2;
	
	private final Vector4 normal;

    public MeshTriangle(final Vector4 vertex0, final Vector4 vertex1,
                        final Vector4 vertex2){
        this.vertex0 = vertex0;
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        e1 = new Vector4(vertex1);
        e1.sub(vertex0);
        e2 = new Vector4(vertex2);
        e2.sub(vertex0);
        this.n0 = e1.cross(e2);
        n0.normalize();
        this.n1 = n0;
        this.n2 = n0;
        this.uv0 = null;
        this.uv1 = null;
        this.uv2 = null;
        
        final Vector4 n = e1.cross(e2);
		if (n.dot(n0) > 0) {
			this.normal = n;
		} else {
			this.normal = n.neg();
		}
		this.normal.normalize();
    }

	public MeshTriangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2, final Vector4 n0, final Vector4 n1,
			final Vector4 n2) {
		this(vertex0, vertex1, vertex2, null, null, null, n0, n1, n2);
	}

	public MeshTriangle(final Vector4 vertex0, final Vector4 vertex1,
			final Vector4 vertex2, final Vector2 uv0, final Vector2 uv1,
			final Vector2 uv2, final Vector4 n0, final Vector4 n1,
			final Vector4 n2) {
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;

		normal = new Vector4(n0);//.add(n1).add(n2).scalarMult(1/3).normalize();
		
		this.n0 = n0;
		this.n0.normalize();
		this.n1 = n1;
		this.n1.normalize();
		this.n2 = n2;
		this.n2.normalize();

		this.uv0 = uv0;
		this.uv1 = uv1;
		this.uv2 = uv2;

		e1 = new Vector4(vertex1);
		e1.sub(vertex0);
		e2 = new Vector4(vertex2);
		e2.sub(vertex0);
		
	}

	@Override
	public RayCollisionInfo hit(Ray ray, CustomStack stack, int top) {
		final Vector4 d = ray.getDir();
		final Vector4 p = d.cross(e2);
		final double div = p.dot(e1);
		if (div > -EPSILON && div < EPSILON) {
			return null;
		}

		final double invDiv = 1 / div;

		final Vector4 t = new Vector4(ray.getSource());
		t.sub(vertex0);

		final double u = invDiv * t.dot(p);

		if (u < 0|| u > 1) {
			return null;
		}

		final Vector4 q = t.cross(e1);

		final double v = invDiv * d.dot(q);
		if (v < 0 || u + v > 1) {
			return null;
		}

		final double dist = invDiv * e2.dot(q);
		if (dist < EPSILON) {
			return null;
		}

		final RayCollisionInfo rci = new RayCollisionInfo(this, ray, dist);

		if (uv0 != null) {
			final double interpolatedU = uv0.x * (1 - u - v) + uv1.x * u
					+ uv2.x * v;
			final double interpolatedV = uv0.y * (1 - u - v) + uv1.y * u
					+ uv2.y * v;
			rci.u = interpolatedU;
			rci.v = interpolatedV;
		}


		final Vector4 normal = new Vector4(n0);
		normal.scalarMult(1 - u - v);

		final Vector4 normal1 = new Vector4(n1);
		normal1.scalarMult(u);

		final Vector4 normal2 = new Vector4(n2);
		normal2.scalarMult(v);

		normal.add(normal1);
		normal.add(normal2);

		rci.normal = normal;

		return rci;
	}

	@Override
	public AABB getAABB() {
		final Vector4[] vertexes = new Vector4[] { vertex0, vertex1, vertex2 };
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (final Vector4 vertex : vertexes) {
			if (vertex.x < minX) {
				minX = vertex.x;
			}
			if (vertex.x > maxX) {
				maxX = vertex.x;
			}
			if (vertex.y < minY) {
				minY = vertex.y;
			}
			if (vertex.y > maxY) {
				maxY = vertex.y;
			}
			if (vertex.z < minZ) {
				minZ = vertex.z;
			}
			if (vertex.z > maxZ) {
				maxZ = vertex.z;
			}
		}

		return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
	}

    @Override
    public Vector4 sampleObject() {
        return getBaricentricPoint();
    }

    public double getArea(){
        Vector4 AB = new Vector4(vertex0).sub(vertex2);
        Vector4 AC = new Vector4(vertex1).sub(vertex2);
        return AB.cross(AC).magnitude() * 0.5;
    }

    public Vector4 getBaricentricPoint(){
        double ran1 = Math.random();
        double ran2 = Math.random();
        double u = 1 - Math.sqrt(ran1);
        double v = ran2 * Math.sqrt(ran1);
        double w = 1 - u - v;
        Vector4 resp = (new Vector4(vertex0).scalarMult(u)).add(new Vector4(vertex1).scalarMult(v))
                .add(new Vector4(vertex2).scalarMult(w));
        resp.w = 1;
        return (resp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeshTriangle that = (MeshTriangle) o;

        if (!n0.equals(that.n0)) return false;
        if (!n1.equals(that.n1)) return false;
        if (!n2.equals(that.n2)) return false;
        if (!vertex0.equals(that.vertex0)) return false;
        if (!vertex1.equals(that.vertex1)) return false;
        if (!vertex2.equals(that.vertex2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vertex0.hashCode();
        result = 31 * result + vertex1.hashCode();
        result = 31 * result + vertex2.hashCode();
        result = 31 * result + n0.hashCode();
        result = 31 * result + n1.hashCode();
        result = 31 * result + n2.hashCode();
        return result;
    }

	@Override
	public Vector4 normal(Vector4 point) {
		return normal;
	}
}

