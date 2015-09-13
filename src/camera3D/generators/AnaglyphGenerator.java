package camera3D.generators;

import camera3D.CameraConfiguration;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public abstract class AnaglyphGenerator extends Generator implements PConstants {

	private float cameraDivergenceX;
	private float cameraDivergenceY;
	private float cameraDivergenceZ;

	public int getComponentCount() {
		return 2;
	}

	public String getComponentFrameName(int frameNum) {
		if (frameNum == 0) {
			return "right";
		} else if (frameNum == 1) {
			return "left";
		} else {
			return "";
		}
	}

	public void notifyCameraConfigChange(PApplet parent,
			CameraConfiguration config) {
		float dx = config.cameraPositionX - config.cameraTargetX;
		float dy = config.cameraPositionY - config.cameraTargetY;
		float dz = config.cameraPositionZ - config.cameraTargetZ;
		float diverge = -config.cameraInput / (config.fovy * RAD_TO_DEG);

		cameraDivergenceX = (dy * config.cameraUpZ - config.cameraUpY * dz)
				* diverge;
		cameraDivergenceY = (dz * config.cameraUpX - config.cameraUpZ * dx)
				* diverge;
		cameraDivergenceZ = (dx * config.cameraUpY - config.cameraUpX * dy)
				* diverge;
	}

	public void prepareForDraw(int frameNum, PApplet parent,
			CameraConfiguration config) {
		if (frameNum == 0) {
			parent.camera(config.cameraPositionX + cameraDivergenceX,
					config.cameraPositionY + cameraDivergenceY,
					config.cameraPositionZ + cameraDivergenceZ,
					config.cameraTargetX, config.cameraTargetY,
					config.cameraTargetZ, config.cameraUpX, config.cameraUpY,
					config.cameraUpZ);
		} else if (frameNum == 1) {
			parent.camera(config.cameraPositionX - cameraDivergenceX,
					config.cameraPositionY - cameraDivergenceY,
					config.cameraPositionZ - cameraDivergenceZ,
					config.cameraTargetX, config.cameraTargetY,
					config.cameraTargetZ, config.cameraUpX, config.cameraUpY,
					config.cameraUpZ);
		}
	}

	public void cleanup(PApplet parent, CameraConfiguration config) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);
	}

	/*
	 * Utility Functions
	 */

	public float[] makeLUTremoveGammaCorrectionStandardRGB() {
		float[] removeGammaCorrectionLUT = new float[256];

		for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
			removeGammaCorrectionLUT[c] = removeGammaCorrectionStandardRGB(c / 255f);
		}

		return removeGammaCorrectionLUT;
	}

	public int[] makeLUTapplyGammaCorrectionStandardRGB(int maxEncodedValue) {
		int[] gammaCorrectionLUT = new int[maxEncodedValue];
		for (int s = 0; s < maxEncodedValue; ++s) {
			gammaCorrectionLUT[s] = (int) (255 * applyGammaCorrectionStandardRGB(s
					/ (float) maxEncodedValue));
		}
		return gammaCorrectionLUT;
	}

	public float applyGammaCorrectionStandardRGB(float s) {
		if (s <= 0.0031308) {
			return 12.92f * s;
		} else {
			return (float) (1.055 * Math.pow(s, 0.41666) - 0.055);
		}
	}

	public float removeGammaCorrectionStandardRGB(float s) {
		if (s <= 0.04045f) {
			return s / 12.92f;
		} else {
			return (float) Math.pow((s + 0.055) / 1.055, 2.4);
		}
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
