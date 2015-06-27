package edu.ar.itba.raytracer.samplers;

import edu.ar.itba.raytracer.vector.Vector2;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sampler {

    protected final int samples;
    protected final int numSets;
    protected final List<Vector2> sampleList = new ArrayList<>();
    private final List<Vector4> sampleHemisphere = new ArrayList<>();
    private int count = 0;
    private int jump = 0;

    public Sampler(int samples){
        this.samples = samples;
        this.numSets = 83;
    }


    /*
     * Generates a unit vector in a random direction with uniform probability around the hemisphere of directions about
     * the positive z axis.
     */
    public static Vector4 uniformSampleHemisphere() {
        final Vector4 result = new Vector4(0,0,0,0);
        final Random rng = new Random();
        result.z = rng.nextDouble();
        final double r = Math.sqrt(Math.max(0., 1. - result.z * result.z));
        final double phi = 2.0 * Math.PI * rng.nextDouble();
        result.x = r * Math.cos(phi);
        result.y = r * Math.sin(phi);
        return result;
    }

    public void sampleHemisphere(){

        for (int j = 0; j < samples; j++) {
            double cos_phi = Math.cos(2.0 * Math.PI * sampleList.get(j).x);
            double sin_phi = Math.sin(2.0 * Math.PI * sampleList.get(j).x);
            double cos_theta = Math.pow((1.0 - sampleList.get(j).y), 1.0 / (Math.E + 1.0));
            double sin_theta = Math.sqrt(1.0 - cos_theta * cos_theta);
            double pu = sin_theta * cos_phi;
            double pv = sin_theta * sin_phi;
            double pw = cos_theta;
            sampleHemisphere.add(new Vector3(pu, pv, pw));
        }

    }

    public void generateSamples2(){
        int n = (int) Math.sqrt((float) samples);
        for (int j = 0; j < numSets; j++)
            for (int p = 0; p < n; p++)
                for (int q = 0; q < n; q++)
                    sampleList.add(new Vector2((q + 0.5) / n, (p + 0.5) / n));
    }

    protected void shuffleIndices(){
        Collections.shuffle(sampleList);
    }

}
