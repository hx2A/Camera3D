package camera3D.generators;

import processing.core.PApplet;
import camera3D.CameraConfiguration;

/**
 * 
 * Regular renderer that doesn't do anything special.
 * 
 * This is useful if you want to make Camera3D render "normally" without
 * altering too much of your code.
 * 
 * @author James Schmitz
 *
 */
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

		parent.frustum(config.frustumLeft, config.frustumRight,
				config.frustumBottom, config.frustumTop, config.frustumNear,
				config.frustumFar);
	}

	public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
		// do nothing
	}

	public void completedDraw(int frameNum, PApplet parent) {
		// do nothing
	}

	public void cleanup(PApplet parent) {
		parent.camera(config.cameraPositionX, config.cameraPositionY,
				config.cameraPositionZ, config.cameraTargetX,
				config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
				config.cameraUpY, config.cameraUpZ);

		parent.frustum(config.frustumLeft, config.frustumRight,
				config.frustumBottom, config.frustumTop, config.frustumNear,
				config.frustumFar);
	}
}
