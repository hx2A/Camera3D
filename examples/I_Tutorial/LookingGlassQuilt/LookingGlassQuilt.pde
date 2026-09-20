import camera3D.Camera3D;

Camera3D camera3D;

float rotX = 0; 
float rotY = 0;
float rotZ = 0;

void setup() {
  // default for Looking Glass Go is 11 columns by 6 rows and a frame size of
  // 372 by 682. This makes the quilt size 4092 by 4092.
  size(372, 682, P3D);
  pixelDensity(displayDensity());

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(color(192));

  // the default quilt dimensions are 11x6 with an aspect ratio of 0.5625
  // these are the suggested settings for the Looking Glass Go
  // set the output location to a directory that exists on your computer
  camera3D.renderLookingGlassQuilt().setQuiltDimensions(11, 6, 0.5625).setOutputLocation("/tmp/frames/");
  // 30 second recording at 30 fps
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
  box(125);
  pop();

  int xSpacing = 100;
  int ySpacing = 200;
  int zSpacing = 100;
  for (int x = -xSpacing; x <= xSpacing; x += 2 * xSpacing) {
    for (int y = -ySpacing; y <= ySpacing; y += 2 * ySpacing) {
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
