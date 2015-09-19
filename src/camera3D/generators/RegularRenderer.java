package camera3D.generators;

import processing.core.PApplet;
import camera3D.CameraConfiguration;

public class RegularRenderer extends Generator {

	public RegularRenderer() {

	}

	public int getComponentCount() {
		return 1;
	}

	public String getComponentFrameName(int frameNum) {
		return "regular";
	}

	protected void recalculateCameraSettings() {
		// do nothing
	}

	public void prepareForDraw(int frameNum, PApplet parent) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		// do nothing
	}

	public void cleanup(PApplet parent) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);
	}
}
