import camera3D.*;

PGraphics label;
Camera3D camera3D;

float rotX = 0;
float rotY = 0;
float rotZ = 0;

void setup() {
  size(500, 500, P3D);
  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(255);
  camera3D.renderDuboisRedCyanAnaglyph();
  camera3D.setCameraDivergence(3);

  label = createGraphics(140, 60);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0);
  label.textSize(16);
  label.text("Rotating Cube\nby Jim Schmitz", 0, 0);
  label.endDraw();
}

void preDraw() {
  println("executing " + camera3D.currentActivity());
  rotX += 0.5;
  rotY += 0.1;
  rotZ += 0.3;
}

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

void postDraw() {
  println("executing " + camera3D.currentActivity());
  copy(label, 0, 0, label.width, label.height, width - label.width,
      height - label.height, label.width, label.height);
}
