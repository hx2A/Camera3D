package camera3D.generators;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * 
 * @author James Schmitz
 *
 *         Base for all Steroscopic generators, including Anaglyph generators.
 *
 *         Many thanks to Paul Bourke for explaining the correct way to do
 *         stereoscopic rendering using parallel axis asymmetric frustum
 *         perspective projection:
 * 
 *         http://paulbourke.net/stereographics/stereorender/
 * 
 */
public abstract class StereoscopicGenerator extends Generator implements
		PConstants {

	private float divergence;
	private int swapLeftRight;
	private boolean useAsymmetricFrustrum;

	private float cameraDivergenceX;
	private float cameraDivergenceY;
	private float cameraDivergenceZ;
	private float frustrumSkew;

	public StereoscopicGenerator() {
		divergence = 1;
		swapLeftRight = 1;
		useAsymmetricFrustrum = true;
	}

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

	public StereoscopicGenerator setDivergence(float divergence) {
		this.divergence = divergence;

		if (config != null && config.isReady())
			recalculateCameraSettings();

		return this;
	}

	public StereoscopicGenerator swapLeftRight(boolean swap) {
		if (swap)
			swapLeftRight = -1;
		else
			swapLeftRight = 1;

		if (config != null && config.isReady())
			recalculateCameraSettings();

		return this;
	}

	public StereoscopicGenerator useSymmetricFrustum() {
		useAsymmetricFrustrum = false;

		return this;
	}

	protected void recalculateCameraSettings() {
		float dx = config.cameraPositionX - config.cameraTargetX;
		float dy = config.cameraPositionY - config.cameraTargetY;
		float dz = config.cameraPositionZ - config.cameraTargetZ;
		float diverge = -(swapLeftRight * divergence)
				/ (config.fovy * RAD_TO_DEG);

		cameraDivergenceX = (dy * config.cameraUpZ - config.cameraUpY * dz)
				* diverge;
		cameraDivergenceY = (dz * config.cameraUpX - config.cameraUpZ * dx)
				* diverge;
		cameraDivergenceZ = (dx * config.cameraUpY - config.cameraUpX * dy)
				* diverge;

		float distanceToTarget = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		float cameraDivergenceDistance = (float) (Math.signum(swapLeftRight
				* divergence) * Math.sqrt(cameraDivergenceX * cameraDivergenceX
				+ cameraDivergenceY * cameraDivergenceY + cameraDivergenceZ
				* cameraDivergenceZ));
		frustrumSkew = cameraDivergenceDistance * config.frustumNear
				/ distanceToTarget;
	}

	public void prepareForDraw(int frameNum, PApplet parent) {
		if (useAsymmetricFrustrum) {
			if (frameNum == 0) {
				parent.camera(config.cameraPositionX + cameraDivergenceX,
						config.cameraPositionY + cameraDivergenceY,
						config.cameraPositionZ + cameraDivergenceZ,
						config.cameraTargetX + cameraDivergenceX,
						config.cameraTargetY + cameraDivergenceY,
						config.cameraTargetZ + cameraDivergenceZ,
						config.cameraUpX, config.cameraUpY, config.cameraUpZ);

				parent.frustum(config.frustumLeft - frustrumSkew,
						config.frustumRight - frustrumSkew,
						config.frustumBottom, config.frustumTop,
						config.frustumNear, config.frustumFar);
			} else if (frameNum == 1) {
				parent.camera(config.cameraPositionX - cameraDivergenceX,
						config.cameraPositionY - cameraDivergenceY,
						config.cameraPositionZ - cameraDivergenceZ,
						config.cameraTargetX - cameraDivergenceX,
						config.cameraTargetY - cameraDivergenceY,
						config.cameraTargetZ - cameraDivergenceZ,
						config.cameraUpX, config.cameraUpY, config.cameraUpZ);

				parent.frustum(config.frustumLeft + frustrumSkew,
						config.frustumRight + frustrumSkew,
						config.frustumBottom, config.frustumTop,
						config.frustumNear, config.frustumFar);
			}
		} else {
			if (frameNum == 0) {
				parent.camera(config.cameraPositionX + cameraDivergenceX,
						config.cameraPositionY + cameraDivergenceY,
						config.cameraPositionZ + cameraDivergenceZ,
						config.cameraTargetX, config.cameraTargetY,
						config.cameraTargetZ, config.cameraUpX,
						config.cameraUpY, config.cameraUpZ);
			} else if (frameNum == 1) {
				parent.camera(config.cameraPositionX - cameraDivergenceX,
						config.cameraPositionY - cameraDivergenceY,
						config.cameraPositionZ - cameraDivergenceZ,
						config.cameraTargetX, config.cameraTargetY,
						config.cameraTargetZ, config.cameraUpX,
						config.cameraUpY, config.cameraUpZ);
			}

			parent.frustum(config.frustumLeft, config.frustumRight,
					config.frustumBottom, config.frustumTop,
					config.frustumNear, config.frustumFar);
		}
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
