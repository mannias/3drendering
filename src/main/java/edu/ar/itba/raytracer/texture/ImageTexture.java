package edu.ar.itba.raytracer.texture;

import java.awt.image.BufferedImage;

import edu.ar.itba.raytracer.RayCollisionInfo;
import edu.ar.itba.raytracer.properties.Color;

public class ImageTexture implements Texture {

	private final int hres;
	private final int vres;
	private final BufferedImage image;
	private final TextureMapping mapping;

	public ImageTexture(final BufferedImage image, final TextureMapping mapping) {
		this.image = image;
		hres = image.getWidth();
		vres = image.getHeight();
		this.mapping = mapping;
	}

	public ImageTexture(final BufferedImage image) {
		this(image, null);
	}

	@Override
	public Color getColor(RayCollisionInfo collisionInfo) {
		int[] point;

		if (mapping != null) {
			point = mapping.getPixelCoordinates(
					collisionInfo.getLocalCollisionPoint(), hres, vres);
		} else {
			point = new int[] { (int) (collisionInfo.u * (hres - 1)),
					(int) (collisionInfo.v * (vres - 1)) };
		}

		int rgb = image.getRGB(point[0], point[1]);

		return new Color(((rgb >> 16) & 0x0FF) * 1.0 / 255,
				((rgb >> 8) & 0x0FF) * 1.0 / 255, (rgb & 0x0FF) * 1.0 / 255);
	}

}
