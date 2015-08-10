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
		// left
		for (int i = 0; i < pixels.length; i = i + 2) {
			pixels[i / width * width + i % width / 2] = pixels[i];
		}

		// right
		for (int i = 0; i < pixels.length; i = i + 2) {
			pixels[i / width * width + i % width / 2 + width / 2] = pixelsAlt[i];
		}
	}
}
