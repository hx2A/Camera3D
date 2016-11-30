package camera3D.generators;

import camera3D.generators.AnaglyphGenerator;

/**
 * 
 * Simple bitmask filter anaglyph algorithm.
 * 
 * This is what you would get if you combined the red channel of one image with
 * the green+blue channel of another image. This is the algorithm the previous
 * RedBlue library used.
 * 
 * @author James Schmitz
 *
 */
public class BitMaskFilterAnaglyphGenerator extends AnaglyphGenerator {

    private int leftFilter;
    private int rightFilter;

    public BitMaskFilterAnaglyphGenerator(int leftFilter, int rightFilter) {
        this.leftFilter = leftFilter;
        this.rightFilter = rightFilter;
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        for (int ii = 0; ii < pixelDest.length; ++ii) {
            pixelDest[ii] = (pixelStorage[0][ii] & rightFilter)
                    | (pixelDest[ii] & leftFilter);
        }
    }
}
