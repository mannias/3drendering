package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ar.itba.raytracer.light.Light;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.shape.CustomStack;
import edu.ar.itba.raytracer.vector.Matrix44;
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

	private Vector4 forwardVector;

	private Vector4 u;
	private Vector4 v;

	private final Vector4[] pixelPoints;

	private final Scene scene;

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
		return this.render(pictureWidth, pictureHeight);
	}

	public BufferedImage render(final int width, final int height) {
		final int pixels = width * height;
		final int cores = Runtime.getRuntime().availableProcessors();

		final int pixelsPerTask = 32 * 32;
		// final int pixelsPerTask = pixels;

		final ForkJoinTask<?>[] threads = new ForkJoinTask[cores];
		final Runnable[] runnables = new Runnable[cores];
		final AtomicInteger startPixel = new AtomicInteger();
		final int aaSamplesSqrt = (int) Math.sqrt(aaSamples);
		for (int i = 0; i < cores; i++) {
			runnables[i] = new Runnable() {
				@Override
				public void run() {
					final CustomStack stack = new CustomStack();
					final long start = System.nanoTime();
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
							for (int p = 0; p < aaSamplesSqrt; p++) {
								for (int q = 0; q < aaSamplesSqrt; q++) {
									final double ppx = x - .5 * pictureWidth
									// + (q + Math.random())
											+ (q + .5) / aaSamplesSqrt;
									final double ppy = -y + .5 * pictureHeight
									// + (p + Math.random())
											+ (p + .5) / aaSamplesSqrt;
									stack.reset();
									if (x == 321 && y == 221) {
										System.out.println("LL");
									}
									Color c = shade(getPrimaryRay(ppx, ppy),
											rayDepth, stack);
									pixelRed += c.getRed();
									pixelGreen += c.getGreen();
									pixelBlue += c.getBlue();
								}
							}

							double n2 = aaSamples;
							// if (x == 320 && y == 221) {
							// picture[y][x] = new Color(1, 1, 1);
							// continue;
							// }

							picture[y][x] = new Color(pixelRed / n2, pixelGreen
									/ n2, pixelBlue / n2);
						}
					}
					System.out.println("TIME THREAD "
							+ ((System.nanoTime() - start) / 1000000));
				}
			};
			threads[i] = ForkJoinTask.adapt(runnables[i]);
		}
		final ForkJoinPool fjp = new ForkJoinPool(Runtime.getRuntime()
				.availableProcessors());
		final long start = System.nanoTime();
		for (ForkJoinTask<?> fjt : threads) {
			fjp.execute(fjt);
			// fjt.fork();
		}

		for (ForkJoinTask<?> fjt : threads) {
			fjt.join();
		}
		System.out.println((System.nanoTime() - start) / 1000000);

		// System.out.println(String.format("SPHERE INTERSECTIONS / CALLS %d/%d",
		// Sphere.intersections.get(), Sphere.calls.get()));
		// System.out.println(String.format("RATIO %f",
		// (double) Sphere.intersections.get() / Sphere.calls.get()));
		// System.out.println(String.format("PLANE INTERSECTIONS / CALLS %d/%d",
		// Plane.intersections.get(), Plane.calls.get()));
		// System.out.println(String.format("RATIO %f",
		// (double) Plane.intersections.get() / Plane.calls.get()));

		return takePicture();
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
				final Color lightColor = light.color;

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

}
