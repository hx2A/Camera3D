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
