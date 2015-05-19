package edu.ar.itba.raytracer.texture;

import edu.ar.itba.raytracer.vector.Vector4;

public class SphericalTextureMapping implements TextureMapping {

	@Override
	public int[] getPixelCoordinates(Vector4 localHitPoint, int hres, int vres) {
		final double theta = Math.acos(localHitPoint.y);
		double phi = Math.atan2(localHitPoint.x, localHitPoint.z);

		if (phi < 0) {
			phi += 2 * Math.PI;
		}

		final double u = phi / 2 / Math.PI;
		final double v = 1 - theta / Math.PI;

		final int column = (int) ((hres - 1) * u);
		final int row = vres - 1 - (int) ((vres - 1) * v);

		return new int[] { column, row };

	}

}
