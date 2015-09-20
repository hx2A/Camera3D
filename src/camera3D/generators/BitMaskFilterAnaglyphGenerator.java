package camera3D.generators;

import camera3D.generators.AnaglyphGenerator;

public class BitMaskFilterAnaglyphGenerator extends AnaglyphGenerator {

	private int leftFilter;
	private int rightFilter;

	public BitMaskFilterAnaglyphGenerator(int leftFilter, int rightFilter) {
		this.leftFilter = leftFilter;
		this.rightFilter = rightFilter;
	}

	public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
		for (int ii = 0; ii < pixelDest.length; ++ii) {
			pixelDest[ii] = (pixelStorage[0][ii] & rightFilter)
					| (pixelDest[ii] & leftFilter);
		}
	}
}
