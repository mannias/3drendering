package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.net.PasswordAuthentication;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.samplers.Sampler;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.texture.Texture;
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

	private final int aaSamples;
	private final int rayDepth;
    private final int samplesPerPixel;

	private Vector4 forwardVector;

	private Vector4 u;
	private Vector4 v;

	private final Vector4[] pixelPoints;

	private final Scene scene;

    private final Sampler sampler;
    
    private int iteration;
    
    private Vector4 up;

	public Camera(final Scene scene, final int pictureWidth,
			final int pictureHeight, final double fov, final Vector4 position,
			final Vector4 lookAt, final Vector4 up, final Matrix44 transform,
			final int aaSamples, final int rayDepth) {
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
		
		this.up = up;

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

		this.aaSamples = aaSamples;
		this.rayDepth = rayDepth;

        //TODO: Wired
        this.samplesPerPixel = 400;
        sampler = new Sampler(samplesPerPixel, pictureWidth * pictureHeight);
//        sampler.generateSamples();
//        sampler.sampleHemisphere(1);
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
		iteration++;
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
                    if(false) {
                        trace(startPixel, pixelsPerTask, pixels, width, aaSamples,(x,y,z) -> shade(x,y,z));
                    }else{
                        trace(startPixel, pixelsPerTask, pixels, width, samplesPerPixel, (x,y,z) -> pathShade(x, y, z));
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

		return takePicture();
	}
	
	private final AtomicInteger done = new AtomicInteger();

    private void trace(AtomicInteger startPixel, int pixelsPerTask, int pixels, int width, int samples,
                       ShadeFunction func){
        final CustomStack stack = new CustomStack();
        int currentStart;
        int samplesSqrt = (int)Math.sqrt(samples);
        
        while ((currentStart = startPixel.getAndAdd(pixelsPerTask)) < pixels) {
            final int endPixel = (currentStart + pixelsPerTask) >= pixels ? pixels
                    : currentStart + pixelsPerTask;
            for (int i = currentStart; i < endPixel; i++) {
                final int x = i % width;
                final int y = i / width;
                double pixelRed = 0;
                double pixelGreen = 0;
                double pixelBlue = 0;
                int count = 0;
                final long start = System.nanoTime();
                for (int s = 0 ; s < samplesPerPixel; s++) {
					for (int p = 0; p < 1; p++) {
						for (int q = 0; q < 1; q++) {
							final double ppx = x - .5 * pictureWidth
									+ (q + Math.random()) / 1;
							final double ppy = -y + .5 * pictureHeight
									+ (p + Math.random()) / 1;
							stack.reset();
							if (x == 450 && y == 200) {
								System.out.println("STAHP");
							}
							Color c = func.shade(getPrimaryRay(ppx, ppy),
									rayDepth, stack);
							if (Double.isNaN(c.getRed())
									|| Double.isNaN(c.getBlue())
									|| Double.isNaN(c.getGreen())) {
								System.out.println("SASA");
							}
							pixelRed += c.getRed();// * Math.PI;
							pixelGreen += c.getGreen();//* Math.PI;
							pixelBlue += c.getBlue();//* Math.PI;
							if (c.getRed() + c.getGreen() + c.getBlue() > 0) {
								count++;
							}
						}
					}
                }
//                System.out.println("AVG LIGHT =" + (count * 1.0 / samples));
                final long time = System.nanoTime() - start;
                
                final int pixelsDone = done.incrementAndGet();
                
                final long remaining = (pixels - pixelsDone) * time;
                
                
//                System.out.println(pixelsDone * 1.0/ pixels + " completed. About " + (remaining / 1e9) + " seconds remaining");

                double n2 = samples;
                 if (x == 450 && y == 200) {
                 picture[y][x] = new Color(1, 1, 1);
                 continue;
                 }
                 
//                 final double r = (picture[y][x].getRed() * iteration + pixelRed)/(iteration+1);
//                 final double g = (picture[y][x].getGreen() * iteration + pixelGreen)/(iteration+1);
//                 final double b = (picture[y][x].getBlue() * iteration + pixelBlue) /(iteration+1);
//
//                 picture[y][x] = new Color(r,g,b);
                 
//                 final int iplus = iteration + 1;
                 
//                 picture[y][x] = picture[y][x].add(new Color(pixelRed, pixelGreen, pixelBlue));
                 
//                 final double max = Math.max(Math.max(r,g),b);
                 
//                 picture[y][x] = new Color(r/iplus,g/iplus,b/iplus);
                picture[y][x] = new Color(pixelRed / Math.max(count, 1), pixelGreen
                        / Math.max(count, 1), pixelBlue / Math.max(count, 1));
                
//                picture[y][x] = new Color(pixelRed / samplesPerPixel, pixelGreen
//                        / samplesPerPixel, pixelBlue / samplesPerPixel);
            }
        }
    }

    private Color pathShade(final Ray ray, final int rayDepth,
                            final CustomStack stack){

        final RayCollisionInfo collision = castRay(ray, stack);
        if(collision == null){
            return scene.getAmbientLight();
        }

        Color intensity = new Color(scene.getAmbientLight());

        final Vector4 collisionPoint = collision.getWorldCollisionPoint();
        final Material objectMaterial = collision.getObj().material;
        final Vector4 deltaNormal = new Vector4(collision.normal);
        deltaNormal.scalarMult(.001f);
        final Vector4 collisionPointPlusDelta = new Vector4(collisionPoint);
        collisionPointPlusDelta.add(deltaNormal);

        if(objectMaterial.light != null){
        	final Color in = objectMaterial.light.getIntensity(null);
        	if(Double.isNaN(in.getRed()) || Double.isNaN(in.getBlue()) || Double.isNaN(in.getGreen())) {
				System.out.println("SASA");
			}
            return objectMaterial.kd.getColor(collision);//.scalarMult(17);
        }

        for (final Light light : scene.getLights()) {
            if (!scene.isIlluminati(collisionPointPlusDelta, light, stack)) {
                continue;
            }
            final Vector4 lightVersor = light.getDirection(collisionPoint);
            final double ln = lightVersor.dot(collision.normal);
            if (ln > 0) {
                final Color lightColor = light.getIntensity(collisionPoint);

                final Color diffuse = new Color(lightColor);
                diffuse.scalarMult(ln);
                diffuse.mult(objectMaterial.kd.getColor(collision));

                intensity = intensity.add(diffuse);
                final Vector4 r = new Vector4(collision.normal);
                r.scalarMult(2 * ln);
                r.sub(lightVersor);
                final double rv = r.dot(v);
                if (rv > 0) {
                    final Color specular = new Color(lightColor);

                    final Color ksAux = new Color(objectMaterial.ks.getColor(collision));
                    ksAux.scalarMult(Math.pow(rv, objectMaterial.shininess));

                    specular.mult(ksAux);
                    intensity = intensity.add(specular);
                }
            }
        }

        if(rayDepth <= 0){
        	if(Double.isNaN(intensity.getRed()) || Double.isNaN(intensity.getBlue()) || Double.isNaN(intensity.getGreen())) {
				System.out.println("SASA");
			}
            return intensity;
        }
        
        

        if(objectMaterial.shininess == 0){
            //THIS IS DIFFUSE

//            Vector4 w = new Vector4(collision.normal);
//            Vector4 v = new Vector3(0.0034, 1, 0.0071).cross(w);
//            v.normalize();
//            Vector4 u = v.cross(w);
//
//            Vector4 hem = sampler.getSample();
//            Vector4 reflectedDir = u.scalarMult(hem.x).add(v.scalarMult(hem.y)).add(w.scalarMult(hem.z));
//            reflectedDir.normalize();
        	
        	final double r1 = 2 * Math.PI * Math.random();
        	final double r2 = Math.random();
        	final double r2s = Math.sqrt(r2);
        	
        	final Color f = objectMaterial.kd.getColor(collision);
        
			final Vector4 w = new Vector4(collision.normal);
			final Vector4 v = new Vector4(.0034, 1, .0071, 0).cross(w);
			v.normalize();
			final Vector4 u = v.cross(w);
			
//			final Vector4 d = new Vector4(u.scalarMult(Math.cos(r1))
//					.scalarMult(r2s)
//					.add(v.scalarMult(Math.sin(r1)).scalarMult(r2s))
//					.add(w.scalarMult(Math.sqrt(1 - r2))));        	
//        	d.normalize();
			
			final Vector4 sample = sampler.getSample();
			
			final Vector4 wi = u.scalarMult(sample.x).add(v.scalarMult(sample.y)).add(w.scalarMult(sample.z));
//			final Vector4 wi = new Vector4(Math.random() - .5,
//					Math.random() - .5, Math.random() - .5, 0);
			wi.normalize();
			
			if (wi.dot(collision.normal) < 0) {
				System.out.println("PROBLEM!");
			}
			
//			final Vector4 wi = new Vector4(up);
			
			final double pdf = collision.normal.dot(wi);// Math.PI;
			final Color color = new Color(objectMaterial.kd.getColor(collision));//.scalarMult(1.0 / Math.PI);

			final double ndotwi = collision.normal.dot(new Vector4(wi).scalarMult(-1));
			
			final Color newColor = pathShade(new Ray(collisionPointPlusDelta, wi), rayDepth-1, stack); 
			
			final Color ret = color.mult(newColor);
			
			if(Double.isNaN(ret.getRed()) || Double.isNaN(ret.getBlue()) || Double.isNaN(ret.getGreen())) {
				System.out.println("SASA");
			}
			
			return ret;
        	
//        	return f.mult(pathShade(new Ray(collisionPointPlusDelta, d), rayDepth - 1, stack));

        	
//        	final double rx = Math.random() - .5;
//        	final double ry = Math.random() - .5;
//        	final double rz = Math.random() - .5;
//        	
//        	final Vector4 reflectedDir = new Vector4(rx,ry,rz, 0);
//        	reflectedDir.normalize();
//        	
//        	if (reflectedDir.dot(collision.normal) < 0) {
//        		reflectedDir.scalarMult(-1);
//        	}
//        	
//        	final Ray reflectedRay = new Ray(collisionPointPlusDelta,
//        			reflectedDir);
//        	
//            double pdf = collision.normal.dot(reflectedDir) * 0.3183098861837906715;
//            double phi = collision.normal.dot(reflectedDir);
//
//            Color color = new Color(objectMaterial.kd.getColor(collision)).scalarMult(0.3183098861837906715);
//
//            Color newColor = pathShade(reflectedRay,rayDepth-1,stack);
//
//            return intensity.add(color.mult(newColor).scalarMult(phi / pdf));
        }else{
            //THIS IS REFLECTIVE
            Vector4 wo = ray.dir.neg();
            double ndotwo = collision.normal.dot(wo);
            Vector4 reflectedDir = wo.neg().add(new Vector4(collision.normal).scalarMult(2 * ndotwo));
            double pdf = Math.abs(collision.normal.dot(reflectedDir));
            double phi = collision.normal.dot(reflectedDir);
            
            Color color = new Color(objectMaterial.ks.getColor(collision));

            final Ray reflectedRay = new Ray(collisionPointPlusDelta,
                    reflectedDir);

            Color newColor = pathShade(reflectedRay,rayDepth-1,stack);

            return intensity.add(color.mult(newColor).scalarMult(phi/pdf));
        }

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
            return objectMaterial.light.getIntensity(null);
        }

		for (final Light light : scene.getLights()) {
			// Move the from point a little in the direction of the normal
			// vector, to avoid rounding problems and intersecting with the same
			// object we're starting from.
			if (!scene.isIlluminati(collisionPointPlusDelta, light, stack)) {
				continue;
			}

			final Vector4 lightVersor = light.getDirection(collisionPoint);

			// final Vector4 lightVersor = new Vector4(light.getTransform()
			// .getPosition());
			// lightVersor.sub(collisionPoint);
			// lightVersor.normalize();

			final double ln = lightVersor.dot(normal);

			if (ln > 0) {
				final Color lightColor = light.getIntensity(collisionPoint);

				final Color diffuse = new Color(lightColor);
				diffuse.scalarMult(ln);
				diffuse.mult(kd);

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
