package camera3D.generators;

import camera3D.generators.AnaglyphGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class DuboisAnaglyphGenerator64bitLUT extends AnaglyphGenerator {

	private long[] leftLUT;
	private long[] rightLUT;
	private float[] removeGammaCorrectionLUT;
	private int[] gammaCorrectionLUT;
	private final int MAX_ENCODED_VALUE = (int) Math.pow(2, 20);

	public DuboisAnaglyphGenerator64bitLUT(AnaglyphMatrix left,
			AnaglyphMatrix right) {
		System.out.println("precomputing lookup tables...");

		removeGammaCorrectionLUT = makeLUTremoveGammaCorrectionStandardRGB();

		leftLUT = preComputeLUT(left);
		rightLUT = preComputeLUT(right);

		gammaCorrectionLUT = makeLUTapplyGammaCorrectionStandardRGB(MAX_ENCODED_VALUE);

		System.out.println("done!");
	}

	public static AnaglyphGenerator createRedCyanGenerator() {
		return new DuboisAnaglyphGenerator64bitLUT(
				AnaglyphConstants.LEFT_DUBOIS_REDCYAN,
				AnaglyphConstants.RIGHT_DUBOIS_REDCYAN);
	}

	public static AnaglyphGenerator createMagentaGreenGenerator() {
		return new DuboisAnaglyphGenerator64bitLUT(
				AnaglyphConstants.LEFT_DUBOIS_MAGENTAGREEN,
				AnaglyphConstants.RIGHT_DUBOIS_MAGENTAGREEN);
	}

	public static AnaglyphGenerator createAmberBlueGenerator() {
		return new DuboisAnaglyphGenerator64bitLUT(
				AnaglyphConstants.LEFT_DUBOIS_AMBERBLUE,
				AnaglyphConstants.RIGHT_DUBOIS_AMBERBLUE);
	}

	private long[] preComputeLUT(AnaglyphMatrix matrix) {
		long[] lut = new long[256 * 256 * 256];

		for (int col = 0; col < lut.length; ++col) {
			float red = removeGammaCorrectionLUT[(col & 0x00FF0000) >> 16];
			float green = removeGammaCorrectionLUT[(col & 0x0000FF00) >> 8];
			float blue = removeGammaCorrectionLUT[col & 0x000000FF];

			ColorVector val = matrix
					.rightMult(new ColorVector(red, green, blue));

			long encodedRed = (long) (clip(val.red) * (MAX_ENCODED_VALUE - 1));
			long encodedGreen = (long) (clip(val.green) * (MAX_ENCODED_VALUE - 1));
			long encodedBlue = (long) (clip(val.blue) * (MAX_ENCODED_VALUE - 1));

			lut[col] = (encodedRed << 42) | (encodedGreen << 21) | encodedBlue;
		}

		return lut;
	}

	public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		long encodedColor;

		for (int ii = 0; ii < pixels.length; ++ii) {
			encodedColor = leftLUT[pixels[ii] & 0x00FFFFFF]
					+ rightLUT[pixelsAlt[ii] & 0x00FFFFFF];

			if (0 < (encodedColor & 0x4000000000000000L)) {
				pixels[ii] = 0xFFFF0000;
			} else {
				pixels[ii] = 0xFF000000 | (gammaCorrectionLUT[(int) ((encodedColor & 0x3FFFFC0000000000L) >> 42)] << 16);
			}

			if (0 < (encodedColor & 0x20000000000L)) {
				pixels[ii] |= 0x0000FF00;
			} else {
				pixels[ii] |= (gammaCorrectionLUT[(int) ((encodedColor & 0x1FFFFE00000L) >> 21)] << 8);
			}

			if (0 < (encodedColor & 0x100000L)) {
				pixels[ii] |= 0x000000FF;
			} else {
				pixels[ii] |= (gammaCorrectionLUT[(int) (encodedColor & 0xFFFFFL)]);
			}
		}

		return pixels;
	}
}
