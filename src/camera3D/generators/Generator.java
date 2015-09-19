package camera3D.generators;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import camera3D.CameraConfiguration;

public abstract class Generator {

	protected CameraConfiguration config;

	/**
	 * The number of components in the composite image, ie, the number of times
	 * to call the draw method.
	 * 
	 * @return int
	 */
	abstract public int getComponentCount();

	/**
	 * Give each component a name, such as "left" and "right" for anaglyph
	 * images.
	 * 
	 * @param frameNum
	 * @return
	 */
	abstract public String getComponentFrameName(int frameNum);

	/**
	 * Notify renderer that something about the camera changed. For example, the
	 * stereoscopic generators need to do some recalculations when the camera
	 * moves.
	 * 
	 * @param config
	 */
	public void notifyCameraConfigChange(CameraConfiguration config) {
		this.config = config;

		if (config != null && config.isReady())
			recalculateCameraSettings();
	}

	/**
	 * Perform renderer recalculations due to camera config change.
	 */
	abstract protected void recalculateCameraSettings();

	/**
	 * This is called once before each call to the user's draw method. Typically
	 * the generators need to move the camera around or change some settings.
	 * 
	 * @param frameNum
	 * @param parent
	 */
	abstract public void prepareForDraw(int frameNum, PApplet parent);

	/**
	 * Combine the component frames into one composite frame.
	 * 
	 * @param pixels
	 * @param pixelsAlt
	 */
	abstract public void generateCompositeFrame(int[] pixels, int[] pixelsAlt);

	public void generateCompositeFrameAndSaveComponents(int[] pixels,
			int[] pixelsAlt, PApplet parent, String parentClassName) {
		PImage frame1 = parent.createImage(parent.width, parent.height,
				PConstants.RGB);
		frame1.loadPixels();
		generateCompositeFrame(frame1.pixels, pixelsAlt);
		frame1.updatePixels();
		frame1.save(parent.insertFrame("####-" + parentClassName + "-"
				+ getComponentFrameName(0) + "-component-modified.png"));

		PImage frame2 = parent.createImage(parent.width, parent.height,
				PConstants.RGB);
		frame2.loadPixels();
		System.arraycopy(pixels, 0, frame2.pixels, 0, pixels.length);
		generateCompositeFrame(frame2.pixels, new int[pixels.length]);
		frame2.updatePixels();
		frame2.save(parent.insertFrame("####-" + parentClassName + "-"
				+ getComponentFrameName(1) + "-component-modified.png"));

		generateCompositeFrame(pixels, pixelsAlt);
	}

	/**
	 * This is called after the last call to the user's draw method and before
	 * the user's postDraw method. This should put the camera back so other
	 * libraries like ControlP5 can function correctly.
	 * 
	 * @param parent
	 */
	abstract public void cleanup(PApplet parent);

	/**
	 * Simple utility function that is used in a couple of places.
	 * 
	 * @param x
	 * @return
	 */
	protected float clip(float x) {
		return Math.min(Math.max(x, 0), 1);
	}

	/**
	 * Simple utility function that is used in a couple of places.
	 * 
	 * @param x
	 * @param min
	 * @param max
	 * @return
	 */
	protected float clip(float x, float min, float max) {
		return Math.min(Math.max(x, min), max);
	}
}
