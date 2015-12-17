/*
Dubois Anaglyphs are a mathematically optimal way to create
anaglyphs.

Go here:

http://www.site.uottawa.ca/~edubois/anaglyph/

to learn more.
*/
import camera3D.Camera3D;

PGraphics label;
Camera3D camera3D;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(300, 300, P3D);
  camera3D = new Camera3D(this);
  camera3D.renderDuboisRedCyanAnaglyph().setDivergence(1);
  // or use the magenta green or amber blue generators.
//  camera3D.renderDuboisMagentaGreenAnaglyph().setDivergence(1);
//  camera3D.renderDuboisAmberBlueAnaglyph().setDivergence(1);
  camera3D.setBackgroundColor(255);
  camera3D.enableSaveFrame('s', "debug");

  label = createGraphics(120, 20);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0);
  label.textSize(12);
  label.text("Camera3D Example", 0, 0);
  label.endDraw();
}

void preDraw() {
  rot += 1;
  rotX = rot * 0.5f;
  rotY = rot * 0.1f;
  rotZ = rot * 0.3f;
}

void draw() {
  strokeWeight(4);
  stroke(0);

  int boxSize = 90;
  int zDepth = -100;

  pushMatrix();
  fill(255, 0, 0);
  translate(width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 255, 0);
  translate(4 * width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 0, 255);
  translate(width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(128, 128, 128);
  translate(4 * width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();
}

void postDraw() {
  copy(label, 0, 0, label.width, label.height, width - label.width,
       height - label.height - 10, label.width, label.height);
}