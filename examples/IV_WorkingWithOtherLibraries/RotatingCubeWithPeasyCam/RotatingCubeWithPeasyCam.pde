import camera3D.Camera3D;
import peasy.*;

private Camera3D camera3D;
private PeasyCam cam;
PMatrix3D modelview;

void setup() {
  size(500, 500, P3D);

  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(100);
  cam.setMaximumDistance(500);

  camera3D = new Camera3D(this);
  camera3D.setBackgroundColor(255);
  camera3D.renderDefaultAnaglyph().setDivergence(2);

/*
After creating your PeasyCam and Camera3D, simply link the
two together using an adapter:
*/
  camera3D.createPeasyCamAdapter().setPeasyCam(cam);

/*
Then use PeasyCam and Camera3D as normal. The adapter will
communicate changes to PeasyCam orientation to Camera3D.
*/


/*
PeasyCam allows you to create more than one PeasyCam and to switch
from one to other. If your sketch uses this feature, do something
like this:

  camera3D.createPeasyCamAdapter().setPeasyCam(cam1);
  camera3D.createPeasyCamAdapter().setPeasyCam(cam2);
  camera3D.createPeasyCamAdapter().setPeasyCam(cam3);

Now there are three adapters linking each PeasyCam to Camera3D. The
adapters check to make sure each PeasyCam is active before
communicating coordinates to Camera3D.

Any PeasyCam modification code should best go in the preDraw method.
*/
}

void preDraw() {
 // if you are going to fiddle with PeasyCam settings, do so here.
} 

void draw() {

  background(255);

  strokeWeight(8);
  stroke(0);
  fill(255, 255, 255);
  box(25);
}
