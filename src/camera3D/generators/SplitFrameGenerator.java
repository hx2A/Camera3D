package camera3D.generators;

public class SplitFrameGenerator extends StereoscopicGenerator {

	public static final int SIDE_BY_SIDE = 0;
	public static final int OVER_UNDER = 1;
	public static final int GOOGLE_CARDBOARD = 2;

	private int width;
	private int height;
	private int technique;

	public SplitFrameGenerator(int width, int height, int technique) {
		this.width = width;
		this.height = height;
		this.technique = technique;
	}

	public static SplitFrameGenerator createSideBySideGenerator(int width,
			int height) {
		return new SplitFrameGenerator(width, height, SIDE_BY_SIDE);
	}

	public static SplitFrameGenerator createOverUnderGenerator(int width,
			int height) {
		return new SplitFrameGenerator(width, height, OVER_UNDER);
	}

	public static SplitFrameGenerator createGoogleCardboardGenerator(int width,
			int height) {
		return new SplitFrameGenerator(width, height, GOOGLE_CARDBOARD);
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		if (technique == SIDE_BY_SIDE) {
			for (int y = 0; y < height; y++) {
				// left
				int offset = y * width;
				for (int x = 0; x < width / 2; x++) {
					pixels[offset + x] = pixels[offset + x * 2];
				}

				// right
				int offset2 = offset + width / 2;
				for (int x = 0; x < width / 2; x++) {
					pixels[offset2 + x] = pixelsAlt[offset + x * 2];
				}
			}
		} else if (technique == OVER_UNDER) {
			// over
			for (int y = 0; y < height / 2; ++y) {
				System.arraycopy(pixels, 2 * y * width, pixels, y * width,
						width);
			}

			// under
			for (int y = 0; y < height / 2; ++y) {
				System.arraycopy(pixelsAlt, 2 * y * width, pixels, y * width
						+ width * (height / 2), width);
			}
		} else if (technique == GOOGLE_CARDBOARD) {
			for (int y = 0; y < height; ++y) {
				System.arraycopy(pixels, y * width + (width / 4), pixels, y
						* width, width / 2);
				System.arraycopy(pixelsAlt, y * width + (width / 4), pixels, y
						* width + width / 2, width / 2);
				pixels[y * width + width / 2] = 0xFF000000;
			}
		}
	}
}
