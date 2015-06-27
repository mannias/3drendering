package edu.ar.itba.raytracer.light;

import com.sun.tools.classfile.TypeAnnotation;
import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.GeometricObject;
import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector4;

public class AreaLight extends PositionLight{

    public AreaLight(final Color color, final Instance obj, final Matrix44 transform) {
        super(color,obj.getAABB().center,transform);
    }

    public AreaLight(final Color color) {
        super(color);
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

    public void setPosition(final Instance object, final Matrix44 transform){

        super.setPosition(object.getAABB().getCorners().get(0), transform);
    }
}
