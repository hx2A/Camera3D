import camera3D.Camera3D;

Camera3D camera3D;

float rotX = 0; 
float rotY = 0;
float rotZ = 0;

void setup() {
  size(500, 500, P3D);
  pixelDensity(displayDensity());

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(color(192));
  camera3D.renderLookingGlassQuilt().setOutputLocation("/tmp/frames/");
  camera3D.setFrameLimit(30 * 30);

  strokeWeight(2);
  stroke(0);
  fill(255, 255, 255);
}

void preDraw() {
  rotX += 0.5;
  rotY += 0.1;
  rotZ += 0.3;
}

void draw() {
  translate(width / 2, height / 2, 0);
  
  push();
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(100);
  pop();

  int xySpacing = 100;
  int zSpacing = 100;
  for (int x = -xySpacing; x <= xySpacing; x += 2 * xySpacing) {
    for (int y = -xySpacing; y <= xySpacing; y += 2 * xySpacing) {
      for (int z = -zSpacing; z <= zSpacing; z += 2 * zSpacing) {
        push();
        translate(x, y, z);
        rotateX(radians(rotX));
        rotateY(radians(rotY));
        rotateZ(radians(rotZ));
        box(25);
        pop();
      }
    }
  }
}
