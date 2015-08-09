package camera3D.generators;

public class OverUnderGenerator extends AnaglyphGenerator {

	private int width;
	private int height;

	public OverUnderGenerator(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public static AnaglyphGenerator createGoogleCardboardGenerator(int width,
			int height) {
		return new OverUnderGenerator(width, height);
	}

	public void generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		// over
		for (int y = 0; y < height / 2; ++y) {
			System.arraycopy(pixels, 2 * y * width, pixels, y * width, width);
		}

		// under
		for (int y = 0; y < height / 2; ++y) {
			System.arraycopy(pixelsAlt, 2 * y * width, pixels, y * width
					+ width * (height / 2), width);
		}
	}
}
