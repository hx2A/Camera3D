/*
Split Depth Illusion animations are inspired by the split depth
GIFs popular on the Internet. I figured out how to get Processing
to implement the same effect.

This is different from adding two rectangles to a regular P3D
sketch. The occlusion bars are pure white and will not cast or
receive a shadow, just like a split depth GIF.
*/
import camera3D.Camera3D;

Camera3D camera3D;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(300, 300, P3D);
  camera3D = new Camera3D(this);

/*
There are two ways to select the location of the occlusion plane:

* absolute distance from the camera
* multiple of the distance from the camera location to the camera
target.

Either is fine, but one may be more convenient than the other.

Try fiddling with the numbers to see what happens.
*/  
  camera3D.renderSplitDepthIllusion().setOcclusionZdistance(350);
  // or:
//  camera3D.renderSplitDepthIllusion().setOcclusionZfactor(1.4);
  camera3D.setBackgroundColor(128);
  camera3D.enableSaveFrame('s', "debug");
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