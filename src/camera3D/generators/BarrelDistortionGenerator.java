package camera3D.generators;

/**
 * Optimized implementation of the barrel distortion algorithm.
 * 
 * Builds a lookup table to map pixels in sketch to the distorted view that will
 * be countered by the lenses.
 * 
 * Defaults to settings for an Oculus Rift's lenses.
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

public class BarrelDistortionGenerator extends StereoscopicGenerator {

    private int width;
    private int height;

    private float pow2;
    private float pow4;
    private float zoom;

    private int[] arrayIndex;
    private int[] pixelMapping;

    public BarrelDistortionGenerator(int width, int height) {
        this.width = width;
        this.height = height;

        this.pow2 = 0.22f;
        this.pow4 = 0.24f;
        this.zoom = 1;

        calculatePixelMaps(pow2, pow4, zoom);
    }

    public BarrelDistortionGenerator setZoom(float zoom) {
        this.zoom = zoom;

        calculatePixelMaps(pow2, pow4, zoom);

        return this;
    }

    public BarrelDistortionGenerator setBarrelDistortionCoefficients(
            float pow2, float pow4) {
        this.pow2 = pow2;
        this.pow4 = pow4;

        calculatePixelMaps(pow2, pow4, zoom);

        return this;
    }

    @Override
    public BarrelDistortionGenerator setDivergence(float divergence) {
        return (BarrelDistortionGenerator) super.setDivergence(divergence);
    }

    @Override
    public BarrelDistortionGenerator swapLeftRight(boolean swap) {
        return (BarrelDistortionGenerator) super.swapLeftRight(swap);
    }

    @Override
    public BarrelDistortionGenerator useSymmetricFrustum() {
        return (BarrelDistortionGenerator) super.useSymmetricFrustum();
    }

    private void calculatePixelMaps(float pow2, float pow4, float zoom) {
        arrayIndex = new int[width * height];
        pixelMapping = new int[width * height];

        int xCenter = width / 2;
        int quarterWidth = width / 4;
        int yCenter = height / 2;
        double rMax2inv = 1 / (Math.pow(xCenter / 2, 2) + Math.pow(yCenter, 2));

        for (int x = 0; x < width; ++x) {
            double xOffset = x - xCenter;
            for (int y = 0; y < height; ++y) {
                double yOffset = y - yCenter;
                double r2 = xOffset * xOffset + yOffset * yOffset;
                double sr2 = r2 * rMax2inv;
                double distortion = (1 + pow2 * sr2 + pow4 * sr2 * sr2) / zoom;
                double xPrime = distortion * xOffset + xCenter;
                double yPrime = distortion * yOffset + yCenter;

                if (xPrime < xCenter - quarterWidth
                        || xPrime >= xCenter + quarterWidth || yPrime < 0
                        || yPrime >= height) {
                    // black void
                    if (x >= quarterWidth && x < xCenter)
                        arrayIndex[y * width + x - quarterWidth] = -1;
                    if (x + quarterWidth < width)
                        arrayIndex[y * width + x + quarterWidth] = -1;
                } else {
                    // right
                    arrayIndex[y * width + x + quarterWidth] = 0;
                    pixelMapping[y * width + x + quarterWidth] = ((int) Math
                            .floor(yPrime) * width)
                            + ((int) Math.floor(xPrime));
                    // left
                    if (x < xCenter + quarterWidth) {
                        arrayIndex[y * width + x - quarterWidth] = 1;
                        pixelMapping[y * width + x - quarterWidth] = ((int) Math
                                .floor(yPrime) * width)
                                + ((int) Math.floor(xPrime));
                    }
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
