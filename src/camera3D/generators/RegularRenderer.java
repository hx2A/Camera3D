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

	public void notifyCameraConfigChange(PApplet parent,
			CameraConfiguration config) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);
	}

	public void prepareForDraw(int frameNum, PApplet parent,
			CameraConfiguration config) {
		// do nothing
	}

	public void generateCompositeFrame(int[] pixels, int[] pixelsAlt) {
		// do nothing
	}

	public void cleanup(PApplet parent, CameraConfiguration config) {
		// do nothing
	}
}
