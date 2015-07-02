package camera3D.generators;

public class StereogramGenerator extends AnaglyphGenerator {

	private int width;
	private int height;

	public StereogramGenerator(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public static AnaglyphGenerator createGoogleCardboardGenerator(int width,
			int height) {
		return new StereogramGenerator(width, height);
	}

	public void generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		for (int y = 0; y < height; ++y) {
			System.arraycopy(pixels, y * width + (width / 4), pixels,
					y * width, width / 2);
			System.arraycopy(pixelsAlt, y * width + (width / 4), pixels, y
					* width + width / 2, width / 2);
			pixels[y * width + width / 2] = 0xFF000000;
		}
	}
}
