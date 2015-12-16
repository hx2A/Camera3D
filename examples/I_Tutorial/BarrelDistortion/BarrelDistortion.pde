/*
This is the Barrel Distortion algorithm, suitable for Oculus Rift
or (maybe) Google Cardboard.

The default distortion coefficients are set for the Oculus Rift.
You can change them with the setBarrelDistortionCoefficients
method.
*/
import camera3D.Camera3D;
import camera3D.generators.*;
Camera3D camera3D;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(300, 300, P3D);
  camera3D = new Camera3D(this);
  BarrelDistortionGenerator generator = camera3D.renderBarrelDistortion().setDivergence(1);
  // Change these numbers to learn how this works. Set them to 0, 0 for no distortion.
  generator.setBarrelDistortionCoefficients(0.22, 0.24);

  camera3D.setBackgroundColor(255);
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