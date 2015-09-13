package camera3D.generators;

import camera3D.generators.AnaglyphGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class DuboisAnaglyphGenerator extends AnaglyphGenerator {

	private AnaglyphMatrix left;
	private AnaglyphMatrix right;
	private float[] removeGammaCorrectionLUT;
	private int[] gammaCorrectionLUT;
	private int maxEncodedValue;

	public DuboisAnaglyphGenerator(AnaglyphMatrix left, AnaglyphMatrix right) {
		maxEncodedValue = (int) Math.pow(2, 16);

		this.left = left;
		this.right = right;

		removeGammaCorrectionLUT = makeLUTremoveGammaCorrectionStandardRGB();
		gammaCorrectionLUT = makeLUTapplyGammaCorrectionStandardRGB(maxEncodedValue);
	}

	public static AnaglyphGenerator createRedCyanGenerator() {
		return new DuboisAnaglyphGenerator(
				AnaglyphConstants.LEFT_DUBOIS_REDCYAN,
				AnaglyphConstants.RIGHT_DUBOIS_REDCYAN);
	}

	public static AnaglyphGenerator createMagentaGreenGenerator() {
		return new DuboisAnaglyphGenerator(
				AnaglyphConstants.LEFT_DUBOIS_MAGENTAGREEN,
				AnaglyphConstants.RIGHT_DUBOIS_MAGENTAGREEN);
	}

	public static AnaglyphGenerator createAmberBlueGenerator() {
		return new DuboisAnaglyphGenerator(
				AnaglyphConstants.LEFT_DUBOIS_AMBERBLUE,
				AnaglyphConstants.RIGHT_DUBOIS_AMBERBLUE);
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

			int anaglyphRed = gammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.red)
					+ clip(leftAnaglyph.red)))];
			int anaglyphGreen = gammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.green)
					+ clip(leftAnaglyph.green)))];
			int anaglyphBlue = gammaCorrectionLUT[(int) ((maxEncodedValue - 1) * clip(clip(rightAnaglyph.blue)
					+ clip(leftAnaglyph.blue)))];

			pixels[ii] = 0xFF000000 | (anaglyphRed << 16)
					| (anaglyphGreen << 8) | anaglyphBlue;
		}
	}
}
