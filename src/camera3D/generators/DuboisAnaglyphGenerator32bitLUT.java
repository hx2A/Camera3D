package camera3D.generators;

import camera3D.generators.StereoscopicGenerator;
import camera3D.generators.util.AnaglyphMatrix;

/**
 * Optimized implementation of the Dubois Anaglyph algorithm.
 * 
 * More information can be found here:
 * 
 * http://www.site.uottawa.ca/~edubois/anaglyph/
 * 
 * particularly:
 * 
 * http://www.site.uottawa.ca/~edubois/anaglyph/LeastSquaresHowToPhotoshop.pdf
 * http://www.site.uottawa.ca/~edubois/icassp01/anaglyphdubois.pdf
 * 
 * This algorithm is actually similar to what's being done in the
 * MatrixAnaglyphGenerator except the calculations are done in linear RGB space.
 * 
 * This works by precomputing a set of lookup tables for every possible color.
 * There is a small up-front cost but after that the performance speedup of the
 * generateCompositeFrame method is about 300x.
 * 
 * The lookup tables will use about 128 MB of RAM. The lower memory and speed
 * comes at the cost of lower calculation accuracy for desaturated colors.
 * 
 * Without these optimizations it would not be possible to use this algorithm in
 * real time.
 * 
 * @author jim
 *
 */

public class DuboisAnaglyphGenerator32bitLUT extends AnaglyphGenerator {

	private long[] leftLUT;
	private long[] rightLUT;
	private float[] removeGammaCorrectionLUT;
	private int[] applyGammaCorrectionLUT;
	private final int MAX_ENCODED_VALUE = (int) Math.pow(2, 9);

	public DuboisAnaglyphGenerator32bitLUT(AnaglyphMatrix left,
			AnaglyphMatrix right) {

		System.out.println("precomputing lookup tables...");

		removeGammaCorrectionLUT = preComputeRemoveGammaCorrectionStandardrgbLUT();

		leftLUT = preComputeDuboisLUT(left, removeGammaCorrectionLUT,
				MAX_ENCODED_VALUE, 10);
		rightLUT = preComputeDuboisLUT(right, removeGammaCorrectionLUT,
				MAX_ENCODED_VALUE, 10);

		applyGammaCorrectionLUT = preComputeApplyGammaCorrectionStandardrgbLUT(MAX_ENCODED_VALUE);

		System.out.println("done!");
	}

	public static StereoscopicGenerator createRedCyanGenerator() {
		return new DuboisAnaglyphGenerator32bitLUT(LEFT_DUBOIS_REDCYAN,
				RIGHT_DUBOIS_REDCYAN);
	}

	public static StereoscopicGenerator createMagentaGreenGenerator() {
		return new DuboisAnaglyphGenerator32bitLUT(LEFT_DUBOIS_MAGENTAGREEN,
				RIGHT_DUBOIS_MAGENTAGREEN);
	}

	public static StereoscopicGenerator createAmberBlueGenerator() {
		return new DuboisAnaglyphGenerator32bitLUT(LEFT_DUBOIS_AMBERBLUE,
				RIGHT_DUBOIS_AMBERBLUE);
	}

	public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
		long encodedColor;

		for (int ii = 0; ii < pixelDest.length; ++ii) {
			/*
			 * This addition is a SIMD operation, adding the R, G, and B color
			 * values that are embedded in the longs in leftLUT and rightLUT.
			 */
			encodedColor = leftLUT[pixelDest[ii] & 0x00FFFFFF]
					+ rightLUT[pixelStorage[0][ii] & 0x00FFFFFF];

			/*
			 * For each color, first check if the rollover bit is set. If so,
			 * that color is fully saturated. Otherwise, extract the color from
			 * encodedColor, apply gamma correction, and build the pixel color.
			 */
			if (0 < (encodedColor & 0x20000000)) {
				pixelDest[ii] = 0xFFFF0000;
			} else {
				pixelDest[ii] = 0xFF000000 | (applyGammaCorrectionLUT[(int) ((encodedColor & 0x1FF00000) >> 20)] << 16);
			}

			if (0 < (encodedColor & 0x80000)) {
				pixelDest[ii] |= 0x0000FF00;
			} else {
				pixelDest[ii] |= (applyGammaCorrectionLUT[(int) ((encodedColor & 0x7FC00) >> 10)] << 8);
			}

			if (0 < (encodedColor & 0x200)) {
				pixelDest[ii] |= 0x000000FF;
			} else {
				pixelDest[ii] |= (applyGammaCorrectionLUT[(int) (encodedColor & 0x1FF)]);
			}
		}
	}
}
