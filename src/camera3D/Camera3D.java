package camera3D;

import java.io.File;
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
    private int[][] pixelStorage;
    private float avgGeneratorTimeMillis;
    private float avgDrawTimeMillis;

    private String saveFrameLocation;
    private boolean enableSaveFrame;
    private boolean saveNextFrame;
    private char saveFrameKey;
    private int saveFrameNum;
    private boolean reportStats;
    private String parentClassName;

    private CameraConfiguration config;

    private Generator generator;
    private int backgroundColor;
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

        this.backgroundColor = 0xFFFFFFFF;
        this.callPreDraw = checkForMethod("preDraw");
        this.callPostDraw = checkForMethod("postDraw");

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);
        parent.registerMethod("keyEvent", this);

        width = parent.width;
        height = parent.height;
        pixelCount = parent.width * parent.height;
        pixelStorage = new int[1][pixelCount];
        avgGeneratorTimeMillis = 1;

        enableSaveFrame = false;
        saveNextFrame = false;
        reportStats = false;

        renderRegular();

        config = new CameraConfiguration();
        camera();
        perspective();

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
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public StereoscopicGenerator renderDefaultAnaglyph() {
        return renderBitMaskRedCyanAnaglyph();
    }

    public StereoscopicGenerator renderBitMaskRedCyanAnaglyph() {
        return renderBitMaskFilterAnaglyph(0xFFFF0000, 0x0000FFFF);
    }

    public StereoscopicGenerator renderBitMaskMagentaGreenAnaglyph() {
        return renderBitMaskFilterAnaglyph(0xFFFF00FF, 0x0000FF00);
    }

    public StereoscopicGenerator renderBitMaskFilterAnaglyph(int leftFilter,
            int rightFilter) {
        StereoscopicGenerator generator = new BitMaskFilterAnaglyphGenerator(
                leftFilter, rightFilter);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderDuboisRedCyanAnaglyph() {
        StereoscopicGenerator generator = DuboisAnaglyphGenerator64bitLUT
                .createRedCyanGenerator();

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderDuboisMagentaGreenAnaglyph() {
        StereoscopicGenerator generator = DuboisAnaglyphGenerator64bitLUT
                .createMagentaGreenGenerator();

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderDuboisAmberBlueAnaglyph() {
        StereoscopicGenerator generator = DuboisAnaglyphGenerator64bitLUT
                .createAmberBlueGenerator();

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderTrueAnaglyph() {
        StereoscopicGenerator generator = MatrixAnaglyphGeneratorLUT
                .createTrueAnaglyphGenerator();

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderGrayAnaglyph() {
        StereoscopicGenerator generator = MatrixAnaglyphGeneratorLUT
                .createGrayAnaglyphGenerator();

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderHalfColorAnaglyph() {
        StereoscopicGenerator generator = MatrixAnaglyphGeneratorLUT
                .createHalfColorAnaglyphGenerator();

        setGenerator(generator);

        return generator;
    }

    public BarrelDistortionGenerator renderBarrelDistortion() {
        BarrelDistortionGenerator generator = new BarrelDistortionGenerator(
                parent.width, parent.height);

        setGenerator(generator);

        return generator;
    }

    public Monoscopic360Generator renderMonoscopic360() {
        Monoscopic360Generator generator = new Monoscopic360Generator(
                parent.width, parent.height);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderSplitFrameSideBySide() {
        StereoscopicGenerator generator = new SplitFrameGenerator(parent.width,
                parent.height, SplitFrameGenerator.SIDE_BY_SIDE);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderSplitFrameOverUnder() {
        StereoscopicGenerator generator = new SplitFrameGenerator(parent.width,
                parent.height, SplitFrameGenerator.OVER_UNDER);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderSplitFrameOverUnderHalfHeight() {
        StereoscopicGenerator generator = new SplitFrameGenerator(parent.width,
                parent.height, SplitFrameGenerator.OVER_UNDER_HALF_HEIGHT);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderSplitFrameSideBySideHalfWidth() {
        StereoscopicGenerator generator = new SplitFrameGenerator(parent.width,
                parent.height, SplitFrameGenerator.SIDE_BY_SIDE_HALF_WIDTH);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator renderInterlaced() {
        StereoscopicGenerator generator = new SplitFrameGenerator(parent.width,
                parent.height, SplitFrameGenerator.INTERLACED);

        setGenerator(generator);

        return generator;
    }

    public SplitDepthGenerator renderSplitDepthIllusion() {
        SplitDepthGenerator generator = new SplitDepthGenerator(parent.width,
                parent.height);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator stereoscopicSequentialFrameSaver(
            String filename) {
        StereoscopicFrameSaver generator = new StereoscopicFrameSaver(filename);

        setGenerator(generator);

        return generator;
    }

    public StereoscopicGenerator stereoscopicLeftRightFrameSaver(
            String leftFilename, String rightFilename) {
        StereoscopicFrameSaver generator = new StereoscopicFrameSaver(
                leftFilename, rightFilename);

        setGenerator(generator);

        return generator;
    }

    public RegularRenderer renderRegular() {
        RegularRenderer generator = new RegularRenderer();

        setGenerator(generator);

        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;

        pixelStorage = new int[generator.getComponentCount()][pixelCount];

        avgGeneratorTimeMillis = 1;

        generator.notifyCameraConfigChange(config);
    }

    public Generator getGenerator() {
        return generator;
    }

    public void enableSaveFrame(char key, String saveFrameLocation) {
        saveFrameKey = key;

        if (!saveFrameLocation.endsWith(File.separator)) {
            saveFrameLocation += File.separator;
        }
        this.saveFrameLocation = saveFrameLocation;

        enableSaveFrame = true;
    }

    public void enableSaveFrame(String saveFrameLocation) {
        enableSaveFrame('s', saveFrameLocation);
    }

    public void enableSaveFrame(char key) {
        enableSaveFrame(key, "frames");
    }

    public void enableSaveFrame() {
        enableSaveFrame('s');
    }

    public float getGeneratorTime() {
        return avgGeneratorTimeMillis;
    }

    public float getDrawTime() {
        return avgDrawTimeMillis;
    }

    public void reportStats() {
        reportStats = true;
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

        generator.notifyCameraConfigChange(config);
    }

    public void setFrameLimit(int frameLimit) {
        config.frameLimit = frameLimit;
    }

    public void setCameraLocation(float cameraX, float cameraY, float cameraZ) {
        config.cameraPositionX = cameraX;
        config.cameraPositionY = cameraY;
        config.cameraPositionZ = cameraZ;

        generator.notifyCameraConfigChange(config);
    }

    public void setCameraTarget(float targetX, float targetY, float targetZ) {
        config.cameraTargetX = targetX;
        config.cameraTargetY = targetY;
        config.cameraTargetZ = targetZ;

        generator.notifyCameraConfigChange(config);
    }

    public void setCameraUpDirection(float upX, float upY, float upZ) {
        config.cameraUpX = upX;
        config.cameraUpY = upY;
        config.cameraUpZ = upZ;

        generator.notifyCameraConfigChange(config);
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

        generator.notifyCameraConfigChange(config);
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
     * 
     * The pre() and draw() methods are where all the action is in this class.
     * The rest is mainly configuration code.
     */
    public void pre() {
        frameNum = -2;
        if (callPreDraw) {
            callMethod("preDraw");
        }

        // generator might have changed the number of components
        if (pixelStorage.length != generator.getComponentCount()) {
            pixelStorage = new int[generator.getComponentCount()][pixelCount];
        }

        parent.background(backgroundColor);

        frameNum = 0;
        generator.prepareForDraw(frameNum, parent);
    }

    public void draw() {
        generator.completedDraw(frameNum, parent);

        // retrieve what was just drawn and copy to pixelStorage
        if (generator.copyFrameNumber(0)) {
            parent.loadPixels();
            System.arraycopy(parent.pixels, 0, pixelStorage[0], 0, pixelCount);
        }

        if (saveNextFrame)
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-" + generator.getComponentFrameName(frameNum)
                    + "-component.png");

        // now loop through the draw cycle repeatedly, filling pixel storage.
        for (frameNum = 1; frameNum < generator.getComponentCount(); ++frameNum) {
            parent.background(backgroundColor);
            generator.prepareForDraw(frameNum, parent);

            long drawStartTime = System.nanoTime();

            parent.draw();

            avgDrawTimeMillis = 0.9f * avgGeneratorTimeMillis + 0.1f
                    * (System.nanoTime() - drawStartTime) / 1000000f;

            generator.completedDraw(frameNum, parent);

            if (generator.copyFrameNumber(frameNum)) {
                parent.loadPixels();
                System.arraycopy(parent.pixels, 0, pixelStorage[frameNum], 0,
                        pixelCount);
            }

            if (saveNextFrame)
                parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                        + "-" + generator.getComponentFrameName(frameNum)
                        + "-component.png");
        }

        // create composite frame
        long generateStartTime = System.nanoTime();

        if (saveNextFrame) {
            generator.generateCompositeFrameAndSaveComponents(parent.pixels,
                    pixelStorage, parent, parentClassName, saveFrameLocation);
        } else {
            generator.generateCompositeFrame(parent.pixels, pixelStorage);
        }

        avgGeneratorTimeMillis = 0.9f * avgGeneratorTimeMillis + 0.1f
                * (System.nanoTime() - generateStartTime) / 1000000f;

        parent.updatePixels();

        if (saveNextFrame)
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-composite.png");

        // call generator's cleanup method to put camera back the way it was.
        // Other libraries like ControlP5 needs this.
        generator.cleanup(parent);

        if (callPostDraw) {
            frameNum = -1;
            callMethod("postDraw");
        }

        if (saveNextFrame) {
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-final.png");
            saveNextFrame = false;
        }

        if (reportStats) {
            if (generator.getComponentCount() == 1) {
                System.out
                        .printf("%05d | Frame Rate: %.2f frames/sec | Generator Render Time: %.3f ms\n",
                                parent.frameCount, parent.frameRate,
                                avgGeneratorTimeMillis);
            } else {
                System.out
                        .printf("%05d | Frame Rate: %.2f frames/sec | draw() time: %.3f ms | Generator Render Time: %.3f ms\n",
                                parent.frameCount, parent.frameRate,
                                avgDrawTimeMillis, avgGeneratorTimeMillis);
            }
        }

        if (config.frameLimit > 0 && parent.frameCount >= config.frameLimit) {
            System.out
                    .println("***** Camera3D exiting sketch as requested *****");
            System.out.flush();
            parent.exit();
        }
    }

    public void keyEvent(KeyEvent e) {
        // the saveFrameNum thing below is to keep the program from saving many
        // frames in a row
        // if the user is too slow to lift their finger off the keyboard.
        if (e.getKey() == saveFrameKey && enableSaveFrame
                && parent.frameCount > saveFrameNum + 10) {
            saveNextFrame = true;
            saveFrameNum = parent.frameCount;
        }
    }

    /*
     * Internal reflective methods for examining sketch.
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