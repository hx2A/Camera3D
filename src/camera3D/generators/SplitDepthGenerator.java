package camera3D.generators;

import processing.core.PApplet;
import camera3D.CameraConfiguration;

public class SplitDepthGenerator extends Generator {

	private int width;
	private int height;

	private int occlusionPlaneColor;

	public SplitDepthGenerator(int width, int height) {
		this(width, height, 0xFFFFFFFF);
	}

	public SplitDepthGenerator(int width, int height, int occlusionPlaneColor) {
		this.width = width;
		this.height = height;
		this.occlusionPlaneColor = occlusionPlaneColor;
	}

	public void notifyCameraConfigChange(PApplet parent,
			CameraConfiguration config) {
		// do nothing
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

	public void prepareForDraw(int frameNum, PApplet parent, CameraConfiguration config) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);

		if (frameNum == 0) {
			parent.frustum(config.frustumLeft, config.frustumRight,
					config.frustumBottom, config.frustumTop,
					config.frustumNear, config.frustumFar * config.cameraInput);

			parent.background(occlusionPlaneColor);
		} else if (frameNum == 1) {
			parent.frustum(config.frustumLeft, config.frustumRight,
					config.frustumBottom, config.frustumTop,
					config.frustumNear, config.frustumFar);
		}
	}

	public void cleanup(PApplet parent, CameraConfiguration config) {
		// do nothing
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		int pos;

		for (int i = 0; i < height; ++i) {
			pos = (int) (i * width + width * 0.3f - 5);
			System.arraycopy(pixelsAlt, pos, pixels, pos, 10);
			pos = (int) (i * width + width * 0.7f - 5);
			System.arraycopy(pixelsAlt, pos, pixels, pos, 10);
		}
	}
}
