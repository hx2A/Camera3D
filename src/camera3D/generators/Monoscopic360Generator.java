package camera3D.generators;

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

    public final static int SIZE_4K = 4 * 1024;
    public final static int SIZE_8K = 8 * 1024;
    private final static double PI = 3.14159265359;

    // dimensions of sketch
    private int frameWidth;
    private double halfFrameWidth;
    private int compositeSize; // width of composite frame in pixels
    private String saveLocation;

    private PImage compositeFrame;
    private int frameCount;

    private Vector cameraDirection;
    private Vector cameraUp;

    private Panel[] panels;
    private int[] arrayIndex;
    private int[] pixelMapping;

    public Monoscopic360Generator(int width, int height) {
        if (width != height) {
            throw new RuntimeException("Width must equal height.");
        }

        this.frameWidth = width;
        this.halfFrameWidth = frameWidth / 2d;
        this.compositeSize = 4 * width;
        this.saveLocation = null;

        this.frameCount = 0;

        calculatePixelMaps(); // TODO: create panels but don't calculate maps
    }

    public Monoscopic360Generator setCompositeSize(int size) {
        if (size / 2 != size / 2.0) {
            throw new RuntimeException("Size must be an even number");
        }
        this.compositeSize = size;

        // invalidate lookup tables so they are recalculated
        arrayIndex = null;
        pixelMapping = null;

        return this;
    }

    public Monoscopic360Generator setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;

        return this;
    }

    private void calculatePixelMaps() {
        panels = new Panel[getComponentCount()];
        arrayIndex = new int[compositeSize * compositeSize / 2];
        pixelMapping = new int[compositeSize * compositeSize / 2];

        // initialize the 6 panels with the correct settings
        panels[0] = new Panel(0, CameraOrientation.ABOVE);
        panels[1] = new Panel(0, CameraOrientation.FRONT);
        panels[2] = new Panel(0, CameraOrientation.REAR);
        panels[3] = new Panel(0, CameraOrientation.LEFT);
        panels[4] = new Panel(0, CameraOrientation.RIGHT);
        panels[5] = new Panel(0, CameraOrientation.BELOW);

        // precompute the sin and cos LUTs
        double[] sinThetaLUT = new double[compositeSize];
        double[] cosThetaLUT = new double[compositeSize];
        for (int x = 0; x < compositeSize; ++x) {
            double theta = -((2 * PI) * (x + 0.5) / compositeSize - PI);
            sinThetaLUT[x] = Math.sin(theta);
            cosThetaLUT[x] = Math.cos(theta);
        }
        double[] sinPhiLUT = new double[compositeSize / 2];
        double[] cosPhiLUT = new double[compositeSize / 2];
        for (int y = 0; y < compositeSize / 2; ++y) {
            double phi = (PI * (y + 0.5) / (compositeSize / 2));
            sinPhiLUT[y] = Math.sin(phi);
            cosPhiLUT[y] = Math.cos(phi);
        }

        // for each pixel in the composite frame, reverse the equirectangular
        // projection to get the spherical coordinates. Then find which
        // panel to use and the best pixel.
        int prevPanel = 0;
        for (int x = 0; x < compositeSize; ++x) {
            for (int y = 0; y < compositeSize / 2; ++y) {
                // guess the correct panel is the same as the previous pixel
                Integer index = panels[prevPanel].locate(sinThetaLUT[x],
                        cosThetaLUT[x], sinPhiLUT[y], cosPhiLUT[y]);
                if (index != null) {
                    arrayIndex[y * compositeSize + x] = prevPanel;
                    pixelMapping[y * compositeSize + x] = index;
                    continue;
                }

                // guess was incorrect...try the others
                int p;
                for (p = 0; p < panels.length; ++p) {
                    if (p == prevPanel) {
                        // already tried this one
                        continue;
                    }
                    index = panels[p].locate(sinThetaLUT[x], cosThetaLUT[x],
                            sinPhiLUT[y], cosPhiLUT[y]);
                    if (index != null) {
                        arrayIndex[y * compositeSize + x] = p;
                        pixelMapping[y * compositeSize + x] = index;
                        prevPanel = p;
                        break;
                    }
                }
                if (p == panels.length) {
                    System.out.println(String.format("panel miss: (%d, %d)", x,
                            y));
                    arrayIndex[y * compositeSize + x] = -1;
                    pixelMapping[y * compositeSize + x] = 0;
                }
            }
        }
    }

    public int getComponentCount() {
        return 6;
    }

    public String getComponentFrameName(int frameNum) {
        if (0 <= frameNum && frameNum < panels.length) {
            return panels[frameNum].getName();
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

        if (frameNum == 0) {
            compositeFrame = parent.createImage(compositeSize,
                    compositeSize / 2, PConstants.RGB);
        }

        panels[frameNum].setCamera(parent);
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        if (arrayIndex == null || pixelMapping == null) {
            calculatePixelMaps();
        }

        compositeFrame.loadPixels();
        for (int i = 0; i < compositeFrame.pixels.length; ++i) {
            if (arrayIndex[i] >= 0)
                compositeFrame.pixels[i] = pixelStorage[arrayIndex[i]][pixelMapping[i]];
        }
        compositeFrame.updatePixels();

        // save compositeFrame to file
        if (saveLocation != null) {
            compositeFrame.save(insertFrame(saveLocation, frameCount));
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

        // clear canvas and add output for displayed frame
        parent.background(0);
        parent.copy(compositeFrame, 0, 0, compositeFrame.width,
                compositeFrame.height, 0, parent.height / 4, parent.width,
                parent.height / 2);
    }

    private enum CameraOrientation {
        FRONT, LEFT, REAR, RIGHT, ABOVE, BELOW
    }

    private class Panel {

        private int id;
        private CameraOrientation orientation;

        public Panel(int id, CameraOrientation orientation) {
            this.id = id;
            this.orientation = orientation;
        }

        public String getName() {
            return orientation + "-" + id;
        }

        public Integer locate(double sinTheta, double cosTheta, double sinPhi,
                double cosPhi) {
            double polarX = -halfFrameWidth * sinPhi * sinTheta;
            double polarY = -halfFrameWidth * cosPhi;
            double polarZ = -halfFrameWidth * sinPhi * cosTheta;

            int frameX = -1;
            int frameY = -1;
            double shrinkage = 1 - 0e-3;

            switch (orientation) {
            case FRONT:
                if (polarZ < 0) {
                    double scale = -shrinkage * halfFrameWidth / polarZ;
                    frameX = (int) Math.floor(halfFrameWidth + scale * polarX);
                    frameY = (int) Math.floor(halfFrameWidth + scale * polarY);
                }
                break;
            case REAR:
                if (polarZ > 0) {
                    double scale = shrinkage * halfFrameWidth / polarZ;
                    frameX = (int) Math.floor(halfFrameWidth - scale * polarX);
                    frameY = (int) Math.floor(halfFrameWidth + scale * polarY);
                }
                break;
            case ABOVE:
                if (polarY < 0) {
                    double scale = -shrinkage * halfFrameWidth / polarY;
                    frameX = (int) Math.floor(halfFrameWidth + scale * polarX);
                    frameY = (int) Math.floor(halfFrameWidth - scale * polarZ);
                }
                break;
            case BELOW:
                if (polarY > 0) {
                    double scale = shrinkage * halfFrameWidth / polarY;
                    frameX = (int) Math.floor(halfFrameWidth + scale * polarX);
                    frameY = (int) Math.floor(halfFrameWidth + scale * polarZ);
                }
                break;
            case LEFT:
                if (polarX < 0) {
                    double scale = -shrinkage * halfFrameWidth / polarX;
                    frameX = (int) Math.floor(halfFrameWidth - scale * polarZ);
                    frameY = (int) Math.floor(halfFrameWidth + scale * polarY);
                }
                break;
            case RIGHT:
                if (polarX > 0) {
                    double scale = shrinkage * halfFrameWidth / polarX;
                    frameX = (int) Math.floor(halfFrameWidth + scale * polarZ);
                    frameY = (int) Math.floor(halfFrameWidth + scale * polarY);
                }
                break;
            default:
                break;
            }

            if (frameX < 0 || frameX >= frameWidth || frameY < 0
                    || frameY >= frameWidth) {
                return null;
            } else {
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

            parent.frustum(-config.frustumNear, config.frustumNear,
                    -config.frustumNear, config.frustumNear,
                    config.frustumNear, config.frustumFar);

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
