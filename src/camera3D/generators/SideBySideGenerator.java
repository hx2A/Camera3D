package camera3D.generators;

public class SideBySideGenerator extends AnaglyphGenerator {

	private int width;
	private int height;

	public SideBySideGenerator(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public static AnaglyphGenerator createGoogleCardboardGenerator(int width,
			int height) {
		return new SideBySideGenerator(width, height);
	}

	public void generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width / 2; x++) {
				pixels[offset + x] = pixels[offset + x * 2];
			}

			int offset2 = offset + width / 2;
			for (int x = 0; x < width / 2; x++) {
				pixels[offset2 + x] = pixelsAlt[offset + x * 2];
			}
		}
	}
}
