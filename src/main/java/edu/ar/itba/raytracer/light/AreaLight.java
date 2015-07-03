package edu.ar.itba.raytracer.light;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class AreaLight extends PositionLight{

    public Instance object;
    private final Matrix44 transform;
    
    public AreaLight(final Color color, Instance obj, final Matrix44 transform, final double gain) {
        super(color.scalarMult(gain));
        this.object = obj;
        this.transform = transform;
        System.out.println(gain);
        System.out.println(color);
    }

    public AreaLight(final Color color, final Matrix44 transform, final double gain) {
        this(color,null,transform,gain);
    }

    @Override
    public Vector4 getDirection(final Vector4 hitPoint) {
        final Vector4 ret = new Vector4(getPosition());
        ret.sub(hitPoint);
        ret.normalize();
        return ret;
    }

    @Override
    public Color getIntensity(final RayCollisionInfo hitPoint) {
    	final double ndotd = Math.abs(object.normal(hitPoint.getLocalCollisionPoint()).dot(hitPoint.getRay().dir));
    	
		final Color c = new Color(color).scalarMult(ndotd);//.clamp();
		return c;
    }

    public void setObject(Instance object){
        this.object = object;
    }

    public Vector4 getPosition(){
        final Vector4 position = transform.multiplyVec(object.sampleObject());
        
        return position;
    }
    
}
