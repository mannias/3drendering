package edu.ar.itba.raytracer.properties;

import java.util.concurrent.atomic.AtomicInteger;

public class Profiler {

    private final int totalSize;
    private AtomicInteger sum;
    private int percentage;

    public Profiler(final int totalSize){
        this.totalSize = totalSize;
        this.sum = new AtomicInteger(0);
        this.percentage = 0;
    }

    public void sumValue(){
        int actualSum = sum.incrementAndGet();
        if(actualSum % (totalSize/100) == 0){
            System.out.println("Percentage: " + percentage++  + "%");
        }
    }
}
