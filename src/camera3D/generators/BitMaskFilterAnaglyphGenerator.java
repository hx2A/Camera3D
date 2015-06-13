package camera3D.generators;

public class BitMaskFilterAnaglyphGenerator extends AnaglyphGenerator {

	private int leftFilter;
	private int rightFilter;

	public BitMaskFilterAnaglyphGenerator(int leftFilter, int rightFilter) {
		this.leftFilter = leftFilter;
		this.rightFilter = rightFilter;
	}

	public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt) {
		for (int ii = 0; ii < pixels.length; ++ii) {
			pixels[ii] = (pixelsAlt[ii] & rightFilter)
					| (pixels[ii] & leftFilter);
		}

		return pixels;
	}

}
