package camera3D.generators;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * 
 * Monoscopic 360 Video Generator
 * 
 * @author James Schmitz
 * 
 */

public class Monoscopic360Generator extends Generator {

    private final static double PI = 3.14159265359;

    private int frameWidth;
    private int frameHeight;

    private PImage projectionFrame;
    private int projectionWidth;
    private int projectionHeight;
    private float zNear;
    private float zFar;
    private int panelXSteps;
    private int panelYSteps;
    private double widthOffset;
    private double heightOffset;
    private String saveFilename;
    private String panelExplainPlanLocation;
    private boolean displayCompositeFrame;
    private int[] emptyPixelArray;

    private Vector cameraDirection;
    private Vector cameraUp;

    private ArrayList<Panel> panels;
    private int[] arrayIndex;
    private int[] pixelMapping;
    private int frameCount;

    public Monoscopic360Generator(int width, int height) {
        this.frameWidth = width;
        this.frameHeight = height;
        this.projectionWidth = 3 * width;
        this.projectionHeight = 3 * height;
        this.zNear = 1;
        this.zFar = 1000;
        this.panelXSteps = 1;
        this.panelYSteps = 1;
        this.widthOffset = 0d;
        this.heightOffset = 0d;

        if (projectionWidth / (float) projectionHeight < 2.0) {
            widthOffset = (2 * projectionHeight - projectionWidth) / 2.0;
        } else if (projectionWidth / (float) projectionHeight > 2.0) {
            heightOffset = (projectionWidth / 2.0 - projectionHeight) / 2.0;
        }

        this.saveFilename = null;
        this.panelExplainPlanLocation = null;

        this.frameCount = 0;
        this.displayCompositeFrame = true;
        this.emptyPixelArray = new int[width * height];

        initPanels();
    }

    public Monoscopic360Generator setOutputSizeAndLocation(int size,
            String saveLocation) {
        this.projectionWidth = size;
        this.projectionHeight = size / 2;
        this.widthOffset = 0;
        this.heightOffset = 0;

        this.saveFilename = saveLocation;

        initPanels();

        return this;
    }

    public Monoscopic360Generator setOutputWidthHeightAndLocation(int width,
            int height, String saveLocation) {
        this.projectionWidth = width;
        this.projectionHeight = height;
        this.widthOffset = 0d;
        this.heightOffset = 0d;

        if (projectionWidth / (float) projectionHeight < 2.0) {
            widthOffset = (2 * projectionHeight - projectionWidth) / 2.0;
        } else if (projectionWidth / (float) projectionHeight > 2.0) {
            heightOffset = (projectionWidth / 2.0 - projectionHeight) / 2.0;
        }

        this.saveFilename = saveLocation;

        initPanels();

        return this;
    }

    public Monoscopic360Generator setPanelXYSteps(int panelXSteps,
            int panelYSteps) {
        if (panelXSteps < 1 || panelYSteps < 1) {
            throw new RuntimeException(
                    "Panel step sizes must be greater than or equal to 1.");
        }

        this.panelXSteps = panelXSteps;
        this.panelYSteps = panelYSteps;

        return this;
    }

    public Monoscopic360Generator setNearFarLimits(float near, float far) {
        if (near < 0 || far < 0 || near > far) {
            throw new RuntimeException(
                    "Near and far must both be positive with near < far.");
        }

        this.zNear = near;
        this.zFar = far;

        return this;
    }

    public Monoscopic360Generator setPanelExplainPlanLocation(
            String panelExplainPlanLocation) {
        this.panelExplainPlanLocation = panelExplainPlanLocation;

        return this;
    }

    public Monoscopic360Generator skipDisplayingCompositeFrame() {
        this.displayCompositeFrame = false;

        return this;
    }

    public Monoscopic360Generator setThreadCount(int threadCount) {
        initExecutor(threadCount);

        return this;
    }

    private void initPanels() {
        // initialize the panels with the correct settings
        panels = new ArrayList<Panel>();

        for (int i = 0; i < panelXSteps; ++i) {
            float startX = i / (float) panelXSteps;
            float endX = (i + 1) / (float) panelXSteps;
            for (int j = 0; j < panelYSteps; ++j) {
                float startY = j / (float) panelYSteps;
                float endY = (j + 1) / (float) panelYSteps;
                int id = i * panelYSteps + j;

                panels.add(new Panel(id, CameraOrientation.ABOVE, startX, endX,
                        startY, endY));
                panels.add(new Panel(id, CameraOrientation.FRONT, startX, endX,
                        startY, endY));
                panels.add(new Panel(id, CameraOrientation.RIGHT, startX, endX,
                        startY, endY));
                panels.add(new Panel(id, CameraOrientation.REAR, startX, endX,
                        startY, endY));
                panels.add(new Panel(id, CameraOrientation.LEFT, startX, endX,
                        startY, endY));
                panels.add(new Panel(id, CameraOrientation.BELOW, startX, endX,
                        startY, endY));
            }
        }

        // invalidate lookup tables so they are recalculated
        arrayIndex = null;
        pixelMapping = null;
    }

    private void calculatePixelMaps(PApplet parent) {
        // print out a helpful suggestion for improving performance
        float optimalSize = 2 / (float) Math.tan(2 * PI
                / (2 * widthOffset + projectionWidth));
        String suggestion = null;
        if (frameWidth * panelXSteps < 0.8 * optimalSize) {
            suggestion = "width";
        }
        if (frameHeight * panelYSteps < 0.8 * optimalSize) {
            if (suggestion == null) {
                suggestion = "height";
            } else {
                suggestion = "width and height";
            }
        }
        if (suggestion != null) {
            System.out.printf("If your screen and computer allows it, please"
                    + " consider increasing your sketch %s.\n", suggestion);
            System.out
                    .println("You can also add more rendering panels with setPanelXYSteps().");
            System.out
                    .println("Consult online documentation for 360 resolution best practices.");
        }

        // precompute the sin and cos LUTs
        double[] sinThetaLUT = new double[projectionWidth];
        double[] cosThetaLUT = new double[projectionWidth];
        for (int x = 0; x < projectionWidth; ++x) {
            double theta = -((2 * PI) * (x + widthOffset + 0.5)
                    / (2 * widthOffset + projectionWidth) - PI);
            sinThetaLUT[x] = Math.sin(theta);
            cosThetaLUT[x] = Math.cos(theta);
        }
        double[] sinPhiLUT = new double[projectionHeight];
        double[] cosPhiLUT = new double[projectionHeight];
        for (int y = 0; y < projectionHeight; ++y) {
            double phi = (PI * (y + heightOffset + 0.5) / (2 * heightOffset + projectionHeight));
            sinPhiLUT[y] = Math.sin(phi);
            cosPhiLUT[y] = Math.cos(phi);
        }

        arrayIndex = new int[projectionWidth * projectionHeight];
        pixelMapping = new int[projectionWidth * projectionHeight];

        // for each pixel in the composite frame, reverse the equirectangular
        // projection to get the spherical coordinates. Then find which
        // panel to use and the best pixel.
        int prevPanel = 0;
        for (int x = 0; x < projectionWidth; ++x) {
            for (int y = 0; y < projectionHeight; ++y) {
                // guess the correct panel is the same as the previous pixel
                Integer index = panels.get(prevPanel).locate(sinThetaLUT[x],
                        cosThetaLUT[x], sinPhiLUT[y], cosPhiLUT[y]);
                if (index != null) {
                    arrayIndex[y * projectionWidth + x] = prevPanel;
                    pixelMapping[y * projectionWidth + x] = index;
                    continue;
                }

                // guess was incorrect...try the others
                int p;
                for (p = 0; p < panels.size(); ++p) {
                    if (p == prevPanel) {
                        // already tried this one
                        continue;
                    }
                    index = panels.get(p).locate(sinThetaLUT[x],
                            cosThetaLUT[x], sinPhiLUT[y], cosPhiLUT[y]);
                    if (index != null) {
                        arrayIndex[y * projectionWidth + x] = p;
                        pixelMapping[y * projectionWidth + x] = index;
                        prevPanel = p;
                        break;
                    }
                }
                if (p == panels.size()) {
                    System.out
                            .println(String
                                    .format("panel miss: (%d, %d) please report bug with sketch and projection size information",
                                            x, y));
                    arrayIndex[y * projectionWidth + x] = -1;
                    pixelMapping[y * projectionWidth + x] = 0;
                }
            }
        }

        // Remove unused panels. But first, adjust the arrayIndex values to
        // compensate for the missing panel
        int removals = 0;
        for (int i = 0; i < panels.size(); ++i) {
            if (panels.get(i).unused()) {
                for (int j = 0; j < arrayIndex.length; ++j) {
                    if (arrayIndex[j] + removals > i) {
                        arrayIndex[j]--;
                    }
                }
                removals++;
            }
        }
        panels.removeIf(Panel::unused);
        System.out.printf("There are %d panels.\n", panels.size());

        if (panelExplainPlanLocation != null) {
            PImage arrayIndexFrame = parent.createImage(projectionWidth,
                    projectionHeight, PConstants.RGB);
            arrayIndexFrame.loadPixels();
            for (int i = 0; i < arrayIndex.length; ++i) {
                if (arrayIndex[i] < 0) {
                    arrayIndexFrame.pixels[i] = 0xFF000000;
                } else {
                    int value = 255 * (1 + panels.get(arrayIndex[i]).getId())
                            / (1 + panelXSteps * panelYSteps);
                    int orientationOrdinal = panels.get(arrayIndex[i])
                            .getOrientationOrdinal();
                    arrayIndexFrame.pixels[i] = 0xFF000000
                            | (value << (8 * (orientationOrdinal / 2)))
                            | (value << (8 * (((orientationOrdinal + 1) / 2) % 3)));
                }
            }
            arrayIndexFrame.updatePixels();
            arrayIndexFrame.save(panelExplainPlanLocation);
        }
    }

    public int getComponentCount() {
        return panels.size();
    }

    public String getComponentFrameName(int frameNum) {
        if (0 <= frameNum && frameNum < panels.size()) {
            return panels.get(frameNum).getName();
        } else {
            return "";
        }
    }

    protected void recalculateCameraSettings() {
        cameraDirection = new Vector(
                (float) (config.cameraTargetX - config.cameraPositionX),
                (float) (config.cameraTargetY - config.cameraPositionY),
                (float) (config.cameraTargetZ - config.cameraPositionZ));
        cameraUp = new Vector((float) config.cameraUpX,
                (float) config.cameraUpY, (float) config.cameraUpZ);
    }

    public void prepareForDraw(int frameNum, PApplet parent) {
        frameCount = parent.frameCount;

        if (frameNum == 0 && (projectionFrame == null || displayCompositeFrame)) {
            projectionFrame = parent.createImage(projectionWidth,
                    projectionHeight, PConstants.RGB);
        }

        panels.get(frameNum).setCamera(parent);

        if (arrayIndex == null || pixelMapping == null) {
            calculatePixelMaps(parent);
        }
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        projectionFrame.loadPixels();
        executeTask(
                projectionFrame.pixels.length,
                (int start, int end) -> {
                    for (int ii = start; ii < end; ++ii) {
                        if (arrayIndex[ii] >= 0) {
                            projectionFrame.pixels[ii] = pixelStorage[arrayIndex[ii]][pixelMapping[ii]];
                        }
                    }
                });
        projectionFrame.updatePixels();

        // save compositeFrame to file
        if (saveFilename != null) {
            String filename = insertFrame(saveFilename, frameCount);
            projectionFrame.save(filename);
        }

        System.arraycopy(emptyPixelArray, 0, pixelDest, 0, pixelDest.length);
        if (displayCompositeFrame) {
            // compare aspect ratios and copy to be centered in the frame
            if (projectionWidth / (float) projectionHeight > frameWidth
                    / (float) frameHeight) {
                // This resize operation means the PImage needs to be recreated
                // for the next projection frame
                projectionFrame.resize(frameWidth, 0);
                int offset = (frameHeight - projectionFrame.height) / 2;
                System.arraycopy(projectionFrame.pixels, 0, pixelDest, offset
                        * frameWidth, projectionFrame.pixels.length);
            } else {
                // This resize operation means the PImage needs to be recreated
                // for the next projection frame
                projectionFrame.resize(0, frameHeight);
                int offset = (frameWidth - projectionFrame.width) / 2;
                for (int i = 0; i < frameHeight; ++i) {
                    System.arraycopy(projectionFrame.pixels, i
                            * projectionFrame.width, pixelDest, i * frameWidth
                            + offset, projectionFrame.width);
                }
            }
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

        if (saveFilename != null && frameCount == 1) {
            checkDiskSpace(parent, insertFrame(saveFilename, frameCount));
        }
    }

    private enum CameraOrientation {
        ABOVE, FRONT, RIGHT, REAR, LEFT, BELOW
    }

    private void checkDiskSpace(PApplet parent, String filename) {
        File file = new File(filename);

        if (!file.isAbsolute()) {
            file = parent.saveFile(filename);
        }

        File dir = file.getAbsoluteFile().getParentFile();

        long mb = (long) Math.pow(2, 20);
        long kb = (long) Math.pow(2, 10);
        double gb = Math.pow(2, 30);
        long filesize = file.length();
        long usablespace = dir.getUsableSpace();

        System.out.println("Saving frames to directory "
                + dir.getAbsolutePath());
        System.out.printf("Available space on that drive: %.2fGB\n",
                usablespace / gb);
        if (filesize / mb > 0) {
            System.out.printf("Saving each frame takes about %dMB\n", filesize
                    / mb);
        } else {
            System.out.printf("Saving each frame takes about %dKB\n", filesize
                    / kb);
        }

        if (config.frameLimit > 0) {
            long totalBytes = filesize * config.frameLimit;
            System.out.printf("Saving %d frames will take %.2fGB\n",
                    config.frameLimit, totalBytes / gb);
            if (totalBytes > usablespace) {
                throw new RuntimeException(
                        "Not enough disk space to save requested frames!");
            }
        } else {
            long totalFrames = usablespace / filesize;
            System.out.printf("Available space for about %d frames\n",
                    totalFrames);
        }
    }

    private class Panel {

        private int id;
        private CameraOrientation orientation;
        private boolean used;
        private double startPanelX;
        private double endPanelX;
        private double startPanelY;
        private double endPanelY;

        public Panel(int id, CameraOrientation orientation, double startPanelX,
                double endPanelX, double startPanelY, double endPanelY) {
            this.id = id;
            this.orientation = orientation;
            this.used = false;
            this.startPanelX = startPanelX;
            this.endPanelX = endPanelX;
            this.startPanelY = startPanelY;
            this.endPanelY = endPanelY;
        }

        public String getName() {
            return orientation + "-" + id;
        }

        public boolean unused() {
            return !used;
        }

        public int getId() {
            return id;
        }

        public int getOrientationOrdinal() {
            return orientation.ordinal();
        }

        public Integer locate(double sinTheta, double cosTheta, double sinPhi,
                double cosPhi) {
            double polarX = -0.5 * sinPhi * sinTheta;
            double polarY = -0.5 * cosPhi;
            double polarZ = -0.5 * sinPhi * cosTheta;

            double panelX = -1;
            double panelY = -1;

            switch (orientation) {
            case FRONT:
                if (polarZ < 0) {
                    double scale = -0.5 / polarZ;
                    panelX = 0.5 + scale * polarX;
                    panelY = 0.5 + scale * polarY;
                }
                break;
            case REAR:
                if (polarZ > 0) {
                    double scale = 0.5 / polarZ;
                    panelX = 0.5 - scale * polarX;
                    panelY = 0.5 + scale * polarY;
                }
                break;
            case ABOVE:
                if (polarY < 0) {
                    double scale = -0.5 / polarY;
                    panelX = 0.5 + scale * polarX;
                    panelY = 0.5 - scale * polarZ;
                }
                break;
            case BELOW:
                if (polarY > 0) {
                    double scale = 0.5 / polarY;
                    panelX = 0.5 + scale * polarX;
                    panelY = 0.5 + scale * polarZ;
                }
                break;
            case LEFT:
                if (polarX < 0) {
                    double scale = -0.5 / polarX;
                    panelX = 0.5 - scale * polarZ;
                    panelY = 0.5 + scale * polarY;
                }
                break;
            case RIGHT:
                if (polarX > 0) {
                    double scale = 0.5 / polarX;
                    panelX = 0.5 + scale * polarZ;
                    panelY = 0.5 + scale * polarY;
                }
                break;
            default:
                break;
            }

            int frameX = (int) Math.floor(frameWidth * (panelX - startPanelX)
                    / (endPanelX - startPanelX));
            int frameY = (int) Math.floor(frameHeight * (panelY - startPanelY)
                    / (endPanelY - startPanelY));

            if (frameX < 0 || frameX >= frameWidth || frameY < 0
                    || frameY >= frameHeight) {
                return null;
            } else {
                used = true;
                return frameY * frameWidth + frameX;
            }
        }

        public void setCamera(PApplet parent) {
            Vector direction;
            Vector up;

            switch (orientation) {
            case ABOVE:
                direction = cameraUp.mult(cameraDirection.magnitude()).mult(-1);
                up = cameraDirection.normalized();
                break;
            case LEFT:
                direction = cameraUp.cross(cameraDirection);
                up = cameraUp;
                break;
            case RIGHT:
                direction = cameraDirection.cross(cameraUp);
                up = cameraUp;
                break;
            case REAR:
                direction = cameraDirection.mult(-1);
                up = cameraUp;
                break;
            case BELOW:
                direction = cameraUp.mult(cameraDirection.magnitude());
                up = cameraDirection.normalized().mult(-1);
                break;
            case FRONT:
                direction = cameraDirection;
                up = cameraUp;
                break;
            default:
                throw new RuntimeException("Unknown Orientation");
            }

            parent.camera(config.cameraPositionX, config.cameraPositionY,
                    config.cameraPositionZ, config.cameraPositionX
                            + direction.v1, config.cameraPositionY
                            + direction.v2, config.cameraPositionZ
                            + direction.v3, up.v1, up.v2, up.v3);

            float frustumLeft = (float) (zNear * (2 * startPanelX - 1));
            float frustumRight = (float) (zNear * (2 * endPanelX - 1));
            float frustumBottom = (float) (zNear * (1 - 2 * endPanelY));
            float frustumTop = (float) (zNear * (1 - 2 * startPanelY));

            parent.frustum(frustumLeft, frustumRight, frustumBottom,
                    frustumTop, zNear, zFar);
        }
    }

    private class Vector {

        public float v1;
        public float v2;
        public float v3;

        public Vector(float v1, float v2, float v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

        public Vector mult(float c) {
            return new Vector(c * v1, c * v2, c * v3);
        }

        public float magnitude() {
            return (float) Math.sqrt(v1 * v1 + v2 * v2 + v3 * v3);
        }

        public Vector normalized() {
            return mult(1 / magnitude());
        }

        public Vector cross(Vector other) {
            return new Vector(v2 * other.v3 - v3 * other.v2, v3 * other.v1 - v1
                    * other.v3, v1 * other.v2 - v2 * other.v1);
        }
    }
}
