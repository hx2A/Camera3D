package camera3D.generators;

import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class DuboisAnaglyphGenerator64bitLUT extends AnaglyphGenerator {

	private long[] leftLUT;
	private long[] rightLUT;
	private float[] removeGammaCorrectionLUT;
	private int[] gammaCorrectionLUT;

	private int maxEncodedValue;

	public DuboisAnaglyphGenerator64bitLUT(AnaglyphMatrix left,
			AnaglyphMatrix right) {
		maxEncodedValue = (int) Math.pow(2, 20);

		removeGammaCorrectionLUT = new float[256];
		for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
			removeGammaCorrectionLUT[c] = removeGammaCorrectionStandardRGB(c / 255f);
		}

		leftLUT = preComputeLUT(left);
		rightLUT = preComputeLUT(right);

		gammaCorrectionLUT = new int[maxEncodedValue];
		for (int s = 0; s < maxEncodedValue; ++s) {
			gammaCorrectionLUT[s] = (int) (255 * applyGammaCorrectionStandardRGB(s
					/ (float) maxEncodedValue));
		}
	}

	private long[] preComputeLUT(AnaglyphMatrix matrix) {
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

			lut[col] = (encodedRed << 42) | (encodedGreen << 21) | encodedBlue;
		}

		return lut;
	}

	public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt) {

		long newPixel;

		for (int ii = 0; ii < pixels.length; ++ii) {
			newPixel = leftLUT[pixels[ii] & 0x00FFFFFF]
					+ rightLUT[pixelsAlt[ii] & 0x00FFFFFF];

			if (0 < (newPixel & 0x4000000000000000L)) {
				pixels[ii] = 0xFFFF0000;
			} else {
				pixels[ii] = 0xFF000000 | (gammaCorrectionLUT[(int) ((newPixel & 0x3FFFFC0000000000L) >> 42)] << 16);
			}

			if (0 < (newPixel & 0x20000000000L)) {
				pixels[ii] |= 0x0000FF00;
			} else {
				pixels[ii] |= (gammaCorrectionLUT[(int) ((newPixel & 0x1FFFFE00000L) >> 21)] << 8);
			}

			if (0 < (newPixel & 0x100000L)) {
				pixels[ii] |= 0x000000FF;
			} else {
				pixels[ii] |= (gammaCorrectionLUT[(int) (newPixel & 0xFFFFFL)]);
			}
		}

		return pixels;
	}
}
