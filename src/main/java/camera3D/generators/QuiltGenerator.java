
package camera3D.generators;

import java.io.File;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class QuiltGenerator extends Generator implements PConstants {

  private PApplet parent;

  private PImage outputFrame;

  protected int columns;
  protected int rows;
  private int frameWidth;
  private int frameHeight;
  private int quiltWidth;

  private File saveLocation;
  private float intraFrameDivergence;

  private float cameraDivergenceX;
  private float cameraDivergenceY;
  private float cameraDivergenceZ;
  private float frustumSkew;

  public QuiltGenerator(PApplet parent) {
    this.parent = parent;

    this.frameWidth = parent.width * parent.pixelDensity;
    this.frameHeight = parent.height * parent.pixelDensity;

    this.saveLocation = null;

    setQuiltDimensions(11, 6);
  }

  public QuiltGenerator setQuiltDimensions(int columns, int rows) {
    this.columns = columns;
    this.rows = rows;

    outputFrame = null;

    this.quiltWidth = this.frameWidth * columns;
    this.intraFrameDivergence = 58f / (rows * columns);

    return this;
  }

  public QuiltGenerator setOutputLocation(String saveLocation) {
    this.saveLocation = new File(saveLocation);

    return this;
  }

  public QuiltGenerator setViewCone(float viewCone) {
    this.intraFrameDivergence = viewCone / (rows * columns);

    if (config != null && config.isReady())
      recalculateCameraSettings();

    return this;
  }

  @Override
  public int getComponentCount() {
    return columns * rows;
  }

  @Override
  public String getComponentFrameName(int frameNum) {
    return "frame_" + frameNum;
  }

  @Override
  protected void recalculateCameraSettings() {
    float dx = config.cameraPositionX - config.cameraTargetX;
    float dy = config.cameraPositionY - config.cameraTargetY;
    float dz = config.cameraPositionZ - config.cameraTargetZ;
    float diverge = -intraFrameDivergence / (config.fovy * RAD_TO_DEG);

    cameraDivergenceX = (dy * config.cameraUpZ - config.cameraUpY * dz)
        * diverge;
    cameraDivergenceY = (dz * config.cameraUpX - config.cameraUpZ * dx)
        * diverge;
    cameraDivergenceZ = (dx * config.cameraUpY - config.cameraUpX * dy)
        * diverge;

    float distanceToTarget = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    float cameraDivergenceDistance = (float) (Math.signum(intraFrameDivergence) * Math.sqrt(
        cameraDivergenceX * cameraDivergenceX
            + cameraDivergenceY * cameraDivergenceY + cameraDivergenceZ
                * cameraDivergenceZ));

    frustumSkew = cameraDivergenceDistance * config.frustumNear / distanceToTarget;
  }

  @Override
  public void prepareForDraw(int frameNum, PApplet parent) {
    float divergenceOffset = frameNum - ((rows * columns - 1) / 2.0f);

    parent.camera(config.cameraPositionX + divergenceOffset * cameraDivergenceX,
        config.cameraPositionY + divergenceOffset * cameraDivergenceY,
        config.cameraPositionZ + divergenceOffset * cameraDivergenceZ,
        config.cameraTargetX + divergenceOffset * cameraDivergenceX,
        config.cameraTargetY + divergenceOffset * cameraDivergenceY,
        config.cameraTargetZ + divergenceOffset * cameraDivergenceZ,
        config.cameraUpX, config.cameraUpY, config.cameraUpZ);

    parent.frustum(config.frustumLeft - divergenceOffset * frustumSkew,
        config.frustumRight - divergenceOffset * frustumSkew,
        config.frustumBottom, config.frustumTop,
        config.frustumNear, config.frustumFar);
  }

  @Override
  public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
    if (outputFrame == null) {
      outputFrame = parent.createImage(frameWidth * columns, frameHeight * rows, PConstants.RGB);
    }

    outputFrame.loadPixels();
    for (int ii = 0; ii < pixelStorage.length; ++ii) {
      int tileRow = ((rows - 1) - (ii / columns)) * frameHeight;
      int tileCol = (ii % columns) * frameWidth;

      for (int r = 0; r < frameHeight; ++r) {
        int srcStart = r * frameWidth;
        int destStart = (r + tileRow) * quiltWidth + tileCol;

        System.arraycopy(pixelStorage[ii], srcStart, outputFrame.pixels, destStart, frameWidth);
      }
    }

    outputFrame.updatePixels();

    // save compositeFrame to file
    String metadata = "qs" + columns + "x" + rows + "a" + ((float) frameWidth / frameHeight) + ".png";
    if (saveLocation != null) {
      String filename = insertFrame((new File(saveLocation, "frame_####_" + metadata)).getAbsolutePath(),
          parent.frameCount);
      outputFrame.save(filename);
      if (parent.frameCount == 1) {
        checkDiskSpace(parent.saveFile(filename));
      }
    }
  }

  @Override
  public void completedDraw(int frameNum, PApplet parent) {
    // do nothing
  }

  @Override
  public void cleanup(PApplet parent) {
    parent.camera(config.cameraPositionX, config.cameraPositionY,
        config.cameraPositionZ, config.cameraTargetX,
        config.cameraTargetY, config.cameraTargetZ, config.cameraUpX,
        config.cameraUpY, config.cameraUpZ);
  }

}
