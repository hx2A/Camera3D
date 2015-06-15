package camera3D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.Math;

import processing.core.*;
import processing.event.KeyEvent;
import camera3D.generators.AnaglyphGenerator;
import camera3D.generators.BitMaskFilterAnaglyphGenerator;
import camera3D.generators.DuboisAnaglyphGenerator64bitLUT;
import camera3D.generators.MatrixAnaglyphGenerator;

public class Camera3D implements PConstants {

	public final static String VERSION = "##library.prettyVersion##";

	private enum Renderer {
		REGULAR, ANAGLYPH
	}

	private PApplet parent;

	private int width;
	private int height;
	private int pixelCount;
	private int[] pixelsAlt;

	private boolean debugTools;
	private boolean saveNextFrame;
	private boolean saveAllFrames;
	private int saveFrameNum;
	private String parentClassName;

	private float cameraX;
	private float cameraY;
	private float cameraZ;
	private float cameraDivergenceX;
	private float cameraDivergenceY;
	private float cameraDivergenceZ;
	private float cameraDivergence;
	private float targetX;
	private float targetY;
	private float targetZ;
	private float upX;
	private float upY;
	private float upZ;
	private float fovy;

	private Renderer renderer;
	private AnaglyphGenerator anaglyphGenerator;
	private float backgroundColor;
	private boolean callPreDraw;
	private boolean callPostDraw;
	private String currentActivity;

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

		debugTools = false;
		saveNextFrame = false;
		saveAllFrames = false;

		camera();
		perspective();
		setCameraDivergence(3);
		renderAnaglyph();

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

	public void renderAnaglyph() {
		renderAnaglyph(0xFFFF0000, 0x0000FFFF);
	}

	public void renderAnaglyph(int leftFilter, int rightFilter) {
		anaglyphGenerator = new BitMaskFilterAnaglyphGenerator(leftFilter,
				rightFilter);
		renderAnaglyph(anaglyphGenerator);
	}

	public void renderDuboisRedCyanAnaglyph() {
		renderAnaglyph(DuboisAnaglyphGenerator64bitLUT.createRedCyanGenerator());
	}

	public void renderDuboisMagentaGreenAnaglyph() {
		renderAnaglyph(DuboisAnaglyphGenerator64bitLUT
				.createMagentaGreenGenerator());
	}

	public void renderDuboisAmberBlueAnaglyph() {
		renderAnaglyph(DuboisAnaglyphGenerator64bitLUT
				.createAmberBlueGenerator());
	}

	public void renderTrueAnaglyph() {
		renderAnaglyph(MatrixAnaglyphGenerator.createTrueAnaglyphGenerator());
	}

	public void renderGrayAnaglyph() {
		renderAnaglyph(MatrixAnaglyphGenerator.createGrayAnaglyphGenerator());
	}

	public void renderHalfColorAnaglyph() {
		renderAnaglyph(MatrixAnaglyphGenerator
				.createHalfColorAnaglyphGenerator());
	}

	public void renderAnaglyph(AnaglyphGenerator anaglyphGenerator) {
		this.anaglyphGenerator = anaglyphGenerator;
		renderer = Renderer.ANAGLYPH;
	}

	public void renderStandard() {
		renderer = Renderer.REGULAR;
		setCameraDivergence(0);
	}

	public void enableDebugTools() {
		debugTools = true;
	}

	public void saveAllFrames() {
		saveAllFrames = true;
	}

	/*
	 * Camera Methods
	 */
	public void camera() {
		camera(width / 2f, height / 2f,
				(height / 2f) / (float) Math.tan(PI * 30.0 / 180.0),
				width / 2f, height / 2f, 0, 0, 1, 0);

		recalculateDivergence();
	}

	public void camera(float cameraX, float cameraY, float cameraZ,
			float targetX, float targetY, float targetZ, float upX, float upY,
			float upZ) {
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;

		recalculateDivergence();
	}

	public void setCameraLocation(float cameraX, float cameraY, float cameraZ) {
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;

		recalculateDivergence();
	}

	public void setCameraTarget(float targetX, float targetY, float targetZ) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;

		recalculateDivergence();
	}

	public void setCameraUpDirection(float upX, float upY, float upZ) {
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;

		recalculateDivergence();
	}

	public void perspective() {
		this.fovy = PI / 3;
		parent.perspective();

		recalculateDivergence();
	}

	public void perspective(float fovy, float aspect, float zNear, float zFar) {
		this.fovy = fovy;
		parent.perspective(fovy, aspect, zNear, zFar);

		recalculateDivergence();
	}

	public void setCameraDivergence(float divergence) {
		cameraDivergence = divergence;

		recalculateDivergence();
	}

	private void recalculateDivergence() {
		float dx = cameraX - targetX;
		float dy = cameraY - targetY;
		float dz = cameraZ - targetZ;
		float diverge = -cameraDivergence / (fovy * RAD_TO_DEG);

		cameraDivergenceX = (dy * upZ - upY * dz) * diverge;
		cameraDivergenceY = (dz * upX - upZ * dx) * diverge;
		cameraDivergenceZ = (dx * upY - upX * dy) * diverge;
	}

	public String currentActivity() {
		return currentActivity;
	}

	/*
	 * Drawing functions, called by Processing framework
	 */
	public void pre() {
		currentActivity = "predraw";
		if (callPreDraw) {
			callMethod("preDraw");
		}
		parent.background(backgroundColor);

		if (renderer == Renderer.ANAGLYPH) {
			currentActivity = "right";
		} else {
			currentActivity = "regular";
		}

		parent.camera(cameraX + cameraDivergenceX, cameraY + cameraDivergenceY,
				cameraZ + cameraDivergenceZ, targetX, targetY, targetZ, upX,
				upY, upZ);
	}

	public void draw() {
		if (renderer == Renderer.ANAGLYPH) {
			// retrieve what was just drawn and copy to pixelsAlt
			parent.loadPixels();
			System.arraycopy(parent.pixels, 0, pixelsAlt, 0, pixelCount);

			if (saveNextFrame || saveAllFrames)
				parent.saveFrame("####-" + parentClassName + "-right.png");

			parent.background(backgroundColor);

			currentActivity = "left";

			parent.camera(cameraX - cameraDivergenceX, cameraY
					- cameraDivergenceY, cameraZ - cameraDivergenceZ, targetX,
					targetY, targetZ, upX, upY, upZ);

			parent.draw();
			parent.loadPixels();

			if (saveNextFrame || saveAllFrames)
				parent.saveFrame("####-" + parentClassName + "-left.png");

			anaglyphGenerator.generateAnaglyph(parent.pixels, pixelsAlt);

			parent.updatePixels();

			if (saveNextFrame || saveAllFrames)
				parent.saveFrame("####-" + parentClassName + "-anaglyph.png");
		}

		currentActivity = "postdraw";
		if (callPostDraw) {
			// put the camera back. ControlP5 needs this.
			parent.camera(cameraX, cameraY, cameraZ, targetX, targetY, targetZ,
					upX, upY, upZ);

			callMethod("postDraw");
		}

		if (saveNextFrame || saveAllFrames) {
			parent.saveFrame("####-" + parentClassName + "-final.png");
			saveNextFrame = false;
		}
	}

	public void keyEvent(KeyEvent e) {
		if (e.getKey() == 's' && debugTools
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
			Method m = parent.getClass().getMethod(method);
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