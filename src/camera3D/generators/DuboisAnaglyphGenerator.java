package camera3D.generators;

import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class DuboisAnaglyphGenerator extends AnaglyphGenerator {

	private AnaglyphMatrix left;
	private AnaglyphMatrix right;
	private float[] removeGammaCorrectionLUT;
	private int[] gammaCorrectionLUT;

	public DuboisAnaglyphGenerator(AnaglyphMatrix left, AnaglyphMatrix right) {
		this.left = left;
		this.right = right;

		removeGammaCorrectionLUT = new float[256];
		for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
			removeGammaCorrectionLUT[c] = removeGammaCorrectionStandardRGB(c / 255f);
		}

	}

	public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt) {

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

			float anaglyphRed = applyGammaCorrectionStandardRGB(clip(clip(rightAnaglyph.red)
					+ clip(leftAnaglyph.red)));
			float anaglyphGreen = applyGammaCorrectionStandardRGB(clip(clip(rightAnaglyph.green)
					+ clip(leftAnaglyph.green)));
			float anaglyphBlue = applyGammaCorrectionStandardRGB(clip(clip(rightAnaglyph.blue)
					+ clip(leftAnaglyph.blue)));

			pixels[ii] = 0xFF000000 | (((int) (anaglyphRed * 255)) << 16)
					| (((int) (anaglyphGreen * 255)) << 8)
					| ((int) (anaglyphBlue * 255));

		}

		return pixels;
	}

}
