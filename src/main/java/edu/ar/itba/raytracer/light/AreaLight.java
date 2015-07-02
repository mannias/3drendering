package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class AreaLight extends PositionLight{

    private Instance object;
    private final Matrix44 transform;
    
    private Vector4 normal;
    private Vector4 inc;

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
        final Vector4 ret = new Vector4(position);
        ret.sub(hitPoint);
        ret.normalize();
        return ret;
    }

    @Override
    public Color getIntensity(final RayCollisionInfo hitPoint) {
    	if (position == null) {
    		getPosition(hitPoint);
    	}
    	final double ndotd = -normal.dot(inc);
    	
    	if (ndotd > 0) {
    		return new Color(color).scalarMult(ndotd);//.clamp();
    	}
        return new Color(0,0,0);
    }

    public void setObject(Instance object){
        this.object = object;
    }

    public Vector4 getPosition(final RayCollisionInfo collision){
    	// TODO: Esto no sirve para multithread.
        position = transform.multiplyVec(object.sampleObject());
        normal = object.normal(position);
        inc = new Vector4(position).sub(collision.worldCollisionPoint).normalize();
        return position;
    }

}
