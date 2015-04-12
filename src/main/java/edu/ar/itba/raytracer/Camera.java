package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;

import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.SceneShape;
import edu.ar.itba.raytracer.vector.Vector3;

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

	private final Vector3 forwardVector;
	private final Vector3 heightVector;
	private final Vector3 widthVector;

	private final Vector3[] pixelPoints;

	private final Scene scene;

	public Camera(final Scene scene, final int pictureWidth,
			final int pictureHeight, final double fov, final Transform transform) {
		super(transform);
		this.scene = scene;
		this.pictureWidth = pictureWidth;
		this.pictureHeight = pictureHeight;
		this.distToPixels = calculateDistanceToPixels(fov);
		picture = new Color[pictureHeight][pictureWidth];
		initPicture();
		forwardVector = new Vector3(0, 0, 1).rotate(
				getTransform().getRotation()).scalarMult((double) distToPixels);
		heightVector = new Vector3(0, 1, 0)
				.rotate(getTransform().getRotation());
		widthVector = new Vector3(1, 0, 0).rotate(getTransform().getRotation());

		pixelPoints = new Vector3[pictureHeight * pictureWidth];
		for (int i = 0; i < pictureHeight * pictureWidth; i++) {
			final int x = i % pictureWidth;
			final int y = i / pictureWidth;
			pixelPoints[i] = forwardVector.add(
					heightVector.scalarMult(-y + pictureHeight / 2)).add(
					widthVector.scalarMult(x - pictureWidth / 2));
		}
	}

	public Camera(final Scene scene, final int pictureWidth,
			final int pictureHeight, final double fov) {
		this(scene, pictureWidth, pictureHeight, fov, new Transform());
	}

	private Ray getPrimaryRay(final int x, final int y) {
		final Vector3 position = getTransform().getPosition();
		return new Ray(position,
				pixelPoints[y * pictureWidth + x].sub(position));
	}

	/**
	 * Creates a picture of what the camera is currently seeing.
	 * 
	 * @return a {@link BufferedPicture} with pixels representing what this
	 *         camera is currently seeing.
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
		if (pictureWidth < pictureHeight) {
			halfDim = pictureWidth / 2.0f;
		} else {
			halfDim = pictureHeight / 2.0f;
		}

		return halfDim / (double) Math.tan(fov / 2 * Math.PI / 180);
	}

	public BufferedImage render(final int width, final int height) {
		final int pixels = width * height;
		final int cores = Runtime.getRuntime().availableProcessors();

		final ForkJoinTask<?>[] threads = new ForkJoinTask[cores];
		final Runnable[] runnables = new Runnable[cores];
		for (int i = 0; i < cores; i++) {
			final int startPixel = i * pixels / cores;
			final int endPixel;
			if (i == cores - 1) {
				endPixel = pixels;
			} else {
				endPixel = (i + 1) * pixels / cores;
			}
			runnables[i] = new Runnable() {
				@Override
				public void run() {
					final long start = System.currentTimeMillis();
					for (int i = startPixel; i < endPixel; i++) {
						final int x = i % width;
						final int y = i / width;
						picture[y][x] = shade(getPrimaryRay(x, y), 12);
					}
					System.out.println("TIME THREAD "
							+ (System.currentTimeMillis() - start) + " FOR "
							+ (endPixel - startPixel));
				}
			};
			threads[i] = ForkJoinTask.adapt(runnables[i]);
		}
		final long start = System.currentTimeMillis();
		for (ForkJoinTask<?> fjt : threads) {
			fjt.fork();
		}

		for (ForkJoinTask<?> fjt : threads) {
			fjt.join();
		}
		System.out.println(System.currentTimeMillis() - start);

		return takePicture();
	}

	private Color shade(final Ray ray, final int rayDepth) {
		final RayCollisionInfo collision = castRay(ray);

		if (!collision.collisionDetected()) {
			return Color.DEFAULT_COLOR;
		}

		final SceneShape obj = collision.getObj();

		final Vector3 collisionPoint = collision.getCollisionPoint();
		final Vector3 normal = obj.normal(collisionPoint).normalize();
		final Vector3 collisionPointPlusDelta = collisionPoint.add(normal
				.scalarMult(.001f));

		// Invert the ray direction to get the view versor. The direction is
		// already normalized, so there is no need to normalize again.
		final Vector3 v = ray.getDir().scalarMult(-1);

		final Material objectMaterial = collision.getObj().getProperties()
				.getMaterial();
		final Color objectColor = objectMaterial.color;
		final double ka = objectMaterial.ka;
		final double kd = objectMaterial.kd;
		final double ks = objectMaterial.ks;
		final double shininess = objectMaterial.shininess;

		Color intensity = scene.getAmbientLight().scalarMult(ka)
				.mult(objectColor);

		for (final Light light : scene.getLights()) {
			// Move the from point a little in the direction of the normal
			// vector, to avoid rounding problems and intersecting with the same
			// object we're starting from.
			if (!scene.isIlluminati(collisionPointPlusDelta, light)) {
				continue;
			}

			final Vector3 lightVersor = light.getTransform().getPosition()
					.sub(collisionPoint).normalize();
			final double ln = lightVersor.dot(normal);

			if (ln > 0) {
				final Color lightColor = light.getProperties().getColor();

				final Color diffuse = lightColor.scalarMult(ln).scalarMult(kd)
						.mult(objectColor);

				// final Color diffuse = new Color(diffuseRed, diffuseGreen,
				// diffuseBlue);
				intensity = intensity.add(diffuse);

				final Vector3 r = normal.scalarMult(2 * ln).sub(lightVersor);
				final double rv = r.dot(v);
				if (rv > 0) {
					final Color specular = lightColor.scalarMult(Math.pow(rv,
							shininess) * ks);
					intensity = intensity.add(specular);
				}
			}
		}

		if (rayDepth > 0) {
			final double nv = normal.dot(v);
			final Vector3 reflectedDir = normal.scalarMult(2 * nv).sub(v);
			final Ray reflectedRay = new Ray(collisionPointPlusDelta,
					reflectedDir);

			final Color reflectedColor = shade(reflectedRay, rayDepth - 1)
					.scalarMult(shininess / Material.MAX_SHININESS);

			intensity = intensity.add(reflectedColor);

			Vector3 tNormal = normal;
			double cosThetaI = nv;
			double eta = objectMaterial.refractionIndex;
			if (nv < 0) {
				eta = 1 / eta;
				tNormal = tNormal.scalarMult(-1);
				cosThetaI = -cosThetaI;
			}
			
			final double xx = 1 - (1 - cosThetaI * cosThetaI) / (eta * eta);
			if (xx >= 0) {
				final Vector3 refractedDir = v.scalarMult(-1 / eta).sub(
						tNormal.scalarMult(Math.sqrt(xx) - cosThetaI / eta));
				final Ray refractedRay = new Ray(collisionPoint.sub(tNormal
						.scalarMult(.001f)), refractedDir);

				final Color refractedColor = shade(refractedRay, rayDepth - 1)
						.scalarMult(objectMaterial.transparency);

				intensity = intensity.add(refractedColor);
			}
		}

		return intensity;
	}

	private RayCollisionInfo castRay(final Vector3 from, final Vector3 through) {
		return castRay(new Ray(from, through.sub(from)));
	}

	private RayCollisionInfo castRay(final Ray ray) {
		double minDistance = -1;
		SceneShape intersected = null;
		for (final SceneShape obj : scene.getObjects()) {
			final double distance = obj.intersect(ray);
			if (distance != -1 && (distance < minDistance || minDistance == -1)) {
				minDistance = distance;
				intersected = obj;
			}
		}

		if (intersected == null) {
			return RayCollisionInfo.noCollision(ray);
		}

		return new RayCollisionInfo(intersected, ray, minDistance);
	}

}
