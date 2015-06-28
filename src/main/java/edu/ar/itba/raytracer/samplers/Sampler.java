package edu.ar.itba.raytracer.samplers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

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

    public Vector4 getSample(){
    	final int size = sampleList.size();
        int n = (int) Math.sqrt((float) samples);
        final Vector2 sample = new Vector2(Math.random(), Math.random());
    	
        double cos_phi = Math.cos(2.0 * Math.PI * sample.x);
        double sin_phi = Math.sin(2.0 * Math.PI * sample.x);
        double cos_theta = Math.pow((1.0 - sample.y), 1.0 / (1 + 1.0));
        double sin_theta = Math.sqrt(1.0 - cos_theta * cos_theta);
        double pu = sin_theta * cos_phi;
        double pv = sin_theta * sin_phi;
        double pw = cos_theta;
        
        if(Double.isNaN(pu) || Double.isNaN(pv) || Double.isNaN(pw)) {
			System.out.println("SASA");
		}
        
        return new Vector3(pu, pv, pw);
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
}

