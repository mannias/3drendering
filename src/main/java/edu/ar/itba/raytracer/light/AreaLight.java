package edu.ar.itba.raytracer.light;

import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class AreaLight extends PositionLight{

    private Instance object;
    private final Matrix44 transform;

    public AreaLight(final Color color, Instance obj, final Matrix44 transform) {
        super(color);
        this.object = obj;
        this.transform = transform;
    }

    public AreaLight(final Color color, final Matrix44 transform) {
        super(color);
        this.transform = transform;
    }

    @Override
    public Vector4 getDirection(final Vector4 hitPoint) {
        final Vector4 ret = new Vector4(position);
        ret.sub(hitPoint);
        ret.normalize();
        return ret;
    }

    @Override
    public Color getIntensity(final Vector4 hitPoint) {
        return color;
    }

    public void setObject(Instance object){
        this.object = object;
    }

    public Vector4 getPosition(){
        position = transform.multiplyVec(object.sampleObject());
        position.z = position.z - 0.001;
        return position;
    }
}
