package camera3D.generators;

import camera3D.generators.StereoscopicGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

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
 * This precomputes some lookup tables for gamma correction but not for the
 * Dubois matrix calculations. This will have a small memory footprint but will
 * be much slower than the DuboisAnaglyphGenerator64bitLUT implementation.
 * 
 * @author jim
 *
 */

public class DuboisAnaglyphGenerator extends AnaglyphGenerator {

	private AnaglyphMatrix left;
	private AnaglyphMatrix right;
	private float[] removeGammaCorrectionLUT;
	private int[] applyGammaCorrectionLUT;
	private int maxEncodedValue;

	public DuboisAnaglyphGenerator(AnaglyphMatrix left, AnaglyphMatrix right) {
		maxEncodedValue = (int) Math.pow(2, 16);

		this.left = left;
		this.right = right;

		removeGammaCorrectionLUT = preComputeRemoveGammaCorrectionStandardrgbLUT();
		applyGammaCorrectionLUT = preComputeApplyGammaCorrectionStandardrgbLUT(maxEncodedValue);
	}

	public static StereoscopicGenerator createRedCyanGenerator() {
		return new DuboisAnaglyphGenerator(LEFT_DUBOIS_REDCYAN,
				RIGHT_DUBOIS_REDCYAN);
	}

	public static StereoscopicGenerator createMagentaGreenGenerator() {
		return new DuboisAnaglyphGenerator(LEFT_DUBOIS_MAGENTAGREEN,
				RIGHT_DUBOIS_MAGENTAGREEN);
	}

	public static StereoscopicGenerator createAmberBlueGenerator() {
		return new DuboisAnaglyphGenerator(LEFT_DUBOIS_AMBERBLUE,
				RIGHT_DUBOIS_AMBERBLUE);
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		for (int ii = 0; ii < pixels.length; ++ii) {
			float rightRed = removeGammaCorrectionLUT[(pixels[ii] & 0x00FF0000) >> 16];
			float rightGreen = removeGammaCorrectionLUT[(pixels[ii] & 0x0000FF00) >> 8];
			float rightBlue = removeGammaCorrectionLUT[pixels[ii] & 0x000000FF];
			float leftRed = removeGammaCorrectionLUT[(pixelsAlt[ii] & 0x00FF0000) >> 16];
			float leftGreen = removeGammaCorrectionLUT[(pixelsAlt[ii] & 0x0000FF00) >> 8];
			float leftBlue = removeGammaCorrectionLUT[pixelsAlt[ii] & 0x000000FF];

			ColorVector rightAnaglyph = right.rightMult(new ColorVector(
					rightRed, rightGreen, rightBlue));
			ColorVector leftAnaglyph = left.rightMult(new ColorVector(leftRed,
					leftGreen, leftBlue));

			int anaglyphRed = applyGammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.red)
					+ clip(leftAnaglyph.red)))];
			int anaglyphGreen = applyGammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.green)
					+ clip(leftAnaglyph.green)))];
			int anaglyphBlue = applyGammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.blue)
					+ clip(leftAnaglyph.blue)))];

			pixels[ii] = 0xFF000000 | (anaglyphRed << 16)
					| (anaglyphGreen << 8) | anaglyphBlue;
		}
	}
}
