import camera3D.*;

private PGraphics label;
private Camera3D camera3D;

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
  label.text("Rotating Spheres\nby Jim Schmitz", 0, 0);
  label.endDraw();

  strokeWeight(3);
  stroke(32);
  fill(255, 255, 225);
}

void preDraw() {
  println("executing " + camera3D.currentActivity());
  rotX += 0.1;
  rotY += 0.5;
  rotZ += 0.0;
}

void draw() {
  println("executing " + camera3D.currentActivity());
  translate(width / 2, height / 2, -200);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  sphereDetail(10);

  int sphereCount = 6;
  for (int ii = 0; ii < sphereCount; ++ii) {
    pushMatrix();
    rotateY(TWO_PI * ii / sphereCount);
    translate(0, 0, 200);
    sphere(50);
    popMatrix();
  }
}

void postDraw() {
  println("executing " + camera3D.currentActivity());
  copy(label, 0, 0, label.width, label.height, width - label.width,
      height - label.height, label.width, label.height);
}
