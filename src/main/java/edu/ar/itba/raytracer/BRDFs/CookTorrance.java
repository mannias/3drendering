package edu.ar.itba.raytracer.BRDFs;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.Scene;
import edu.ar.itba.raytracer.Scene.LightingInfo;
import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.vector.Vector4;

public class CookTorrance {

    public static Color getIrradiance(RayCollisionInfo collision, Scene scene, CustomStack stack, Vector4 cameraPosition)
    {
        final Vector4 collisionPoint = collision.getWorldCollisionPoint();
        final Material objectMaterial = collision.getObj().material;
        final Vector4 deltaNormal = new Vector4(collision.normal);
        deltaNormal.scalarMult(.001f);
        final Vector4 collisionPointPlusDelta = new Vector4(collisionPoint);
        collisionPointPlusDelta.add(deltaNormal);

        final Color kd = objectMaterial.kd.getColor(collision);
        final Color ks = objectMaterial.ks.getColor(collision);

        double cook = 0d;

        // set important material values
        double roughnessValue = 1- objectMaterial.roughness; // 0 : smooth, 1: rough
        double F0 = objectMaterial.fresnel; // fresnel reflectance at normal incidence

        Vector4 cameraDirection = new Vector4(cameraPosition).sub(collisionPoint);

        for (final Light light : scene.getLights()) {
        	final LightingInfo li = scene.isIlluminati(collisionPointPlusDelta, light, stack, collision);
            if (!li.lightHits) {
                continue;
            }


            final Vector4 lightVersor = li.dir;
            Vector4 normal = collision.normal.normalize();

            // do the lighting calculation for each fragment.
            double NdotL = Math.max(normal.dot(lightVersor), 0.0);
            if(NdotL > 0.0 || roughnessValue != 1)
            {
                Vector4 eyeDir = cameraDirection.normalize();

                // calculate intermediary values
                Vector4 halfVector = new Vector4(lightVersor).add(eyeDir).normalize();
                double NdotH = Math.max(normal.dot(halfVector), 0.0);
                double NdotV = Math.max(normal.dot(eyeDir), 0.0); // note: this could also be NdotL, which is the same value
                double VdotH = Math.max(eyeDir.dot(halfVector), 0.0);
                double mSquared = roughnessValue * roughnessValue;

                // geometric attenuation
                double NH2 = 2.0 * NdotH;
                double g1 = (NH2 * NdotV) / VdotH;
                double g2 = (NH2 * NdotL) / VdotH;
                double geoAtt = Math.min(1.0, Math.min(g1, g2));

                // roughness (or: microfacet distribution function)
                // beckmann distribution function
                double r1 = 1.0 / ( 4.0 * mSquared * Math.pow(NdotH, 4.0));
                double r2 = (NdotH * NdotH - 1.0) / (mSquared * NdotH * NdotH);
                double roughness = r1 * Math.exp(r2);

                // fresnel
                // Schlick approximation
                double fresnel = Math.pow(1.0 - VdotH, 5.0);
                fresnel *= (1.0 - F0);
                fresnel += F0;
                if(NdotV != 0d && NdotL != 0d){
                    cook += (fresnel *geoAtt*roughness)/(Math.PI * NdotV * NdotL);
                }

            }
        }
        cook /= Math.PI;
        return new Color(kd).scalarMult(1/Math.PI).add(new Color(ks).scalarMult(cook));
    }


}
