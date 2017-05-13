/*
 * This generator constructs an equirectangular projection of every possible
 * angle emanating from the camera. The intended purpose is for you to save
 * each frame and make a movie out of it to play in an online 360 video player.
 * To see this sketch in a 360 video player, click below: 
 * 
 * https://www.youtube.com/watch?v=YLTnoa7eoSQ
 * https://vimeo.com/216711676
 *
 * Read the online documentation to learn how to upload your own sketches.
 * 
 * http://ixora.io/projects/camera-3D/monoscopic-360-video/
 */

import camera3D.*;
	
Camera3D camera3D;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(600, 300, P3D);
  colorMode(HSB, 360, 100, 100);

  camera3D = new Camera3D(this);
  camera3D.renderMonoscopic360();
  camera3D.setBackgroundColor(color(360, 0, 100));
  camera3D.camera(0, 0, 0, 0, 0, -1, 0, -1, 0);
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

  int boxSize = 50;
  for (int y = -750; y <= 750; y += 100) {
    for (int theta = 0; theta < 360; theta += 30) {
      pushMatrix();
      fill(theta, 100, 50);
      rotateY(radians(theta));
      translate(0, y, -250);
      rotateX(radians(rotX));
      rotateY(radians(rotY));
      rotateZ(radians(rotZ));
      box(boxSize);
      popMatrix();
    }
  }
}
