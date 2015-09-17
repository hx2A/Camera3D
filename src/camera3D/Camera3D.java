package camera3D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.Math;

import processing.core.*;
import processing.event.KeyEvent;
import camera3D.generators.*;

public class Camera3D implements PConstants {

	public final static String VERSION = "##library.prettyVersion##";

	private PApplet parent;

	private int width;
	private int height;
	private int pixelCount;
	private int[] pixelsAlt;
	float avgGeneratorTimeMillis;

	private boolean enableSaveFrame;
	private boolean saveNextFrame;
	private char saveFrameKey;
	private int saveFrameNum;
	private String parentClassName;

	private CameraConfiguration config;

	private Generator generator;
	private float backgroundColor;
	private boolean callPreDraw;
	private boolean callPostDraw;
	private int frameNum;

	public Camera3D(PApplet parent) {
		this.parent = parent;

		String currentRenderer = parent.g.getClass().getName();
		if (!currentRenderer.equals(P3D)) {
			System.err.println("This library only works with P3D Sketches.");
			System.err.println("Add P3D to your size method, like this: ");
			System.err.println("size(" + parent.width + ", " + parent.height
					+ ", P3D)");
			parent.exit();
		}

		String[] tokens = parent.getClass().getName().split("\\.");
		parentClassName = tokens[tokens.length - 1].toLowerCase();

		this.backgroundColor = 255;
		this.callPreDraw = checkForMethod("preDraw");
		this.callPostDraw = checkForMethod("postDraw");

		parent.registerMethod("pre", this);
		parent.registerMethod("draw", this);
		parent.registerMethod("keyEvent", this);

		height = parent.height;
		width = parent.width;
		pixelCount = parent.width * parent.height;
		pixelsAlt = new int[pixelCount];
		avgGeneratorTimeMillis = 1;

		enableSaveFrame = false;
		saveNextFrame = false;

		config = new CameraConfiguration();

		renderDefaultAnaglyph();

		camera();
		perspective();
		setCameraDivergence(3);

		welcome();
	}

	private void welcome() {
		System.out
				.println("##library.name## ##library.prettyVersion## by ##author##");
	}

	public static String version() {
		return VERSION;
	}

	/*
	 * User Settings
	 */
	public void setBackgroundColor(float backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void renderDefaultAnaglyph() {
		renderBitMaskRedCyanAnaglyph();
	}

	public void renderBitMaskRedCyanAnaglyph() {
		renderBitMaskFilterAnaglyph(0xFFFF0000, 0x0000FFFF);
	}

	public void renderBitMaskMagentaGreenAnaglyph() {
		renderBitMaskFilterAnaglyph(0xFFFF00FF, 0x0000FF00);
	}

	public void renderBitMaskFilterAnaglyph(int leftFilter, int rightFilter) {
		generator = new BitMaskFilterAnaglyphGenerator(leftFilter, rightFilter);
		setGenerator(generator);
	}

	public void renderDuboisRedCyanAnaglyph() {
		setGenerator(DuboisAnaglyphGenerator64bitLUT.createRedCyanGenerator());
	}

	public void renderDuboisMagentaGreenAnaglyph() {
		setGenerator(DuboisAnaglyphGenerator64bitLUT
				.createMagentaGreenGenerator());
	}

	public void renderDuboisAmberBlueAnaglyph() {
		setGenerator(DuboisAnaglyphGenerator64bitLUT.createAmberBlueGenerator());
	}

	public void renderTrueAnaglyph() {
		setGenerator(MatrixAnaglyphGeneratorLUT.createTrueAnaglyphGenerator());
	}

	public void renderGrayAnaglyph() {
		setGenerator(MatrixAnaglyphGeneratorLUT.createGrayAnaglyphGenerator());
	}

	public void renderHalfColorAnaglyph() {
		setGenerator(MatrixAnaglyphGeneratorLUT
				.createHalfColorAnaglyphGenerator());
	}

	public void renderGoogleCardboard() {
		setGenerator(new SplitFrameGenerator(parent.width, parent.height,
				SplitFrameGenerator.GOOGLE_CARDBOARD));
	}

	public void renderSplitFrameOverUnder() {
		setGenerator(new SplitFrameGenerator(parent.width, parent.height,
				SplitFrameGenerator.OVER_UNDER));
	}

	public void renderSplitFrameSideBySide() {
		setGenerator(new SplitFrameGenerator(parent.width, parent.height,
				SplitFrameGenerator.SIDE_BY_SIDE));
	}

	public void renderSplitDepthIllusion() {
		setGenerator(new SplitDepthGenerator(parent.width, parent.height));
	}

	public void renderRegular() {
		setGenerator(new RegularRenderer());
	}

	public void setGenerator(Generator generator) {
		this.generator = generator;
		avgGeneratorTimeMillis = 1;
		generator.notifyCameraConfigChange(parent, config);
	}

	public Generator getGenerator() {
		return generator;
	}

	public void enableSaveFrame(char key) {
		saveFrameKey = key;
		enableSaveFrame = true;
	}

	public void enableSaveFrame() {
		enableSaveFrame('s');
	}

	public float getGeneratorTime() {
		return avgGeneratorTimeMillis;
	}

	/*
	 * Camera Methods
	 */
	public PeasyCamAdapter createPeasyCamAdapter() {
		return new PeasyCamAdapter(parent, this);
	}

	public void camera() {
		camera(width / 2f, height / 2f,
				(height / 2f) / (float) Math.tan(PI * 30.0 / 180.0),
				width / 2f, height / 2f, 0, 0, 1, 0);
	}

	public void camera(float cameraX, float cameraY, float cameraZ,
			float targetX, float targetY, float targetZ, float upX, float upY,
			float upZ) {
		config.cameraPositionX = cameraX;
		config.cameraPositionY = cameraY;
		config.cameraPositionZ = cameraZ;
		config.cameraTargetX = targetX;
		config.cameraTargetY = targetY;
		config.cameraTargetZ = targetZ;
		config.cameraUpX = upX;
		config.cameraUpY = upY;
		config.cameraUpZ = upZ;

		generator.notifyCameraConfigChange(parent, config);
	}

	public void setCameraLocation(float cameraX, float cameraY, float cameraZ) {
		config.cameraPositionX = cameraX;
		config.cameraPositionY = cameraY;
		config.cameraPositionZ = cameraZ;

		generator.notifyCameraConfigChange(parent, config);
	}

	public void setCameraTarget(float targetX, float targetY, float targetZ) {
		config.cameraTargetX = targetX;
		config.cameraTargetY = targetY;
		config.cameraTargetZ = targetZ;

		generator.notifyCameraConfigChange(parent, config);
	}

	public void setCameraUpDirection(float upX, float upY, float upZ) {
		config.cameraUpX = upX;
		config.cameraUpY = upY;
		config.cameraUpZ = upZ;

		generator.notifyCameraConfigChange(parent, config);
	}

	public void perspective() {
		float cameraZ = (height / 2f) / (float) Math.tan(PI * 60.0 / 360.0);

		perspective(PI / 3f, width / (float) height, cameraZ / 10f,
				cameraZ * 10f);
	}

	public void perspective(float fovy, float aspect, float zNear, float zFar) {
		float ymax = zNear * (float) Math.tan(fovy / 2);
		float ymin = -ymax;
		float xmin = ymin * aspect;
		float xmax = ymax * aspect;

		frustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	public void frustum(float left, float right, float bottom, float top,
			float near, float far) {
		config.frustumLeft = left;
		config.frustumRight = right;
		config.frustumBottom = bottom;
		config.frustumTop = top;
		config.frustumNear = near;
		config.frustumFar = far;
		config.fovy = 2 * (float) Math.atan(top / near);

		parent.frustum(left, right, bottom, top, near, far);

		generator.notifyCameraConfigChange(parent, config);
	}

	public void setCameraDivergence(float input) {
		config.cameraInput = input;
		generator.notifyCameraConfigChange(parent, config);
	}

	public String currentActivity() {
		if (frameNum == -2) {
			return "predraw";
		} else if (frameNum == -1) {
			return "postdraw";
		} else {
			return generator.getComponentFrameName(frameNum);
		}
	}

	public int getFrameNum() {
		return frameNum;
	}

	/*
	 * Drawing functions, called by Processing framework
	 */
	public void pre() {
		frameNum = -2;
		if (callPreDraw) {
			callMethod("preDraw");
		}
		parent.background(backgroundColor);

		frameNum = 0;
		generator.prepareForDraw(frameNum, parent, config);
	}

	public void draw() {
		if (generator.getComponentCount() > 1) {
			// retrieve what was just drawn and copy to pixelsAlt
			parent.loadPixels();
			System.arraycopy(parent.pixels, 0, pixelsAlt, 0, pixelCount);

			if (saveNextFrame)
				parent.saveFrame("####-" + parentClassName + "-"
						+ generator.getComponentFrameName(frameNum) + "-component.png");

			parent.background(backgroundColor);

			frameNum = 1;

			generator.prepareForDraw(frameNum, parent, config);
			parent.draw();
			parent.loadPixels();

			if (saveNextFrame)
				parent.saveFrame("####-" + parentClassName + "-"
						+ generator.getComponentFrameName(frameNum) + "-component.png");

			long startTime = System.nanoTime();

			if (saveNextFrame) {
				generator.generateCompositeFrameAndSaveComponents(
						parent.pixels, pixelsAlt, parent, parentClassName);
			} else {
				generator.generateCompositeFrame(parent.pixels, pixelsAlt);
			}

			avgGeneratorTimeMillis = 0.9f * avgGeneratorTimeMillis + 0.1f
					* (System.nanoTime() - startTime) / 1000000f;

			parent.updatePixels();

			if (saveNextFrame)
				parent.saveFrame("####-" + parentClassName + "-composite.png");
		}

		frameNum = -1;
		if (callPostDraw) {
			// call generator's post draw to put camera back the way it was.
			// Other libraries like ControlP5 needs this.
			generator.cleanup(parent, config);

			callMethod("postDraw");
		}

		if (saveNextFrame) {
			parent.saveFrame("####-" + parentClassName + "-final.png");
			saveNextFrame = false;
		}
	}

	public void keyEvent(KeyEvent e) {
		if (e.getKey() == saveFrameKey && enableSaveFrame
				&& parent.frameCount > saveFrameNum + 10) {
			saveNextFrame = true;
			saveFrameNum = parent.frameCount;
		}
	}

	/*
	 * Internal methods
	 */
	private boolean checkForMethod(String method) {
		try {
			parent.getClass().getMethod(method);
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	private void callMethod(String method) {
		try {
			Method m = parent.getClass().getMethod(method);
			m.invoke(parent, new Object[] {});
		} catch (NoSuchMethodException e) {
			System.err.println("Unexpected exception calling " + method
					+ ". Please report.");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Unexpected exception calling " + method
					+ ". Please report.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Unexpected exception calling " + method
					+ ". Please report.");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Unexpected exception calling " + method
					+ ". Please report.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Exception thrown in function " + method
					+ ". Please fix.");
			e.printStackTrace();
			parent.exit();
		}
	}
}