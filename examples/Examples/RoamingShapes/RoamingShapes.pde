import camera3D.*;

Camera3D camera3D;
Shape[] shapeArray;

int xMin;
int xMax;
int yMin;
int yMax;
int zMin;
int zMax;
int zMaxShape;

void setup() {
  size(500, 500, P3D);
  colorMode(HSB, 360, 100, 100);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(128);
  camera3D.renderDuboisRedCyanAnaglyph();
  camera3D.setCameraDivergence(3);

  int offset = 0;
  xMin = -width / 2 + offset;
  xMax = width / 2 - offset;
  yMin = -height / 2 + offset;
  yMax = height / 2 - offset;
  zMin = -400;
  zMaxShape = -50;
  zMax = 50;

  float maxVelocity = 0.5f;
  float maxRotation = 0.8f;

  shapeArray = new Shape[4];
  for (int ii = 0; ii < shapeArray.length; ++ii) {
    PVector start = new PVector(random(xMin, xMax), random(yMin, yMax),
        random(zMin, zMax));

    PVector velocity = PVector.random3D();
    velocity.normalize();
    velocity.mult(maxVelocity);

    PVector rotation = PVector.random3D();
    rotation.normalize();
    rotation.mult(maxRotation);

    PVector orientation = new PVector(random(360), random(360),
        random(360));
    color shapeColor = color(random(360), 25, 100);

    shapeArray[ii] = new Shape(start, velocity, orientation, rotation,
        shapeColor);
  }
}

void preDraw() {
  for (Shape shape : shapeArray) {
    shape.update();
  }
}

void draw() {
  strokeWeight(4);
  stroke(0);
  translate(width / 2, height / 2);

  fill(360);
  beginShape(QUADS);
  // left
  float epsilon = 2;
  vertex(xMin, yMin, zMin);
  vertex(xMin, yMin, zMax);
  vertex(xMin, yMax, zMax);
  vertex(xMin, yMax, zMin);

  // right
  vertex(xMax, yMin, zMin);
  vertex(xMax, yMin, zMax);
  vertex(xMax, yMax, zMax);
  vertex(xMax, yMax, zMin);

  // back
  vertex(xMax, yMin, zMin);
  vertex(xMax, yMax, zMin);
  vertex(xMin, yMax, zMin);
  vertex(xMin, yMin, zMin);

  // bottom
  vertex(xMin, yMax + epsilon, zMin);
  vertex(xMin, yMax + epsilon, zMax);
  vertex(xMax, yMax + epsilon, zMax);
  vertex(xMax, yMax + epsilon, zMin);

  // top
  vertex(xMin, yMin - epsilon, zMin);
  vertex(xMin, yMin - epsilon, zMax);
  vertex(xMax, yMin - epsilon, zMax);
  vertex(xMax, yMin - epsilon, zMin);
  endShape();

  strokeWeight(1);
  for (Shape shape : shapeArray) {
    shape.draw();
  }
}
