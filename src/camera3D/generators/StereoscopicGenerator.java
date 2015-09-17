package camera3D.generators;

import camera3D.CameraConfiguration;
import processing.core.PApplet;
import processing.core.PConstants;

public abstract class StereoscopicGenerator extends Generator implements PConstants {

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
}
