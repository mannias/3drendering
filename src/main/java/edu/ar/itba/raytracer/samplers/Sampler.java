package edu.ar.itba.raytracer.samplers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

import edu.ar.itba.raytracer.vector.*;

public class Sampler {

    protected final int samples;
    protected final int numSets;
    protected final List<Vector2> sampleList = new ArrayList<>();
    private final Queue<Vector4> sampleHemisphere = new LinkedBlockingQueue<>();
    private int count = 0;
    private int jump = 0;

    public Sampler(int samplesPerPixel, int sets){
        this.samples = samplesPerPixel;
        this.numSets = sets;
    }

    public Vector4 getSample(final double e){
    	final int size = sampleList.size();
        int n = (int) Math.sqrt((float) samples);
        final Vector2 sample = new Vector2(Math.random(), Math.random());
    	
        double cos_phi = Math.cos(2.0 * Math.PI * sample.x);
        double sin_phi = Math.sin(2.0 * Math.PI * sample.x);
        double cos_theta = Math.pow((1.0 - sample.y), 1.0 / (e + 1.0));
        double sin_theta = Math.sqrt(1.0 - cos_theta * cos_theta);
        double pu = sin_theta * cos_phi;
        double pv = sin_theta * sin_phi;
        double pw = cos_theta;
        
        if(Double.isNaN(pu) || Double.isNaN(pv) || Double.isNaN(pw)) {
			System.out.println("SASA");
		}



        return new Vector3(pu, pv, pw);
    }

    public Vector4 getSample2(Vector4 normal, double e){
        // Generate random numbers
        double z, phi, theta;
        z = Math.random();
        phi = Math.random() * 2 * Math.PI;
        theta = (e == 1 ? Math.acos(Math.sqrt(z)) : Math.acos(Math.pow(z, 1 / (e + 1))));

        // Create vector aligned with z=(0,0,1)
        double sintheta = Math.sin(theta);
        Vector4 sample = new Vector4(sintheta*Math.cos(phi), sintheta*Math.sin(phi), z,0);

        // Rotate sample to be aligned with normal
        Vector4 t = new Vector4(Math.random(), Math.random(), Math.random(),0);
        Vector4 u = t.cross(normal); u.normalize();
        Vector4 v = new Vector4(normal).cross(u);

        Matrix33 rot = new Matrix33(u, v, normal);
        rot = rot.transpose();
        return rot.multiplyVec(sample);
    }

//    public Vector4 getSample() {
//        return sampleHemisphere.poll();
//    }

    public void generateSamples(){
        int n = (int) Math.sqrt((float) samples);
        
        for (int j = 0; j < numSets; j++) {
            for (int p = 0; p < n; p++) {
                for (int q = 0; q < n; q++) {
                    sampleList.add(new Vector2((q + Math.random()) / n, (p + Math.random()) / n));
                }
            }
        }
    }

    public static Vector4 cosWeightedHemisphere( Vector4 oNormal )
    {
        double Xi1 = Math.random();
        double Xi2 = Math.random();
        double  theta = Math.acos(Math.sqrt(1.0-Xi1));
        double  phi = 2.0 * Math.PI * Xi2;
        double xs = Math.sin(theta) * Math.cos(phi);
        double ys = Math.cos(theta);
        double zs = Math.sin(theta) * Math.sin(phi);
        Vector4 normal = new Vector4(oNormal).normalize();
        Vector4 y = new Vector4(normal);
        Vector4 h = y;
        if (Math.abs(h.x)<=Math.abs(h.y) && Math.abs(h.x)<=Math.abs(h.z))
            h.x= 1.0;
        else if (Math.abs(h.y)<=Math.abs(h.x) && Math.abs(h.y)<=Math.abs(h.z))
            h.y= 1.0;
        else
            h.z= 1.0;
        Vector4 x = (h.cross(y)).normalize();
        Vector4 z = (x.cross(y)).normalize();
        Vector4 direction = x.scalarMult(xs).add(y.scalarMult(ys)).add(z.scalarMult(zs));
        direction.w = 1;
        return direction.normalize();
    }
}

