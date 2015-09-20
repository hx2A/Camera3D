package camera3D.generators;

import processing.core.PApplet;
import processing.core.PImage;
import camera3D.CameraConfiguration;

public class SplitDepthGenerator extends Generator {

	private int width;
	private int height;

	private int occlusionPlaneColor;
	private boolean[] occlusionPlaneMask;
	private float occlusionZfactor;
	private float occlusionZ;

	public SplitDepthGenerator(int width, int height) {
		this.width = width;
		this.height = height;

		occlusionPlaneMask = new boolean[width * height];

		occlusionPlaneColor = 0xFFFFFFFF;
		occlusionZfactor = 1;

		for (int i = 0; i < height; ++i) {
			int pos = (int) (i * width + width * 0.3f - 5);
			for (int j = 0; j < 10; ++j) {
				occlusionPlaneMask[pos + j] = true;
			}
			pos = (int) (i * width + width * 0.7f - 5);
			for (int j = 0; j < 10; ++j) {
				occlusionPlaneMask[pos + j] = true;
			}
		}
	}

	public SplitDepthGenerator setOcclusionPlaneColor(int color) {
		occlusionPlaneColor = color;

		return this;
	}

	public SplitDepthGenerator setOcclusionPlaneMask(PImage mask) {
		if (width != mask.width || height != mask.height) {
			throw new RuntimeException(
					"The occlusion plane mask must have the same height and width as the sketch.");
		}

		mask.loadPixels();
		for (int i = 0; i < occlusionPlaneMask.length; ++i) {
			occlusionPlaneMask[i] = mask.pixels[i] == 0xFFFFFFFF;
		}

		return this;
	}

	public SplitDepthGenerator setOcclusionZfactor(float occlusionZfactor) {
		this.occlusionZfactor = occlusionZfactor;

		float xdiff = config.cameraPositionX - config.cameraTargetX;
		float ydiff = config.cameraPositionY - config.cameraTargetY;
		float zdiff = config.cameraPositionZ - config.cameraTargetZ;

		occlusionZ = occlusionZfactor
				* (float) Math.sqrt(xdiff * xdiff + ydiff * ydiff + zdiff
						* zdiff);

		return this;
	}

	public SplitDepthGenerator setOcclusionZdistance(float occlusionZdistance) {
		occlusionZ = occlusionZdistance;
		occlusionZfactor = Float.NaN;

		return this;
	}

	protected void recalculateCameraSettings() {
		if (!Float.isNaN(occlusionZfactor))
			setOcclusionZfactor(occlusionZfactor);
	}

	public int getComponentCount() {
		return 2;
	}

	public String getComponentFrameName(int frameNum) {
		if (frameNum == 0) {
			return "front";
		} else if (frameNum == 1) {
			return "behind";
		} else {
			return "";
		}
	}

	public void prepareForDraw(int frameNum, PApplet parent) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);

		if (frameNum == 0) {
			parent.frustum(config.frustumLeft, config.frustumRight,
					config.frustumBottom, config.frustumTop,
					config.frustumNear, occlusionZ);

			parent.background(occlusionPlaneColor);
		} else if (frameNum == 1) {
			parent.frustum(config.frustumLeft, config.frustumRight,
					config.frustumBottom, config.frustumTop,
					config.frustumNear, config.frustumFar);
		}
	}

	public void cleanup(PApplet parent) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);
	}

	public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
		for (int i = 0; i < occlusionPlaneMask.length; ++i) {
			if (occlusionPlaneMask[i])
				pixelDest[i] = pixelStorage[0][i];
		}
	}
}
