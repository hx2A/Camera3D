package camera3D.generators;

/**
 * 
 * Split frame generator. This will pack the right and left component images
 * into one composite image. The two components will be positioned vertically or
 * horizontally. Also, Interlaced component images.
 * 
 * @author James Schmitz
 *
 */
public class SplitFrameGenerator extends StereoscopicGenerator {

    public static final int SIDE_BY_SIDE_HALF_WIDTH = 0;
    public static final int OVER_UNDER_HALF_HEIGHT = 1;
    public static final int SIDE_BY_SIDE = 2;
    public static final int OVER_UNDER = 3;
    public static final int INTERLACED = 4;

    private int width;
    private int height;
    private int pixelDensity;
    private int technique;

    public SplitFrameGenerator(int width, int height, int pixelDensity, int technique) {
        this.width = width;
        this.height = height;
        this.pixelDensity = pixelDensity;
        this.technique = technique;
    }

    public static SplitFrameGenerator createSideBySideHalfWidthGenerator(
            int width, int height, int pixelDensity) {
        return new SplitFrameGenerator(width, height, pixelDensity, SIDE_BY_SIDE_HALF_WIDTH);
    }

    public static SplitFrameGenerator createSideBySideGenerator(int width,
            int height, int pixelDensity) {
        return new SplitFrameGenerator(width, height, pixelDensity, SIDE_BY_SIDE);
    }

    public static SplitFrameGenerator createOverUnderHalfHeightGenerator(
            int width, int height, int pixelDensity) {
        return new SplitFrameGenerator(width, height, pixelDensity, OVER_UNDER_HALF_HEIGHT);
    }

    public static SplitFrameGenerator createOverUnderGenerator(int width,
            int height, int pixelDensity) {
        return new SplitFrameGenerator(width, height, pixelDensity, OVER_UNDER);
    }

    public static SplitFrameGenerator createInterlacedGenerator(int width,
            int height, int pixelDensity) {
        return new SplitFrameGenerator(width, height, pixelDensity, INTERLACED);
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        if (technique == SIDE_BY_SIDE_HALF_WIDTH) {
            for (int y = 0; y < height * pixelDensity; y++) {
                // left
                int offset = y * width * pixelDensity;
                for (int x = 0; x < width * pixelDensity / 2; x++) {
                    pixelDest[offset + x] = pixelDest[offset + x * 2];
                }
                // right
                int offset2 = offset + width * pixelDensity / 2;
                for (int x = 0; x < width * pixelDensity / 2; x++) {
                    pixelDest[offset2 + x] = pixelStorage[0][offset + x * 2];
                }
            }
        } else if (technique == OVER_UNDER_HALF_HEIGHT) {
            // over
            for (int y = 0; y < height * pixelDensity / 2; ++y) {
                System.arraycopy(pixelDest, 2 * y * width * pixelDensity, pixelDest,
                        y * width * pixelDensity, width * pixelDensity);
            }
            // under
            for (int y = 0; y < height * pixelDensity / 2; ++y) {
                System.arraycopy(pixelStorage[0], 2 * y * width * pixelDensity, pixelDest,
                        (y + height * pixelDensity / 2) * width * pixelDensity, width * pixelDensity);
            }
        } else if (technique == SIDE_BY_SIDE) {
            for (int y = 0; y < height * pixelDensity; ++y) {
                System.arraycopy(pixelDest, y * width * pixelDensity + (width * pixelDensity / 4), pixelDest,
                        y * width * pixelDensity, width * pixelDensity / 2);
                System.arraycopy(pixelStorage[0], y * width * pixelDensity + (width * pixelDensity / 4),
                        pixelDest, y * width * pixelDensity + width * pixelDensity / 2, width * pixelDensity / 2);
            }
        } else if (technique == OVER_UNDER) {
            // over
            for (int y = 0; y < height * pixelDensity / 2; ++y) {
                System.arraycopy(pixelDest, (y + height * pixelDensity / 4) * width * pixelDensity,
                        pixelDest, y * width * pixelDensity, width * pixelDensity);
            }
            // under
            for (int y = 0; y < height * pixelDensity / 2; ++y) {
                System.arraycopy(pixelStorage[0], (y + height * pixelDensity / 4) * width * pixelDensity,
                        pixelDest, (y + height * pixelDensity / 2) * width * pixelDensity, width * pixelDensity);
            }
        } else if (technique == INTERLACED) {
            for (int y = 1; y < height * pixelDensity; y += 2) {
                System.arraycopy(pixelStorage[0], y * width * pixelDensity, pixelDest, y
                        * width * pixelDensity, width * pixelDensity);
            }
        }
    }
}
