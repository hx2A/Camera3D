package camera3D.generators;

public abstract class AnaglyphGenerator {

	abstract public int[] generateAnaglyph(int[] pixels, int[] pixelsAlt);

	public float removeGammaCorrectionStandardRGB(float s) {
		if (s <= 0.04045f) {
			return s / 12.92f;
		} else {
			return (float) Math.pow((s + 0.055) / 1.055, 2.4);
		}
	}

	public float[] makeLUTremoveGammaCorrectionStandardRGB() {
		float[] removeGammaCorrectionLUT = new float[256];

		for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
			removeGammaCorrectionLUT[c] = removeGammaCorrectionStandardRGB(c / 255f);
		}

		return removeGammaCorrectionLUT;
	}

	public float applyGammaCorrectionStandardRGB(float s) {
		if (s <= 0.0031308) {
			return 12.92f * s;
		} else {
			return (float) (1.055 * Math.pow(s, 0.41666) - 0.055);
		}
	}

	public int[] makeLUTapplyGammaCorrectionStandardRGB(int maxEncodedValue) {
		int[] gammaCorrectionLUT = new int[maxEncodedValue];
		for (int s = 0; s < maxEncodedValue; ++s) {
			gammaCorrectionLUT[s] = (int) (255 * applyGammaCorrectionStandardRGB(s
					/ (float) maxEncodedValue));
		}
		return gammaCorrectionLUT;
	}

	public float removeGammaCorrection(float s, float gamma) {
		return (float) Math.pow(s, gamma);
	}

	public float applyGammaCorrection(float s, float gamma) {
		return (float) Math.pow(s, 1 / gamma);
	}

	protected float clip(float x) {
		return Math.min(Math.max(x, 0), 1);
	}

	protected float clip(float x, float min, float max) {
		return Math.min(Math.max(x, min), max);
	}
}
