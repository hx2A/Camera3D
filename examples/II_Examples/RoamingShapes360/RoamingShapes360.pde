/*
 * RoamingShapes sketch adapted to 360 video.
 * 
 * When you run this sketch you will see the distorted equirectangular
 * projection. This distortion is necessary to map a sphere to a rectangle, kind
 * of like world maps you may have seen in school:
 * 
 * https://en.wikipedia.org/wiki/Equirectangular_projection
 * 
 * This sketch is intended to be made into a movie and uploaded to an online 360
 * video player. You can see examples online:
 * 
 * https://www.youtube.com/watch?v=Mxr82Qt-gQI
 * https://www.facebook.com/james.e.schmitz/videos/a.10155267585414691.1073741835.722099690/10155267585504691/
 * https://vimeo.com/214303851
 * 
 * Uploading 360 video to Facebook or Youtube requires you to add special
 * metatags to the movie file. Check online documentation to learn how
 * to do this.
 * 
 * Note the Vimeo player adds some distortion to the straight lines. I asked
 * them about it and it seems to be a result of the trade-off between a wider
 * FOV and distortion.
 * 
 * https://vimeo.com/forums/help/topic:288576
 */

import camera3D.*;
import camera3D.generators.*;

Camera3D camera3D;
Shape[] shapeArray;

int xMin;
int xMax;
int yMin;
int yMax;
int zMin;
int zMax;

void setup() {
  size(1000, 500, P3D);
  colorMode(HSB, 360, 100, 100);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(128);
  Monoscopic360Generator generator = camera3D.renderMonoscopic360();

  // You'll probably want to save oversized frames to files to make into a
  // movie. Below are the settings for 4K 360 video. Note that these size
  // settings are different from the `size(1000, 500, P3D)` settings above.
  // For better performance, try to set the sketch width to be about 25% of
  // the output width and a width:height aspect ratio of 2:1.

  //generator.setOutputWidthHeightAndLocation(4 * 1024, 2 * 1024,
  //                                          "roaming_cubes_#####.png");

  // Saving each frame as a 4K frame will slow down the sketch considerably.
  // There is no way around this. For a long running video you may want to get
  // a cup of coffee instead of sitting at your computer. To facilitate this,
  // Camera3D supports a frame limit. The sketch will exit when the
  // `frameCount` reaches the below value.
  
  // camera3D.setFrameLimit(60 * 30);

  // When you use the `setFrameLimit` feature, Camera3D will check your disk
  // drive to make sure you have enough space for all the frames. It will throw
  // an exception if you do not. This is a serious issue if you are saving 4K
  // *.tiff files, which are 25 MB each, or 8K *.tiff files which are 100 MB
  // each. One minute's worth of frames (60 seconds * 30 frames/sec) can hose
  // your harddrive very quickly. I did this to myself while developing this
  // sketch. :-(

  // initialize variables.
  int cubeSize = 400;
  xMin = -cubeSize / 2;
  xMax = cubeSize / 2;
  yMin = -cubeSize / 2;
  yMax = cubeSize / 2;
  zMin = -cubeSize / 2;
  zMax = cubeSize / 2;

  camera3D.camera(0, 0, 0, // camera location
                  0, 0, -1, // camera direction 
                  0, -1, 0); // camera up direction

  float maxVelocity = 0.5f;
  float maxRotation = 0.8f;

  // create shapes and initialize motion.
  shapeArray = new Shape[8];
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

  fill(360);
  beginShape(QUADS);
  // left
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

  // front
  vertex(xMax, yMin, zMax);
  vertex(xMax, yMax, zMax);
  vertex(xMin, yMax, zMax);
  vertex(xMin, yMin, zMax);

  // bottom
  vertex(xMin, yMax, zMin);
  vertex(xMin, yMax, zMax);
  vertex(xMax, yMax, zMax);
  vertex(xMax, yMax, zMin);

  // top
  vertex(xMin, yMin, zMin);
  vertex(xMin, yMin, zMax);
  vertex(xMax, yMin, zMax);
  vertex(xMax, yMin, zMin);
  endShape();

  strokeWeight(4);
  for (Shape shape : shapeArray) {
    shape.draw();
  }
}