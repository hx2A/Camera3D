package camera3D.generators;

import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

/**
 * 
 * Base class for all of the anaglyph generators.
 * 
 * This class provides some default matrix values and implementations
 * of some useful functions for making anaglyphs. If you make your own
 * anaglyph generator you may find it helpful to derive from this class.
 * 
 * @author James Schmitz
 *
 */
public abstract class AnaglyphGenerator extends StereoscopicGenerator {

	/*
	 * Define some Matrix constants. These are used for the default anaglyph
	 * renderers.
	 */
	// Dubois Red Cyan
	public final static AnaglyphMatrix LEFT_DUBOIS_REDCYAN = new AnaglyphMatrix(
			437, 449, 164, -62, -62, -24, -48, -50, -17);
	public final static AnaglyphMatrix RIGHT_DUBOIS_REDCYAN = new AnaglyphMatrix(
			-11, -32, -7, 377, 761, 9, -26, -93, 1234);

	// Dubois Magenta Green
	public final static AnaglyphMatrix LEFT_DUBOIS_MAGENTAGREEN = new AnaglyphMatrix(
			-62, -158, -39, 284, 668, 143, -15, -27, 21);
	public final static AnaglyphMatrix RIGHT_DUBOIS_MAGENTAGREEN = new AnaglyphMatrix(
			529, 705, 24, -16, -15, -65, 9, 75, 937);

	// Dubois Amber Blue
	public final static AnaglyphMatrix LEFT_DUBOIS_AMBERBLUE = new AnaglyphMatrix(
			1062, -205, 299, -26, 908, 68, -38, -173, 22);
	public final static AnaglyphMatrix RIGHT_DUBOIS_AMBERBLUE = new AnaglyphMatrix(
			-16, -123, -17, 6, 62, -17, 94, 185, 911);

	// True Anaglyph
	public final static AnaglyphMatrix LEFT_TRUE_ANAGLYPH = new AnaglyphMatrix(
			299, 587, 114, 0, 0, 0, 0, 0, 0);
	public final static AnaglyphMatrix RIGHT_TRUE_ANAGLYPH = new AnaglyphMatrix(
			0, 0, 0, 0, 0, 0, 299, 587, 114);

	// Gray Anaglyph
	public final static AnaglyphMatrix LEFT_GRAY_ANAGLYPH = new AnaglyphMatrix(
			299, 587, 114, 0, 0, 0, 0, 0, 0);
	public final static AnaglyphMatrix RIGHT_GRAY_ANAGLYPH = new AnaglyphMatrix(
			0, 0, 0, 299, 587, 114, 299, 587, 114);

	// Half Color Anaglyph
	public final static AnaglyphMatrix LEFT_HALF_COLOR_ANAGLYPH = new AnaglyphMatrix(
			299, 587, 114, 0, 0, 0, 0, 0, 0);
	public final static AnaglyphMatrix RIGHT_HALF_COLOR_ANAGLYPH = new AnaglyphMatrix(
			0, 0, 0, 0, 1000, 0, 0, 0, 1000);

	/*
	 * Gamma Correction Functions
	 */
	protected float applyGammaCorrectionStandardRGB(float s) {
		if (s <= 0.0031308) {
			return 12.92f * s;
		} else {
			return (float) (1.055 * Math.pow(s, 0.41666) - 0.055);
		}
	}

	protected float removeGammaCorrectionStandardRGB(float s) {
		if (s <= 0.04045f) {
			return s / 12.92f;
		} else {
			return (float) Math.pow((s + 0.055) / 1.055, 2.4);
		}
	}

	protected float removeGammaCorrection(float s, float gamma) {
		return (float) Math.pow(s, gamma);
	}

	protected float applyGammaCorrection(float s, float gamma) {
		return (float) Math.pow(s, 1 / gamma);
	}

	/*
	 * Functions for pre-calculating look-up-tables
	 */
	protected int[] preComputeApplyGammaCorrectionStandardrgbLUT(int maxEncodedValue) {
		int[] gammaCorrectionLUT = new int[maxEncodedValue];
		for (int s = 0; s < maxEncodedValue; ++s) {
			gammaCorrectionLUT[s] = (int) (255 * applyGammaCorrectionStandardRGB(s
					/ (float) maxEncodedValue));
		}
		return gammaCorrectionLUT;
	}

	protected float[] preComputeRemoveGammaCorrectionStandardrgbLUT() {
		float[] removeGammaCorrectionLUT = new float[256];

		for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
			removeGammaCorrectionLUT[c] = removeGammaCorrectionStandardRGB(c / 255f);
		}

		return removeGammaCorrectionLUT;
	}

	protected long[] preComputeDuboisLUT(AnaglyphMatrix matrix,
			float[] removeGammaCorrectionLUT, int maxEncodedValue, int bitshift) {
		long[] lut = new long[256 * 256 * 256];

		for (int col = 0; col < lut.length; ++col) {
			float red = removeGammaCorrectionLUT[(col & 0x00FF0000) >> 16];
			float green = removeGammaCorrectionLUT[(col & 0x0000FF00) >> 8];
			float blue = removeGammaCorrectionLUT[col & 0x000000FF];

			ColorVector val = matrix
					.rightMult(new ColorVector(red, green, blue));

			long encodedRed = (long) (clip(val.red) * (maxEncodedValue - 1));
			long encodedGreen = (long) (clip(val.green) * (maxEncodedValue - 1));
			long encodedBlue = (long) (clip(val.blue) * (maxEncodedValue - 1));

			lut[col] = (encodedRed << (bitshift * 2))
					| (encodedGreen << bitshift) | encodedBlue;
		}

		return lut;
	}

	protected int[] preComputeMatrixLUT(AnaglyphMatrix matrix) {
		int[] lut = new int[256 * 256 * 256];

		for (int col = 0; col < lut.length; ++col) {
			int red = (col & 0xFF0000) >> 16;
			int green = (col & 0x00FF00) >> 8;
			int blue = col & 0x0000FF;

			ColorVector val = matrix
					.rightMult(new ColorVector(red, green, blue));

			int encodedRed = (int) clip(val.red, 0, 255);
			int encodedGreen = (int) clip(val.green, 0, 255);
			int encodedBlue = (int) clip(val.blue, 0, 255);

			lut[col] = (encodedRed << 16) | (encodedGreen << 8) | encodedBlue;
		}

		return lut;
	}
}
