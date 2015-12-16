import camera3D.Camera3D;

PGraphics label;
Camera3D camera3D;

float rotX = 0; 
float rotY = 0;
float rotZ = 0;

void setup() {
  size(500, 500, P3D);
  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(color(192));
  camera3D.renderDefaultAnaglyph().setDivergence(1);

  label = createGraphics(140, 50);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0);
  label.textSize(16);
  label.text("Rotating Cube", 0, 0);
  label.endDraw();
}

/*
Camera3D adds an optional preDraw method to your sketch.

This method is called ONCE per frame, before any calls to the
draw method.
*/
void preDraw() {
  rotX += 0.5;
  rotY += 0.1;
  rotZ += 0.3;
}

/*
With Camera3D, the draw method is called multiple times per frame.

In this sketch, it is called TWICE. Once for the left, once for
the right.
*/
void draw() {
  strokeWeight(8);
  stroke(0);
  fill(255, 255, 255);
  translate(width / 2, height / 2, -400);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(250);
}

/*
Camera3D adds an optional postDraw method to your sketch.

This method is called ONCE per frame, after all calls to the
draw method.
*/
void postDraw() {
  copy(label, 0, 0, label.width, label.height, width - label.width,
       height - label.height, label.width, label.height);
}
