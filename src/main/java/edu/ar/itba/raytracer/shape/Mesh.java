package edu.ar.itba.raytracer.shape;

import java.util.*;

import edu.ar.itba.raytracer.KdTree;
import edu.ar.itba.raytracer.Ray;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector4;
import javafx.scene.shape.TriangleMesh;

public class Mesh extends GeometricObject {

	private final List<MeshTriangle> triangles;
	private final KdTree tree;
    private final List<Integer> samplesPerTriangle;
    private int sampleSize;

	public Mesh(final Collection<MeshTriangle> triangles) {
		this.triangles = new ArrayList<>(triangles);
		final Collection<GeometricObject> instances = new ArrayList<>(
				triangles.size());
		for (final MeshTriangle t : triangles) {
			instances.add(t);
		}
		tree = KdTree.from(instances);
        samplesPerTriangle = new ArrayList<>();
        sampleSize = 0;
	}

	@Override
	public RayCollisionInfo hit(Ray ray, final CustomStack stack, final int top) {
		// double dist = Double.MAX_VALUE;
		// RayCollisionInfo minCollision = null;
		// for (final MeshTriangle triangle : triangles) {
		// final RayCollisionInfo collision = triangle.hit(ray, stack, top);
		// if (collision != null && collision.distance < dist) {
		// dist = collision.distance;
		// minCollision = collision;
		// }
		// }
		// return minCollision;
		return tree.getCollision(Double.MAX_VALUE, ray, stack, top);
	}

	@Override
	public AABB getAABB() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (final MeshTriangle triangle : triangles) {
            final AABB aabb = triangle.getAABB();
            if (aabb.minX < minX) {
                minX = aabb.minX;
            }
            if (aabb.maxX > maxX) {
                maxX = aabb.maxX;
            }
            if (aabb.minY < minY) {
                minY = aabb.minY;
            }
            if (aabb.maxY > maxY) {
                maxY = aabb.maxY;
            }
            if (aabb.minZ < minZ) {
                minZ = aabb.minZ;
            }
            if (aabb.maxZ > maxZ) {
                maxZ = aabb.maxZ;
            }
        }

        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public Vector4 sampleObject(){
        synchronized (this) {
            if (sampleSize == 0) {
                generateSampleList();
            }
        }
        int index = (int)(sampleSize);
        for(int i = 0; i < triangles.size(); i++){
            if((index -= samplesPerTriangle.get(i)) <= 0){
                return triangles.get(i).getBaricentricPoint();
            }

        }
        System.out.println("should never happen");
        return triangles.get(0).getBaricentricPoint();
    }

    private void generateSampleList(){
        double min = Double.MAX_VALUE;
        List<Double> areas = new ArrayList<>();
        for(MeshTriangle triangle: triangles){
            double area = triangle.getArea();
            if(area < min){
                min = area;
            }
            areas.add(area);
        }
        for(int j = 0; j< areas.size(); j++){
            samplesPerTriangle.add(j, (int) Math.ceil(areas.get(j) / min));
            sampleSize += (int)Math.ceil(areas.get(j)/min);
        }
    }

}
