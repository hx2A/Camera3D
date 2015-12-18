/*
Split Depth Illusion animations are inspired by the split depth
GIFs popular on the internet. I figured out how to get Processing
to implement the same effect.

This is different from adding two rectangles to a regular P3D sketch.
The occulusion bars are pure white and will not cast or receive a
shadow, just like a split depth GIF.

You can use the default occlusion bars or make a custom one using
a mask, as done below.
*/

import camera3D.Camera3D;

Camera3D camera3D;

float z;

Shape[] shapes;

void setup() {
  size(500, 500, P3D);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(128);
  camera3D.enableSaveFrame("/tmp/camera3D/split_depth_illusion");

  // this creates a PGraphics object with a white background and
  // two black rectangles. The occlusion plane is interpreted by
  // looking for white pixels in this mask.  
  PGraphics mask = createGraphics(width, height);
  mask.beginDraw();
  mask.background(255);
  mask.rectMode(CORNERS);
  mask.fill(0);
  mask.rect(20, 20, width / 2 - 50, height - 20, 10);
  mask.rect(width / 2 - 30, 20, width / 2 + 30, height - 20, 10);
  mask.rect(width / 2 + 50, 20, width - 20, height - 20, 10);
  mask.endDraw();

  camera3D.renderSplitDepthIllusion().setOcclusionZfactor(0.8f)
    .setOcclusionPlaneColor(color(255)).setOcclusionPlaneMask(mask);

  stroke(0);
  colorMode(HSB, 255, 100, 100);
  lights();

  shapes = new Shape[30];
  for (int i = 0; i < shapes.length; ++i) {
    shapes[i] = new Shape(i * 25);
  }
}

void preDraw() {
  if ((frameCount / 500) % 2 == 1)
    z -= 1.5;
  else
    z += 1.5;
}

public void draw() {
  translate(width / 2, height / 2, 0);

  for (int i = 0; i < shapes.length; ++i) {
    shapes[i].draw(z);
  }
}