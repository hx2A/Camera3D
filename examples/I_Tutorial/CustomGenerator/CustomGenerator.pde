/*
This is a simple example demonstrating how to make a custom
generator in Camera3D. The generator uses "Wiggle Stereoscopy,"
a not particularly interesting stereoscopic technique that happens
to be simple enough for a quick demo. Google that to see
marginally better examples.
*/
import camera3D.Camera3D;

Camera3D camera3D;

void setup() {
  size(300, 300, P3D);
  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(255);

  WiggleStereoscopyGenerator generator = new WiggleStereoscopyGenerator(10);
  generator.setDivergence(5f);
  camera3D.setGenerator(generator);
}

void draw() {
  strokeWeight(4);
  stroke(0);

  int boxSize = 200;
  int zDepth = -100;

  fill(128, 128, 128);
  translate(width / 2, height / 2, zDepth);
  rotateX(0.5);
  rotateY(0.9);
  rotateZ(0.1);
  box(boxSize);
}
