package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Vector4;

public class SpotLight extends Light {

	private final Vector4 dir;
	private final double angle;
	
	public SpotLight(final Vector4 dir, final double angle) {
		this.dir = dir;
		this.angle = angle;
	}
	
	public void m(final Vector4 x, final Vector4 w) {
		final Vector4 L = new Vector4(x);
		L.sub(getTransform().getPosition());
		
		final double res = Math.max(0, L.dot(dir)/Math.acos(angle)) * dirac(w.sub(L));
	}
	
	public double dirac(final Vector4 arg) {
		if (arg.x == 0 && arg.y ==0 && arg.z == 0 && arg.w==0){
			return 1;
		}
		return 0;
	}

    @Override
    public Vector4 getDirection(Vector4 hitPoint) {
        return null;
    }

    @Override
    public Color getIntensity(Vector4 hitPoint) {
        return null;
    }
}
