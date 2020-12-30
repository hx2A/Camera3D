package camera3D.generators;

import java.net.URL;

import processing.core.PApplet;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;
import processing.opengl.PShader;

public class ChromaDepthGenerator extends Generator {

  private PApplet parent;

  static protected URL defPointShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/pointvert.glsl");
  static protected URL defPointShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/pointfrag.glsl");
  static protected URL defLineShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/linevert.glsl");
  static protected URL defLineShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/linefrag.glsl");

  static protected URL defColorShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/colorvert.glsl");
  static protected URL defColorShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/colorfrag.glsl");
  static protected URL defTextureShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texvert.glsl");
  static protected URL defTextureShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texfrag.glsl");
  static protected URL defLightShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightvert.glsl");
  static protected URL defLightShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightfrag.glsl");
  static protected URL defTexlightShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightvert.glsl");
  static protected URL defTexlightShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightfrag.glsl");

  // extra shaders for Raspberry Pis
  static protected URL defLightShaderVertBrcmURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightvert-brcm.glsl");
  static protected URL defLightShaderVertVc4URL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightvert-vc4.glsl");
  static protected URL defTexlightShaderVertBrcmURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightvert-brcm.glsl");
  static protected URL defTexlightShaderVertVc4URL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightvert-vc4.glsl");

  protected PShader pointShader;
  protected PShader lineShader;
  protected PShader colorShader;
  protected PShader textureShader;
  protected PShader lightShader;
  protected PShader texlightShader;

  public ChromaDepthGenerator() {
    this.parent = null;
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
    // maintain reference to parent
    if (this.parent == null) {
      this.parent = parent;
      initShaders(parent);
    }
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

  protected void initShaders(PApplet parent) {
    String OPENGL_RENDERER = ((PGraphics3D) parent.g).pgl.getString(PGL.RENDERER);

    pointShader = parent.loadShader(defPointShaderFragURL.getPath(), defPointShaderVertURL.getPath());
    lineShader = parent.loadShader(defLineShaderFragURL.getPath(), defLineShaderVertURL.getPath());
    colorShader = parent.loadShader(defColorShaderFragURL.getPath(), defColorShaderVertURL.getPath());
    textureShader = parent.loadShader(defTextureShaderFragURL.getPath(), defTextureShaderVertURL.getPath());

    // use vendor specific shaders if needed
    if (OPENGL_RENDERER.equals("VideoCore IV HW")) { // Broadcom's binary driver for Raspberry Pi
      lightShader = parent.loadShader(defLightShaderFragURL.getPath(), defLightShaderVertBrcmURL.getPath());
      texlightShader = parent.loadShader(defTexlightShaderFragURL.getPath(), defTexlightShaderVertBrcmURL.getPath());
    } else if (OPENGL_RENDERER.contains("VC4")) { // Mesa driver for same hardware
      lightShader = parent.loadShader(defLightShaderFragURL.getPath(), defLightShaderVertVc4URL.getPath());
      texlightShader = parent.loadShader(defTexlightShaderFragURL.getPath(), defTexlightShaderVertVc4URL.getPath());
    } else {
      lightShader = parent.loadShader(defLightShaderFragURL.getPath(), defLightShaderVertURL.getPath());
      texlightShader = parent.loadShader(defTexlightShaderFragURL.getPath(), defTexlightShaderVertURL.getPath());
    }

    parent.shader(pointShader);
    parent.shader(lineShader);
    parent.shader(colorShader);
  }

  public ChromaDepthGenerator setNearFar(float near, float far) {
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

  public ChromaDepthGenerator setColorShader() {
    parent.shader(colorShader);

    return this;
  }

  public ChromaDepthGenerator setTextureShader() {
    parent.shader(textureShader);

    return this;
  }

  public ChromaDepthGenerator setLightShader() {
    parent.shader(lightShader);

    return this;
  }

  public ChromaDepthGenerator setTexLightShader() {
    parent.shader(texlightShader);

    return this;
  }

}
