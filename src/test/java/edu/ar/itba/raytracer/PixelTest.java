package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.shape.*;
import edu.ar.itba.raytracer.texture.CheckedTexture;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.PlaneTextureMapping;
import edu.ar.itba.raytracer.texture.SphericalTextureMapping;
import edu.ar.itba.raytracer.vector.Vector3;
import org.junit.Test;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.vector.Vector4;

public class PixelTest {

	@Test
	public void testPicture() throws IOException, InterruptedException {
		final int height = 480;
		final int width = 640;
		Camera camera = createScene(width, height);
		final BufferedImage image = camera.render(width, height);

		ImageIO.write(image, "png", new File("pic.png"));
	}

	private Camera createScene(final int width, final int height) {
        return null;
//		final Scene scene = new Scene(new Color(1, 1, 1));
//		final Transform cameraTransform = new Transform();
//		cameraTransform.setPosition(new Vector4(-3, -3, -1, 1));
//		cameraTransform.setRotation(new Vector4(-80, 0, -45, 0));
//		final Camera camera = scene.addCamera(width, height, 60,
//				cameraTransform);
//
////        CheckedTexture texture = new CheckedTexture(new Color(0,0,0), new Color(1,1,1),5, new PlaneTextureMapping());
////
////        final Instance i10 = new Instance(new Triangle(new Vector4(0,0,0,0), new Vector4(1,1,0,0), new Vector4(2,0,0,0),
////                new Vector4(0,0,-1,0)));
////        i10.translate(-2,-2,0);
////
////        i10.material = new Material(texture,
////                new ConstantColorTexture(new Color(0.2775, 0.2775, 0.2775)), 0.774597, 560, 1, 1.52);
////        scene.add(i10);
//
////        final Instance i9 = new Instance(new Sphere2(1));
////        i9.translate(1,1,-3);
////        i9.material = new Material(new Color(0,0,0), new Color(0.2775, 0.2775, 0.2775), 0.774597, 560, 0, 0);
////        scene.add(i9);
//
//        final Instance i8 = new Instance(new Sphere());
//        i8.translate(0,0,-1);
//        i8.material = new Material(new ConstantColorTexture(new Color(0, 0, 0)),
//                new ConstantColorTexture(new Color(0.588235, 0.670588, 0.729412)),
//                new ConstantColorTexture(new Color(0.9, 0.9, 0.9)), 750, 1, 1.52);
//        scene.add(i8);
//
//        final Instance i11 = new Instance(new Plane2(new Vector4(0,0,-1,0)));
//        i11.material = new Material(new ConstantColorTexture(new Color(0, 0, 0.0)),
//                new ConstantColorTexture(new Color(.1,.6,.1)),
//                new ConstantColorTexture(new Color(0, 0, 0)) ,0 , 0, 0);
//        scene.add(i11);
//
//        final long start = System.currentTimeMillis();
//		KdTree tree = KdTree.from(scene);
//		System.out.println("Finished building tree in "
//				+ (System.currentTimeMillis() - start));
//
//		// final Transform lightTransform = new Transform();
//		// lightTransform.setPosition(new Vector4(0, 5, 0, 1));
//		// final LightProperties lightProperties = new LightProperties(new
//		// Color(
//		// 1f, 1f, 1f));
//		// scene.addLight(lightTransform, lightProperties);
//
//        final Transform light2Transform = new Transform();
//        light2Transform.setPosition(new Vector4(-2, 5, 5, 1));
//        final LightProperties light2Properties = new LightProperties(new Color(
//                1f, 1f, 1f));
//        scene.addLight(new DirectionalLight(new Vector4(0,-1,1,0), light2Properties));
////        final Transform lightTransform3 = new Transform();
////        lightTransform2.setPosition(new Vector4(7, 8, 0, 1));
////        scene.addLight(lightTransform3, new LightProperties(new Color(1, 1, 1)));
//
//		scene.setTree(tree);
//
//		return camera;
	}
}
