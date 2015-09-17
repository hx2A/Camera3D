/*
This sketch is a simple utility for evaluating the quality of your
monitor and glasses.

Run the sketch with your Red Cyan glasses on and cover one eye. You
should see either the horizontal or vertical lines disappear or
diminish.

Ideally one set of lines will disappear. If it doesn't, try making small
adjustments to the brightness/contrast settings on your monitor to see
if it improves.

Bottom line, the better this works, the better the 3D effect of the other
examples.
*/

import camera3D.*;

Camera3D camera3D;

void setup() {
  size(500, 500, P3D);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(0);
  camera3D.setCameraDivergence(0);
}

void draw() {
  noStroke();
  fill(255);

  if (camera3D.currentActivity() == "left") {
    for (int x = 0; x < width; x += 50) {
      rect(x, 0, 25, 500);
    }
  }

  if (camera3D.currentActivity() == "right") {
    for (int y = 0; y < width; y += 50) {
      rect(0, y, 500, 25);
    }
  }
}

void postDraw() {
  noLoop();
}