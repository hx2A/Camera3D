package camera3D.generators;

import java.net.URL;

import processing.core.PApplet;
import processing.opengl.PShader;

public class ChromaDepthGenerator extends Generator {

  private PApplet parent;

  // TODO: I need additional shaders for Broadcom and Mesa drivers. Check
  // PGraphicsOpenGL code L6929 for more info
  // TODO: the point, line, and color fragment shaders are identical

  static protected URL defColorShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/colorvert.glsl");
  static protected URL defTextureShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texvert.glsl");
  static protected URL defLightShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightvert.glsl");
  static protected URL defTexlightShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightvert.glsl");
  static protected URL defColorShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/colorfrag.glsl");
  static protected URL defTextureShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texfrag.glsl");
  static protected URL defLightShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/lightfrag.glsl");
  static protected URL defTexlightShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/texlightfrag.glsl");

  static protected URL defPointShaderVertURL = ChromaDepthGenerator.class
  .getResource("/camera3D/shaders/chromadepth/pointvert.glsl");
  static protected URL defPointShaderFragURL = ChromaDepthGenerator.class
  .getResource("/camera3D/shaders/chromadepth/pointfrag.glsl");
  static protected URL defLineShaderVertURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/linevert.glsl");
  static protected URL defLineShaderFragURL = ChromaDepthGenerator.class
      .getResource("/camera3D/shaders/chromadepth/linefrag.glsl");

  protected PShader colorShader;
  protected PShader textureShader;
  protected PShader lightShader;
  protected PShader texlightShader;
  protected PShader pointShader;
  protected PShader lineShader;

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
    lineShader = parent.loadShader(defLineShaderFragURL.getPath(), defLineShaderVertURL.getPath());
    pointShader = parent.loadShader(defPointShaderFragURL.getPath(), defPointShaderVertURL.getPath());
    colorShader = parent.loadShader(defColorShaderFragURL.getPath(), defColorShaderVertURL.getPath());
    textureShader = parent.loadShader(defTextureShaderFragURL.getPath(), defTextureShaderVertURL.getPath());
    lightShader = parent.loadShader(defLightShaderFragURL.getPath(), defLightShaderVertURL.getPath());
    texlightShader = parent.loadShader(defTexlightShaderFragURL.getPath(), defTexlightShaderVertURL.getPath());

    parent.shader(lineShader);
    parent.shader(pointShader);
    parent.shader(colorShader);
  }

  public ChromaDepthGenerator setNearFar(float near, float far) {
    lineShader.set("near", near);
    lineShader.set("far", far);
    pointShader.set("near", near);
    pointShader.set("far", far);
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
