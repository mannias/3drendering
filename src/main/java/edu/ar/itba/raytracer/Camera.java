package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ar.itba.raytracer.Scene.LightingInfo;
import edu.ar.itba.raytracer.BRDFs.CookTorrance;
import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.materials.MaterialType;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Profiler;
import edu.ar.itba.raytracer.properties.RayTracerParameters;
import edu.ar.itba.raytracer.samplers.Sampler;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.vector.Matrix33;
import edu.ar.itba.raytracer.vector.Matrix44;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

public class Camera extends SceneElement {

	private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();

	public static enum Extension {
		PNG("png");

		private final String extensionString;

		private Extension(final String str) {
			extensionString = str;
		}

	}

	static {
		SUPPORTED_EXTENSIONS.add("png");
	}

	private final int pictureWidth;
	private final int pictureHeight;
	private final double distToPixels;
	private final Color[][] picture;

	private final int rayDepth;
    private final int samplesPerPixel;

	private Vector4 forwardVector;

	private Vector4 u;
	private Vector4 v;

	private final Vector4[] pixelPoints;

	private final Scene scene;

    private final Sampler sampler;

    private final RayTracerParameters parameters;
    
	public Camera(final Scene scene, final int pictureWidth,
			final int pictureHeight, final double fov, final Vector4 position,
			final Vector4 lookAt, final Vector4 up, final Matrix44 transform,
			RayTracerParameters parameters) {
		this.scene = scene;
		this.pictureWidth = pictureWidth;
		this.pictureHeight = pictureHeight;
		this.distToPixels = calculateDistanceToPixels(fov);
		picture = new Color[pictureHeight][pictureWidth];
		initPicture();


		final Vector4 w = new Vector4(position);
		w.sub(lookAt);
		w.normalize();

		// final Vector4 transformedW = transform.multiplyVec(w);
		final Vector4 transformedW = transform.multiplyVec(w);
		transformedW.normalize();

		final Vector4 transformedUp = transform.multiplyVec(up);
		transformedUp.normalize();

		u = transformedUp.cross(transformedW);
		u.normalize();

		v = transformedW.cross(u);
		
		forwardVector = new Vector4(transformedW);
		forwardVector.scalarMult(-distToPixels);
		pixelPoints = new Vector4[pictureHeight * pictureWidth];
		for (int i = 0; i < pictureHeight * pictureWidth; i++) {
			final int x = i % pictureWidth;
			final int y = i / pictureWidth;
			pixelPoints[i] = new Vector4(forwardVector);
			final Vector4 aux1 = new Vector4(v);
			aux1.scalarMult(-y + pictureHeight / 2);
			final Vector4 aux2 = new Vector4(w);
			aux2.scalarMult(x - pictureWidth / 2);
			pixelPoints[i].add(aux1);
			pixelPoints[i].add(aux2);
		}

		this.position = position;

        this.parameters = parameters;

		if (parameters.pathTracer) {
			this.samplesPerPixel = parameters.traceSamples;
			this.rayDepth = parameters.traceDepth;
		} else {
			this.samplesPerPixel = parameters.aaSamples;
			this.rayDepth = parameters.rayDepth;
		}
        sampler = new Sampler(samplesPerPixel, pictureWidth * pictureHeight);
        profiler = new Profiler(pictureWidth*pictureHeight);
	}

	private final Vector4 position;

	private Ray getPrimaryRay(final double x, final double y) {
		// position.w = 1;
		// final Vector4 dir = new Vector4(forwardVector);
		// final Vector4 aux1 = new Vector4(heightVector);
		// aux1.scalarMult(y);
		// final Vector4 aux2 = new Vector4(widthVector);
		// aux2.scalarMult(x);
		// dir.add(aux1);
		// dir.add(aux2);
		// dir.w = 0;

		final Vector4 dir2 = new Vector4(forwardVector.x + v.x * y + u.x * x,
				forwardVector.y + v.y * y + u.y * x, forwardVector.z + v.z * y
						+ u.z * x, 0);
		dir2.normalize();
		return new Ray(position, dir2);
		// return new Ray(position, forwardVector.add(
		// heightVector.scalarMult(-y + pictureHeight / 2)).add(
		// widthVector.scalarMult(x - pictureWidth / 2)));
		// return new Ray(position,
		// pixelPoints[y * pictureWidth + x].sub(position));
	}

	/**
	 * Creates a picture of what the camera is currently seeing.
	 * 
	 * @return a BufferedPixel with pixels representing what this camera is
	 *         currently seeing.
	 */
	private BufferedImage takePicture() {
		BufferedImage image = new BufferedImage(pictureWidth, pictureHeight,
				BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, pictureWidth, pictureHeight, getRgbFromColors(), 0,
				pictureWidth);

		return image;
	}

	/**
	 * Transforms the {@code picture} into an array of pixels that can be passed
	 * to an RGB {@link BufferedImage}
	 * 
	 * @return an array with {@code pictureWidth} * {@code pictureHeight} slots
	 *         that contains 24-bit pixels (i.e. 8 bits per color)
	 */
	private int[] getRgbFromColors() {
		final int[] pixels = new int[pictureWidth * pictureHeight];
		for (int i = 0; i < pictureHeight; i++) {
			for (int j = 0; j < pictureWidth; j++) {
				final Color color = picture[i][j];
				final int rgb = ((int) (color.getRed() * 255) << 16)
						+ ((int) (color.getGreen() * 255) << 8)
						+ (int) (color.getBlue() * 255);
				pixels[i * pictureWidth + j] = rgb;
			}
		}

		return pixels;
	}

	/**
	 * Initializes the picture grid with the default color.
	 */
	private void initPicture() {
		for (int i = 0; i < pictureHeight; i++) {
			for (int j = 0; j < pictureWidth; j++) {
				picture[i][j] = Color.DEFAULT_COLOR;
			}
		}
	}

	/**
	 * Calculates the distance from the camera to the grid on which the scene
	 * will be projected.
	 * 
	 * <p>
	 * This method should be called in the constructor and it assumes that
	 * {@code pictureWidth} and {@code pictureHeight} have been set.
	 * 
	 * @param fov
	 *            the field of view.
	 * @return the distance to the pixel grid.
	 */
	private double calculateDistanceToPixels(final double fov) {
		final double halfDim;
		if (pictureWidth > pictureHeight) {
			halfDim = pictureWidth / 2.0f;
		} else {
			halfDim = pictureHeight / 2.0f;
		}
		return halfDim / Math.tan(fov / 2 * Math.PI / 180);
	}

	public BufferedImage render() {
		final BufferedImage i = this.render(pictureWidth, pictureHeight);
		return i;
	}

	public BufferedImage render(final int width, final int height) {
		final int pixels = width * height;
		final int cores = Runtime.getRuntime().availableProcessors();
		final int pixelsPerTask = 32 * 32;
		// final int pixelsPerTask = pixels;
		final ForkJoinTask<?>[] threads = new ForkJoinTask[cores];
		final Runnable[] runnables = new Runnable[cores];
		final AtomicInteger startPixel = new AtomicInteger();
		for (int i = 0; i < cores; i++) {
			runnables[i] = new Runnable() {
				@Override
				public void run() {
                    if(parameters.pathTracer) {
                    	trace(startPixel, pixelsPerTask, pixels, width, (x,y,z) -> pathShade(x, y, z, 0));
                    }else{
                    	trace(startPixel, pixelsPerTask, pixels, width,(x,y,z) -> shade(x, y, z));
                    }
				}
			};
			threads[i] = ForkJoinTask.adapt(runnables[i]);
		}
		final ForkJoinPool fjp = new ForkJoinPool(Runtime.getRuntime()
				.availableProcessors());
		for (ForkJoinTask<?> fjt : threads) {
			fjp.execute(fjt);
		}

		for (ForkJoinTask<?> fjt : threads) {
			fjt.join();
		}
//        dynamicRange();
		
		toneMapping(width, height);
        return takePicture();
	}
	
	private void toneMapping(final int w, final int h) {
		
		final Matrix33 rgb2xyz = new Matrix33(0.4124564, 0.3575761, 0.1804375,
				0.2126729, 0.7151522, 0.0721750, 0.0193339, 0.1191920,
				0.9503041);
		
		final Matrix33 xyz2rgb = new Matrix33(3.2404542, -1.5371385,
				-0.4985314, -0.9692660, 1.8760108, 0.0415560, 0.0556434,
				-0.2040259, 1.0572252);
		
		final Vector3[][] xyzs = new Vector3[h][w];
		
		double maxLuminosity = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				final Color rgb = picture[j][i];
				final Vector3 rgbVec = new Vector3(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
				
				final Vector3 xyzVec = rgb2xyz.multiplyVec(rgbVec);
				
				maxLuminosity = Math.max(maxLuminosity, xyzVec.y);
				
				xyzs[j][i] = xyzVec;
			}
		}
		
		final double scale = 1 / maxLuminosity;
		
		
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				final Vector3 xyz = xyzs[j][i];
				xyz.scalarMult(scale);
				final Vector3 rgb = xyz2rgb.multiplyVec(xyz);
				picture[j][i] = new Color(rgb.x, rgb.y, rgb.z);
			}
		}
		
	}
	
    private final Profiler profiler;

    private void trace(AtomicInteger startPixel, int pixelsPerTask, int pixels, int width,
                       ShadeFunction func){

        final CustomStack stack = new CustomStack();
        int currentStart;
        
        
        while ((currentStart = startPixel.getAndAdd(pixelsPerTask)) < pixels) {
            final int endPixel = (currentStart + pixelsPerTask) >= pixels ? pixels
                    : currentStart + pixelsPerTask;
            for (int i = currentStart; i < endPixel; i++) {
                final int x = i % width;
                final int y = i / width;
                double pixelRed = 0;
                double pixelGreen = 0;
                double pixelBlue = 0;
                
                for (int s = 0 ; s< samplesPerPixel; s++) {
						final double ppx = x - .5 * pictureWidth
								+ Math.random();
						final double ppy = -y + .5 * pictureHeight
								+ Math.random();
						stack.reset();
						Color c = func.shade(getPrimaryRay(ppx, ppy), rayDepth,
								stack);
						if (Double.isNaN(c.getRed())
								|| Double.isNaN(c.getBlue())
								|| Double.isNaN(c.getGreen())) {
                        }else {
                            pixelRed += c.getRed();
                            pixelGreen += c.getGreen();
                            pixelBlue += c.getBlue();
                        }
                }
                Color result = new Color(pixelRed / samplesPerPixel, pixelGreen
                        / samplesPerPixel, pixelBlue / samplesPerPixel).clamp();
                
                picture[y][x] = result.gammaCorrect(2.2);
                profiler.sumValue();
            }
        }
    }

    private Color pathShade(final Ray ray, final int rayDepth,
                            final CustomStack stack, double distance) {
        final RayCollisionInfo collision = castRay(ray, stack);
        if (collision == null) {
//        	return new Color(1,0,1);
            return scene.getAmbientLight();
        }
        final Vector4 collisionPoint = collision.getWorldCollisionPoint();
        final Material objectMaterial = collision.getObj().material;
        final Vector4 deltaNormal = new Vector4(collision.normal);
        deltaNormal.scalarMult(.001f);
        final Vector4 collisionPointPlusDelta = new Vector4(collisionPoint);
        collisionPointPlusDelta.add(deltaNormal);

        if(rayDepth != this.rayDepth) {
            distance += collision.distance;
        }

        double survival = 1d;
        if (objectMaterial.light != null) {
        	final Color c;
        	if (rayDepth == this.rayDepth) {
        		c = new Color(objectMaterial.kd.getColor(collision));
        	} else {
                c = new Color(objectMaterial.light.getIntensity(collision)).scalarMult(1.5d/(1d+0.15*Math.abs(distance)+0.02*distance*distance));
        	}
        	return c;
        }

        Color pathColor = new Color(0,0,0);

        if (rayDepth <= 0) {
        	return pathColor;
        }
//            Color weight = new Color(0,0,0);
//            switch(objectMaterial.type){
//                case Matte: weight = kd; break;
//                case Specular: weight = ks; break;
//                case Glass: weight = transparency; break;
//                case Glossy: new Color(kd).mult(ks);break;
//            }
//            double max = Math.max(weight.getRed(), Math.max(weight.getGreen(), weight.getBlue()));
//            survival = 1/max;
//            if(max > Math.random()){
//                return pathColor;
//            }
//
//        }

        if (objectMaterial.type == MaterialType.Matte) {
            if(parameters.direct){
                Color resp2 = directLight(collision, collisionPointPlusDelta, stack, survival, rayDepth, distance);
                pathColor = pathColor.add(resp2);
            }
            if(parameters.indirect) {
        		pathColor = pathColor.add(indirectLightDiffuse(collision, collisionPointPlusDelta, stack,survival, rayDepth - 1,distance));//
        	}
        } else if (objectMaterial.type == MaterialType.Specular) {
            pathColor = pathColor.add(specularReflect(collision, collisionPointPlusDelta, stack, survival, rayDepth, distance));

        } else if (objectMaterial.type == MaterialType.Glossy) {

            if(parameters.direct){
                Color resp2 = directLight(collision, collisionPointPlusDelta, stack, survival, rayDepth, distance);
                pathColor = pathColor.add(resp2);
            }
            if(parameters.indirect) {
                pathColor = pathColor.add(indirectGlossy(collision, collisionPointPlusDelta, stack, survival, rayDepth, distance));
            }

        } else if (objectMaterial.type == MaterialType.Glass) {
            pathColor = pathColor.add(indirectGlass(collision, collisionPointPlusDelta,stack,survival,rayDepth,distance));
        }
        return pathColor;

    }

    private Color directLight(RayCollisionInfo collision, Vector4 collisionPointPlusDelta,
                                     CustomStack stack, double survival, int rayDepth, double distance){
        Color intensity = new Color(0,0,0);
        final Vector4 normal = collision.normal;
        Material objectMaterial = collision.getObj().material;
        final Color kd = objectMaterial.kd.getColor(collision);
        final Color ks = objectMaterial.ks.getColor(collision);
        final Vector4 v = collision.getRay().dir.neg();

        for (final Light light : scene.getLights()) {
            final LightingInfo li = scene.isIlluminati(collisionPointPlusDelta, light, stack, collision);
            if (!li.lightHits) {
                continue;
            }

            final Vector4 lightVersor = li.dir;
            final double ln = lightVersor.dot(normal);
            if (ln > 0) {
                final Color lightColor = light.getIntensity(li.rci);

                final Color diffuse = new Color(lightColor);
                diffuse.scalarMult(ln);
                diffuse.mult(kd).scalarMult(1/Math.PI);
                intensity = intensity.add(diffuse);
                if(objectMaterial.type == MaterialType.Glossy) {
                    final Color specular = new Color(lightColor);
                    double cook = CookTorrance.getFactor(collision, scene, stack, position, lightVersor);
                    specular.mult(ks).scalarMult(cook);
                    intensity = intensity.add(specular);
                }
            }
        }



        return intensity;
    }

    private Color indirectLightDiffuse(RayCollisionInfo collision, Vector4 collisionPointPlusDelta, CustomStack stack,
                                       double survival, final int rayDepth, double distance){
        Material objectMaterial = collision.getObj().material;
        final Vector4 wi = sampler.getSample2(collision.normal,objectMaterial.roughness);
        final Color color = new Color(objectMaterial.kd.getColor(collision));
        final Color newColor = pathShade(new Ray(collisionPointPlusDelta, wi), rayDepth, stack,
                distance);
        return color.mult(newColor);
    }

    private Color specularReflect(RayCollisionInfo collision, Vector4 collisionPointPlusDelta, CustomStack stack,
                                  double survival, final int rayDepth, double distance){
        Color intensity = new Color(0,0,0);
        Material objectMaterial = collision.getObj().material;
        Ray ray = collision.getRay();
        Vector4 wo = ray.dir.neg();
        double ndotwo = collision.normal.dot(wo);
        Vector4 reflectedDir = wo.neg().add(new Vector4(collision.normal).scalarMult(2 * ndotwo));
        Color color = new Color(objectMaterial.ks.getColor(collision));
        if(color.isBlack()){
            color = new Color(objectMaterial.transparency.getColor(collision));
        }
        final Ray reflectedRay = new Ray(collisionPointPlusDelta,
                reflectedDir);
        Color newColor = pathShade(reflectedRay,rayDepth-1,stack, distance);
        return intensity.add(color.mult(newColor));
    }

    private Color indirectGlossy(RayCollisionInfo collision, Vector4 collisionPointPlusDelta, CustomStack stack,
                                 double survival, final int rayDepth, double distance){

        Material objectMaterial = collision.getObj().material;
        final Color ks = objectMaterial.ks.getColor(collision);
        Vector4 wo = new Vector4(collision.getRay().dir);
        double ndotwo = wo.dot(collision.normal);
        Vector4 reflectedDir = wo.sub(new Vector4(collision.normal).scalarMult(2 * ndotwo));
        Vector4 wi = sampler.getSample2(reflectedDir,1/objectMaterial.roughness);
        while (collision.normal.dot(wi) < 0) {
            wi = sampler.getSample2(reflectedDir,collision.getObj().material.roughness);
        }
        Color color = new Color(ks);
        final Ray reflectedRay = new Ray(collisionPointPlusDelta, wi);
        Color newColor = pathShade(reflectedRay, rayDepth - 1, stack, distance);
        double NdotL = new Vector4(collision.normal).dot(wi);
        double cook = CookTorrance.getFactor(collision, scene, stack, position,reflectedDir);
        return newColor.mult(color).scalarMult(cook*NdotL);
    }

    private Color indirectGlass(RayCollisionInfo collision, Vector4 collisionPointPlusDelta, CustomStack stack,
                                double survival, final int rayDepth, double distance) {

        Color pathColor = new Color(0,0,0);
        Ray ray = collision.getRay();
    	final Vector4 normal = new Vector4(collision.normal);
        final Vector4 v = new Vector4(ray.getDir());
        v.scalarMult(-1);
		final double nv = normal.dot(v);
    	final Vector4 tNormal = new Vector4(collision.normal);
		final Material objectMaterial = collision.obj.material;
        double cosThetaI = nv;
        double eta = objectMaterial.refractionIndex;
		if (nv < 0) {
			eta = 1 / eta;
			tNormal.scalarMult(-1);
			cosThetaI = -cosThetaI;
		}

		final double xx = 1 - (1 - cosThetaI * cosThetaI) / (eta * eta);
		if (xx >= 0) {
			final Color materialRefractionColor = objectMaterial.transparency.getColor(collision);
			if (materialRefractionColor.getRed() != 0 || materialRefractionColor.getGreen() != 0 || materialRefractionColor.getBlue() != 0) {

                final Vector4 aux = new Vector4(tNormal);
				aux.scalarMult(Math.sqrt(xx) - cosThetaI / eta);

				final Vector4 refractedDir = new Vector4(v);
				refractedDir.scalarMult(-1 / eta);
				refractedDir.sub(aux);

				final Vector4 aux2 = new Vector4(collision.getWorldCollisionPoint());
				tNormal.scalarMult(.001f);
				aux2.sub(tNormal);

				final Ray refractedRay = new Ray(aux2, refractedDir);

				final Color refractedColor = pathShade(refractedRay, rayDepth - 1, stack, distance);
                final Color reflectedColor = specularReflect(collision,collisionPointPlusDelta,stack,survival,rayDepth,distance);
                pathColor = pathColor.add(reflectedColor);
				pathColor = pathColor.add(refractedColor.mult(materialRefractionColor));

            }
		}else{
            final Color reflectedColor = specularReflect(collision,collisionPointPlusDelta,stack,survival,rayDepth,distance);
            pathColor = pathColor.add(reflectedColor);

        }
		return pathColor;
    }

	private Color shade(final Ray ray, final int rayDepth,
			final CustomStack stack) {
        final RayCollisionInfo collision = castRay(ray, stack);

		if (collision == null) {
			return Color.DEFAULT_COLOR;
		}

		final Vector4 collisionPoint = collision.getWorldCollisionPoint();
		final Vector4 normal = collision.normal;
		final Vector4 deltaNormal = new Vector4(normal);
		deltaNormal.scalarMult(.001f);
		final Vector4 collisionPointPlusDelta = new Vector4(collisionPoint);
		collisionPointPlusDelta.add(deltaNormal);

		// Invert the ray direction to get the view versor. The direction is
		// already normalized, so there is no need to normalize again.
		final Vector4 v = new Vector4(ray.getDir());
		v.scalarMult(-1);

		final Material objectMaterial = collision.getObj().material;
		final Color ka = objectMaterial.ka.getColor(collision);
		final Color kd = objectMaterial.kd.getColor(collision);
		final Color ks = objectMaterial.ks.getColor(collision);
		final double shininess = objectMaterial.shininess;

		Color intensity = new Color(scene.getAmbientLight());
		intensity.mult(ka);

        if(objectMaterial.light != null){
        	if (rayDepth == this.rayDepth) {
        		return objectMaterial.kd.getColor(collision);
        	}
            return objectMaterial.light.getIntensity(collision);
        }

		for (final Light light : scene.getLights()) {
			// Move the from point a little in the direction of the normal
			// vector, to avoid rounding problems and intersecting with the same
			// object we're starting from.
			
			final LightingInfo li = scene.isIlluminati(collisionPointPlusDelta, light, stack, collision);
			if (!li.lightHits) {
                continue;
			}

			final Vector4 lightVersor = li.dir;

			// final Vector4 lightVersor = new Vector4(light.getTransform()
			// .getPosition());
			// lightVersor.sub(collisionPoint);
			// lightVersor.normalize();

			final double ln = lightVersor.dot(normal);

			if (ln > 0) {
				final Color lightColor = light.getIntensity(li.rci);

				final Color diffuse = new Color(lightColor);
				diffuse.scalarMult(ln);
				diffuse.mult(kd).scalarMult(1/Math.PI);

				// final Color diffuse = new Color(diffuseRed, diffuseGreen,
				// diffuseBlue);
				intensity = intensity.add(diffuse);

				final Vector4 r = new Vector4(normal);
				r.scalarMult(2 * ln);
				r.sub(lightVersor);
				final double rv = r.dot(v);
				if (rv > 0) {
					final Color specular = new Color(lightColor);

					final Color ksAux = new Color(ks);
					ksAux.scalarMult(Math.pow(rv, shininess));

					specular.mult(ksAux);
					intensity = intensity.add(specular);
				}
			}
		}

		if (rayDepth > 0) {
			final double nv = normal.dot(v);

			if (shininess != 0) {
				final Vector4 reflectedDir = new Vector4(normal);
				reflectedDir.scalarMult(2 * nv);
				reflectedDir.sub(v);
				reflectedDir.w = 0;
				final Ray reflectedRay = new Ray(collisionPointPlusDelta,
						reflectedDir);

				final Color reflectedColor = shade(reflectedRay, rayDepth - 1,
						stack);
				reflectedColor.scalarMult(shininess / Material.MAX_SHININESS);

				intensity = intensity.add(reflectedColor);
			}
			Vector4 tNormal = new Vector4(normal);
			double cosThetaI = nv;
			double eta = objectMaterial.refractionIndex;
			if (nv < 0) {
				eta = 1 / eta;
				tNormal.scalarMult(-1);
				cosThetaI = -cosThetaI;
			}

			final double xx = 1 - (1 - cosThetaI * cosThetaI) / (eta * eta);
			if (xx >= 0) {
				final Color materialRefractionColor = objectMaterial.transparency
						.getColor(collision);
				if (materialRefractionColor.getRed() != 0
						|| materialRefractionColor.getGreen() != 0
						|| materialRefractionColor.getBlue() != 0) {
					final Vector4 aux = new Vector4(tNormal);
					aux.scalarMult(Math.sqrt(xx) - cosThetaI / eta);
					final Vector4 refractedDir = new Vector4(v);
					refractedDir.scalarMult(-1 / eta);
					refractedDir.sub(aux);
					final Vector4 aux2 = new Vector4(collisionPoint);
					tNormal.scalarMult(.001f);
					aux2.sub(tNormal);
					final Ray refractedRay = new Ray(aux2, refractedDir);

					final Color refractedColor = shade(refractedRay,
							rayDepth - 1, stack);
					refractedColor.mult(materialRefractionColor);

					intensity = intensity.add(refractedColor);
				}
			}
		}

		return intensity;
	}

	public RayCollisionInfo castRay(final Ray ray, final CustomStack stack) {
		return scene.getTree().getCollision(Double.MAX_VALUE, ray, stack, 0);
	}

    public interface ShadeFunction{
        public Color shade(final Ray ray, final int rayDepth, final CustomStack stack);
    }

}
