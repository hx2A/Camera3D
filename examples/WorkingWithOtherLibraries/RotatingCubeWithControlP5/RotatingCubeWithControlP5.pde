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

  cp5 = new ControlP5(this);

/*
Camera3D does not always play nice with other libraries such as
ControlP5. One solution is to call setAutoDraw(false) in the
setup method and then call ControlP5's draw() method in the
sketches' postDraw method.

Try commenting out both lines and see what happens!
*/

  cp5.setAutoDraw(false);
  cp5.addSlider("cubeSize").setRange(50, 300).setValue(100)
      .setPosition(10, 10).setSize(100, 30)
      .setCaptionLabel("Cube Size").setColorCaptionLabel(0);
  cp5.addSlider("zTrans").setRange(-500, 500).setValue(-200)
      .setPosition(10, 60).setSize(100, 30)
      .setCaptionLabel("Z Translation")
      .setColorCaptionLabel(0);

/*
In this simple example it happens you can also get it to work
by moving the Camera3D code *before* the ControlP5 code.

For more complex examples this may not work.
*/

  camera3D = new Camera3D(this);

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
If I call cp5.setAutoDraw(false) I must also explicitly call the
draw method here.
*/

  cp5.draw();
}
