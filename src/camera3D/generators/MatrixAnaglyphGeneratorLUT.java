package camera3D.generators;

import camera3D.generators.AnaglyphGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class MatrixAnaglyphGeneratorLUT extends AnaglyphGenerator {

	private int[] leftLUT;
	private int[] rightLUT;

	public MatrixAnaglyphGeneratorLUT(AnaglyphMatrix left, AnaglyphMatrix right) {
		// Validate the inputs
		if (((left.r1c1 != 0 || left.r1c2 != 0 || left.r1c3 != 0) && (right.r1c1 != 0
				|| right.r1c2 != 0 || right.r1c3 != 0))
				|| ((left.r2c1 != 0 || left.r2c2 != 0 || left.r2c3 != 0) && (right.r2c1 != 0
						|| right.r2c2 != 0 || right.r2c3 != 0))
				|| ((left.r3c1 != 0 || left.r3c2 != 0 || left.r3c3 != 0) && (right.r3c1 != 0
						|| right.r3c2 != 0 || right.r3c3 != 0))) {
			System.err.println("Invalid left and right matrix values.");
			System.err
					.println("Each matrix row can be non-zero in at most one of the (left, right) matrix pair.");
			System.err
					.println("Fix the matrix or use the MatrixAnaglyphGenerator class instead.");
			throw new RuntimeException("Invalid matrix inputs");
		}

		System.out.println("precomputing lookup tables...");

		leftLUT = preComputeLUT(left);
		rightLUT = preComputeLUT(right);

		System.out.println("done!");
	}

	public static AnaglyphGenerator createTrueAnaglyphGenerator() {
		return new MatrixAnaglyphGeneratorLUT(
				AnaglyphConstants.LEFT_TRUE_ANAGLYPH,
				AnaglyphConstants.RIGHT_TRUE_ANAGLYPH);
	}

	public static AnaglyphGenerator createGrayAnaglyphGenerator() {
		return new MatrixAnaglyphGeneratorLUT(
				AnaglyphConstants.LEFT_GRAY_ANAGLYPH,
				AnaglyphConstants.RIGHT_GRAY_ANAGLYPH);
	}

	public static AnaglyphGenerator createHalfColorAnaglyphGenerator() {
		return new MatrixAnaglyphGeneratorLUT(
				AnaglyphConstants.LEFT_HALF_COLOR_ANAGLYPH,
				AnaglyphConstants.RIGHT_HALF_COLOR_ANAGLYPH);
	}

	private int[] preComputeLUT(AnaglyphMatrix matrix) {
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

	public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		for (int ii = 0; ii < pixels.length; ++ii) {
			pixels[ii] = 0xFF000000 | leftLUT[pixels[ii] & 0x00FFFFFF]
					| rightLUT[pixels[ii] & 0x00FFFFFF];
		}

		return pixels;
	}
}
