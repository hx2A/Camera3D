import shapes3d.*;
import camera3D.*;

Camera3D camera3D;
Ellipsoid earth;
PImage background;

void setup() {
  size(800, 600, P3D);
  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(0);
  camera3D.renderDuboisRedCyanAnaglyph().setDivergence(1f);
  camera3D.setCameraTarget(width / 2f, height / 2f, -700);

  background = loadImage("fancy_stars.jpg");

  earth = new Ellipsoid(this, 20, 20);
  earth.setRadius(150);
  earth.setTexture("land_ocean_ice_2048.png");
  earth.drawMode(Shape3D.TEXTURE);

  Box spaceStation = new Box(this, 20, 10, 10);
  spaceStation.fill(128);
  spaceStation.strokeWeight(2);
  spaceStation.stroke(0);
  spaceStation.moveTo(0, 0, 250);

  earth.addShape(spaceStation);
}

void preDraw() {
  earth.rotateBy(0, radians(0.3f), 0);
}

void draw() {
  translate(width / 2, height / 2, -200);
  earth.draw();

  translate(0, 0, -500);
  imageMode(CENTER);

  // don't change the background aspect ratio
  float scaleImg = max(height * 3f / background.height, width * 3f
      / background.width);
  image(background, 0, 0, background.width * scaleImg, background.height
      * scaleImg);
}
