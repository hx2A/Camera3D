package camera3D;

import processing.core.PApplet;
import peasy.*;
import peasy.org.apache.commons.math.geometry.Rotation;
import peasy.org.apache.commons.math.geometry.RotationOrder;
import peasy.org.apache.commons.math.geometry.Vector3D;

public class PeasyCamAdapter {

	private PApplet parent;
	private Camera3D camera3D;
	private PeasyCam cam;

	protected PeasyCamAdapter(PApplet parent, Camera3D camera3D) {
		this.parent = parent;
		this.camera3D = camera3D;
	}

	public void registerPeasyCam(PeasyCam cam) {
		this.cam = cam;
		parent.registerMethod("pre", this);
	}

	public void pre() {
		if (cam.isActive()) {
			float pos[] = cam.getPosition();
			float lookat[] = cam.getLookAt();
			float rotations[] = cam.getRotations();

			Vector3D rup = (new Rotation(RotationOrder.XYZ, rotations[0],
					rotations[1], rotations[2])).applyTo(Vector3D.plusJ);

			camera3D.camera(pos[0], pos[1], pos[2], lookat[0], lookat[1],
					lookat[2], (float) rup.getX(), (float) rup.getY(),
					(float) rup.getZ());
		}
	}
}
