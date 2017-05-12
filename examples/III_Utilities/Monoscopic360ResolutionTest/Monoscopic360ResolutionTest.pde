/*
 * Resolution Test sketch for 360 video.
 * 
 * The purpose of this sketch is to understand the non-uniform pixel-level
 * resolution in an equirectangular projection.
 * 
 * Instead of rendering a 3D scene, this sketch will scatter evenly spaced
 * colored pixels over each component frame. Each colored pixel will be
 * isolated on a white background.
 * 
 * After generating the equirectangular projection, you will see that some of
 * the individual pixels will be stretched out to 2x1, 1x2, or 2x2 colored
 * regions. This will happen most quickly near the projection's equator.
 * 
 * This experiment will show you that resolution is most quickly lost near the
 * equator. The key is to consider the size of the area the projection
 * devotes to a orientation. A large area is devoted to above and below,
 * resulting in the best resolution for those areas in the output.
 * 
 * You can also use this to test the various 360 photo and video viewers. These
 * below videos were all created using this sketch and posted online. Consult
 * the online documentation for more discussion.
 * 
 * https://www.youtube.com/watch?v=PAhGcpR75IQ
 * https://www.facebook.com/james.e.schmitz/videos/a.10155267585414691.1073741835.722099690/10155331394759691
 * https://www.facebook.com/photo.php?fbid=10155331511844691&set=a.10155267585414691.1073741835.722099690
 * https://vimeo.com/217045725
 */


import camera3D.*;
import camera3D.generators.*;

Camera3D camera3D;
int componentCount;

public void setup() {
  size(1300, 650, P3D);

  colorMode(HSB, 360, 100, 100);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(128);

  Monoscopic360Generator generator = camera3D
    .renderMonoscopic360()
    .setPanelXYSteps(1, 2)
    .setPanelExplainPlanLocation(
    "frames/explainplan.png")
    .setOutputSizeAndLocation(4 * 1024, 
    "frames/resolution_test_#####.tiff");

  componentCount = generator.getComponentCount();

  // save 5 frames worth of video
  camera3D.setFrameLimit(5);
}

void draw() {
  int frameNum = camera3D.getFrameNum();
  int id = frameNum / 6;
  int orientationOrdinal = frameNum % 6;

  int value = 255 * (1 + id) / (1 + componentCount / 6);
  int frameColor = 0xFF000000 | (value << (8 * (orientationOrdinal / 2)))
    | (value << (8 * (((orientationOrdinal + 1) / 2) % 3)));

  loadPixels();
  for (int i = 0; i < pixels.length; ++i) {
    if ((i + frameCount) % 7 == 0) {
      pixels[i] = frameColor;
    } else {
      pixels[i] = 0xFFFFFFFF;
    }
  }
  updatePixels();
}
