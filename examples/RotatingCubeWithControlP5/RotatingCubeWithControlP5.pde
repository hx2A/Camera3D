import controlP5.*;
import camera3D.*;

Camera3D camera3D;
ControlP5 cp5;

float rotX = 0;
float rotY = 0;
float rotZ = 0;
int cubeSize;
int zTrans;

void setup() {
  size(500, 500, P3D);
  camera3D = new Camera3D(this);

  /*
   * Camera3D does not always play nice with other libraries such as
   * ControlP5. One solution is to call setautoDraw(false) here
   * and the draw() method in the postDraw method.
   */
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);
  cp5.addSlider("cubeSize").setRange(50, 300).setValue(100)
      .setPosition(10, 10).setSize(100, 30)
      .setCaptionLabel("Cube Size").setColorCaptionLabel(0x000000);
  cp5.addSlider("zTrans").setRange(-500, 500).setValue(-200)
      .setPosition(10, 60).setSize(100, 30)
      .setCaptionLabel("Z Translation")
      .setColorCaptionLabel(color(0));

  strokeWeight(4);
  stroke(0);
  fill(255, 255, 255);
}

void preDraw() {
  rotX += 0.5;
  rotY += 0.1;
  rotZ += 0.3;
}

void draw() {
  translate(width / 2, height / 2, zTrans);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(cubeSize);
}

void postDraw() {
  /*
   * If I call cp5.setAutoDraw(false) I must also explicitly call the draw
   * method here.
   */
  cp5.draw();
}
