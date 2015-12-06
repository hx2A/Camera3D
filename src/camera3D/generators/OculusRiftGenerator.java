package camera3D.generators;

import camera3D.generators.StereoscopicGenerator;

/**
 * Optimized implementation of the Oculus Rift's barrel distortion algorithm.
 * 
 * Builds a lookup table to map pixels in sketch to the distorted view that will
 * be countered by the Oculus Rift's lenses.
 * 
 * Much thanks to this guy for explaining barrel distortion:
 * 
 * https://www.youtube.com/watch?v=B7qrgrrHry0
 * 
 * And to this guy for pointing out that the radius (r) must be normalized:
 * 
 * http://stackoverflow.com/questions/28130618/what-ist-the-correct-oculus-rift-
 * barrel-distortion-radius-function
 * 
 * @author James Schmitz
 *
 */

public class OculusRiftGenerator extends StereoscopicGenerator {

	private int[] arrayIndex;
	private int[] pixelMapping;

	public OculusRiftGenerator(int width, int height) {
		arrayIndex = new int[width * height];
		pixelMapping = new int[width * height];

		int xCenter = width / 2;
		int yCenter = height / 2;
		double rMax = Math.sqrt(Math.pow(xCenter, 2) + Math.pow(yCenter, 2));

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				double r = Math.sqrt(Math.pow(x - xCenter, 2)
						+ Math.pow(y - yCenter, 2));
				double theta = Math.atan2((y - yCenter), (x - xCenter));
				double rNorm = r / rMax;
				double rPrime = r
						* (1 + 0.22 * Math.pow(rNorm, 2) + 0.24 * Math.pow(
								rNorm, 4));
				double xPrime = rPrime * Math.cos(theta) + xCenter;
				double yPrime = rPrime * Math.sin(theta) + yCenter;

				if (xPrime < xCenter - width / 4
						|| xPrime >= xCenter + width / 4 || yPrime < 0
						|| yPrime >= height) {
					// black void
					if (x >= width / 4 && x < xCenter)
						arrayIndex[y * width + x - width / 4] = -1;
					if (x + width / 4 < width)
						arrayIndex[y * width + x + width / 4] = -1;
				} else {
					// left
					arrayIndex[y * width + x - width / 4] = 0;
					pixelMapping[y * width + x - width / 4] = ((int) Math
							.floor(yPrime) * width)
							+ ((int) Math.floor(xPrime));
					// right
					arrayIndex[y * width + x + width / 4] = 1;
					pixelMapping[y * width + x + width / 4] = ((int) Math
							.floor(yPrime) * width)
							+ ((int) Math.floor(xPrime));
				}
			}
		}
	}

	public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
		for (int i = 0; i < pixelDest.length; ++i) {
			if (arrayIndex[i] >= 0)
				pixelDest[i] = pixelStorage[arrayIndex[i]][pixelMapping[i]];
			else
				pixelDest[i] = 0;
		}
	}
}
