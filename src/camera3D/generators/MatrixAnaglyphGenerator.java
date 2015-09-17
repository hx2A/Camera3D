package camera3D.generators;

import camera3D.generators.StereoscopicGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

public class MatrixAnaglyphGenerator extends AnaglyphGenerator {

	private AnaglyphMatrix left;
	private AnaglyphMatrix right;

	public MatrixAnaglyphGenerator(AnaglyphMatrix left, AnaglyphMatrix right) {
		this.left = left;
		this.right = right;
	}

	public static StereoscopicGenerator createTrueAnaglyphGenerator() {
		return new MatrixAnaglyphGenerator(LEFT_TRUE_ANAGLYPH,
				RIGHT_TRUE_ANAGLYPH);
	}

	public static StereoscopicGenerator createGrayAnaglyphGenerator() {
		return new MatrixAnaglyphGenerator(LEFT_GRAY_ANAGLYPH,
				RIGHT_GRAY_ANAGLYPH);
	}

	public static StereoscopicGenerator createHalfColorAnaglyphGenerator() {
		return new MatrixAnaglyphGenerator(LEFT_HALF_COLOR_ANAGLYPH,
				RIGHT_HALF_COLOR_ANAGLYPH);
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		for (int ii = 0; ii < pixels.length; ++ii) {
			ColorVector leftColor = left.rightMult(new ColorVector(
					(0x00FF0000 & pixels[ii]) >> 16,
					(0x0000FF00 & pixels[ii]) >> 8, 0x000000FF & pixels[ii]));

			ColorVector rightColor = right.rightMult(new ColorVector(
					(0x00FF0000 & pixelsAlt[ii]) >> 16,
					(0x0000FF00 & pixelsAlt[ii]) >> 8,
					0x000000FF & pixelsAlt[ii]));

			ColorVector anaglyphColor = leftColor.add(rightColor);

			pixels[ii] = 0xFF000000
					| (((int) clip(anaglyphColor.red, 0, 255)) << 16)
					| (((int) clip(anaglyphColor.green, 0, 255)) << 8)
					| ((int) clip(anaglyphColor.blue, 0, 255));
		}
	}
}
