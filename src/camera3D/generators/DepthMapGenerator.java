package camera3D.generators;

import java.io.IOException;
import java.net.URL;

import processing.core.PApplet;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;
import processing.opengl.PShader;

public class DepthMapGenerator extends Generator {

  static protected float DEFAULT_NEAR = 500;
  static protected float DEFAULT_FAR = 1000;

  private PApplet parent;

  static protected URL defPointShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/pointvert.glsl");
  static protected URL defPointShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/pointfrag.glsl");
  static protected URL defLineShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/linevert.glsl");
  static protected URL defLineShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/linefrag.glsl");

  static protected URL defColorShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/colorvert.glsl");
  static protected URL defColorShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/colorfrag.glsl");
  static protected URL defTextureShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texvert.glsl");
  static protected URL defTextureShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texfrag.glsl");
  static protected URL defLightShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/lightvert.glsl");
  static protected URL defLightShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/lightfrag.glsl");
  static protected URL defTexlightShaderVertURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texlightvert.glsl");
  static protected URL defTexlightShaderFragURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texlightfrag.glsl");

  // extra shaders for Raspberry Pis
  static protected URL defLightShaderVertBrcmURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/lightvert-brcm.glsl");
  static protected URL defLightShaderVertVc4URL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/lightvert-vc4.glsl");
  static protected URL defTexlightShaderVertBrcmURL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texlightvert-brcm.glsl");
  static protected URL defTexlightShaderVertVc4URL = DepthMapGenerator.class
      .getResource("/data/shaders/depthmap/texlightvert-vc4.glsl");

  protected PShader pointShader;
  protected PShader lineShader;
  protected PShader colorShader;
  protected PShader textureShader;
  protected PShader lightShader;
  protected PShader texlightShader;

  public DepthMapGenerator(PApplet parent) {
    this.parent = parent;
    initShaders();
  }

  @Override
  public int getComponentCount() {
    return 1;
  }

  @Override
  public String getComponentFrameName(int frameNum) {
    return "main";
  }

  @Override
  protected void recalculateCameraSettings() {
  }

  @Override
  public void prepareForDraw(int frameNum, PApplet parent) {
    parent.camera(config.cameraPositionX, config.cameraPositionY, config.cameraPositionZ, config.cameraTargetX,
        config.cameraTargetY, config.cameraTargetZ, config.cameraUpX, config.cameraUpY, config.cameraUpZ);
    parent.frustum(config.frustumLeft, config.frustumRight, config.frustumBottom, config.frustumTop, config.frustumNear,
        config.frustumFar);
  }

  @Override
  public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
  }

  @Override
  public void completedDraw(int frameNum, PApplet parent) {
  }

  @Override
  public void cleanup(PApplet parent) {
  }

  protected String[] loadShader(URL url) {
    try {
      return PApplet.loadStrings(url.openStream());
    } catch (IOException e) {
      System.err.println("Cannot load shader " + url.getFile());
    }
    return null;
  }

  protected void initShaders() {
    String OPENGL_RENDERER = ((PGraphics3D) parent.g).pgl.getString(PGL.RENDERER);

    pointShader = new PShader(parent, loadShader(defPointShaderVertURL), loadShader(defPointShaderFragURL));
    lineShader = new PShader(parent, loadShader(defLineShaderVertURL), loadShader(defLineShaderFragURL));
    colorShader = new PShader(parent, loadShader(defColorShaderVertURL), loadShader(defColorShaderFragURL));
    textureShader = new PShader(parent, loadShader(defTextureShaderVertURL), loadShader(defTextureShaderFragURL));

    // use vendor specific shaders if needed
    if (OPENGL_RENDERER.equals("VideoCore IV HW")) { // Broadcom's binary driver for Raspberry Pi
      lightShader = new PShader(parent, loadShader(defLightShaderVertBrcmURL), loadShader(defLightShaderFragURL));
      texlightShader = new PShader(parent, loadShader(defTexlightShaderVertBrcmURL),
          loadShader(defTexlightShaderFragURL));
    } else if (OPENGL_RENDERER.contains("VC4")) { // Mesa driver for same hardware
      lightShader = new PShader(parent, loadShader(defLightShaderVertVc4URL), loadShader(defLightShaderFragURL));
      texlightShader = new PShader(parent, loadShader(defTexlightShaderVertVc4URL),
          loadShader(defTexlightShaderFragURL));
    } else {
      lightShader = new PShader(parent, loadShader(defLightShaderVertURL), loadShader(defLightShaderFragURL));
      texlightShader = new PShader(parent, loadShader(defTexlightShaderVertURL), loadShader(defTexlightShaderFragURL));
    }

    parent.shader(pointShader);
    parent.shader(lineShader);
    parent.shader(colorShader);

    setNearFar(DEFAULT_NEAR, DEFAULT_FAR);
  }

  public DepthMapGenerator setNearFar(float near, float far) {
    pointShader.set("near", near);
    pointShader.set("far", far);
    lineShader.set("near", near);
    lineShader.set("far", far);

    colorShader.set("near", near);
    colorShader.set("far", far);
    lightShader.set("near", near);
    lightShader.set("far", far);
    textureShader.set("near", near);
    textureShader.set("far", far);
    texlightShader.set("near", near);
    texlightShader.set("far", far);

    return this;
  }

  public DepthMapGenerator setColorShader() {
    parent.shader(colorShader);

    return this;
  }

  public DepthMapGenerator setTextureShader() {
    parent.shader(textureShader);

    return this;
  }

  public DepthMapGenerator setLightShader() {
    parent.shader(lightShader);

    return this;
  }

  public DepthMapGenerator setTexLightShader() {
    parent.shader(texlightShader);

    return this;
  }

}
