import camera3D.*;

PGraphics label;
Camera3D camera3D;

float rotX = 0;
float rotY = 0;
float rotZ = 0;

void setup() {
  size(500, 500, P3D);
  camera3D = new Camera3D(this);

  label = createGraphics(140, 60);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0);
  label.textSize(16);
  label.text("Rotating Cube\nby Jim Schmitz", 0, 0);
  label.endDraw();
}

/*
This optional preDraw method will be called ONCE for each
frame, and will be called before the first call to the draw
method.
*/
void preDraw() {
  println("executing " + camera3D.currentActivity());
  rotX += 0.5;
  rotY += 0.1;
  rotZ += 0.3;
}

/*
This is the usual draw method for your sketch. With Camera3D, it
will be called TWICE for each frame. The second call is to draw
your scene from a second perspective.

Since it is called twice, code that must only be run once should
be moved to the preDraw method. Examples of code that belongs in
preDraw are position or rotation calculations.
*/
void draw() {
  println("executing " + camera3D.currentActivity());
  println("frameRate: " + str(frameRate));
  strokeWeight(8);
  stroke(0);
  fill(255, 255, 255);
  translate(width / 2, height / 2, -200);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(200);
}

/*
This optional postDraw method will be called ONCE for each frame,
and will be called after the second call to the draw method.
*/
void postDraw() {
  println("executing " + camera3D.currentActivity());
  copy(label, 0, 0, label.width, label.height, width - label.width,
      height - label.height, label.width, label.height);
}