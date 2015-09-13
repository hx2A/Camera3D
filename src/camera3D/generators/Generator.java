package camera3D.generators;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import camera3D.CameraConfiguration;

public abstract class Generator {

	abstract public int getComponentCount();

	abstract public String getComponentFrameName(int frameNum);

	abstract public void notifyCameraConfigChange(PApplet parent,
			CameraConfiguration config);

	abstract public void prepareForDraw(int frameNum, PApplet parent,
			CameraConfiguration config);

	abstract public void generateCompositeFrame(int[] pixels, int[] pixelsAlt);

	public void generateCompositeFrameAndSaveComponents(int[] pixels,
			int[] pixelsAlt, PApplet parent, String parentClassName) {
		PImage frame1 = parent.createImage(parent.width, parent.height,
				PConstants.RGB);
		frame1.loadPixels();
		generateCompositeFrame(frame1.pixels, pixelsAlt);
		frame1.updatePixels();
		frame1.save(parent.insertFrame("####-" + parentClassName
				+ "-right-filtered.png"));

		PImage frame2 = parent.createImage(parent.width, parent.height,
				PConstants.RGB);
		frame2.loadPixels();
		System.arraycopy(pixels, 0, frame2.pixels, 0, pixels.length);
		generateCompositeFrame(frame2.pixels, new int[pixels.length]);
		frame2.updatePixels();
		frame2.save(parent.insertFrame("####-" + parentClassName
				+ "-left-filtered.png"));

		generateCompositeFrame(pixels, pixelsAlt);
	}

	abstract public void cleanup(PApplet parent, CameraConfiguration config);

}
