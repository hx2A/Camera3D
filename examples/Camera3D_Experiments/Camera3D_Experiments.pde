import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import camera3D.*;
import controlP5.*;
import shapes3d.*;

int windowWidth;
int windowHeight;

// internal values
float xRot = 0;
float yRot = 0;
float zRot = 0;

// user settings
float scale = 0;
int strokeWeight = 5;
float divergence = 3;
float xRotSpeed = 0f;
float yRotSpeed = 0;
float zRotSpeed = 0f;
int xTrans = 0;
int yTrans = 0;
int zTrans = 0;
boolean rgbFlag = true;
int background_v1 = 64;
int background_v2 = 64;
int background_v3 = 64;
int fill_v1 = 255;
int fill_v2 = 255;
int fill_v3 = 255;
int fill_v4 = 255;
int stroke_v1 = 32;
int stroke_v2 = 32;
int stroke_v3 = 32;
int stroke_v4 = 255;

Camera3D camera3D;
ControlP5 cp5;
DropdownList objectList;
Ellipsoid earth;

Map<Integer, String> rendererMenuItems;
String rendererChoices = "Standard Renderer, Default Anaglyph,"
		+ "BitMask Filter Red-Cyan, BitMask Filter Magenta-Green,"
		+ "True Anaglyph, Gray Anaglyph, Half Color Anaglyph,"
		+ "Dubois Red-Cyan, Dubois Magenta-Green, Dubois Amber-Blue";
Map<Integer, String> objectMenuItems;
String objectChoices = "Box, Sphere, Ring of Spheres, Earth";

void setup() {
	size(800, 600, P3D);

	windowWidth = width;
	windowHeight = height;

	xTrans = width / 2;
	yTrans = height / 2;
	zTrans = -200;

	rendererMenuItems = createDropdownMap(rendererChoices);
	objectMenuItems = createDropdownMap(objectChoices);

	createControls();
	camera3D = new Camera3D(this);
	camera3D.renderDefaultAnaglyph();

	earth = new Ellipsoid(this, 40, 40);
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

void createControls() {
	cp5 = new ControlP5(this);
	cp5.setAutoDraw(false);
	cp5.getFont().setSize(12);

	float yOffset = 1f;
	int controlSpace = 23;

	DropdownList rendererList = addDropdown("Renderer Choices",
			(controlSpace * yOffset++), rendererMenuItems).addListener(
			new RendererListener());
	objectList = addDropdown("Object Choices", (controlSpace * yOffset),
			objectMenuItems); // .addListener(new ObjectListener());

	yOffset += 0.2;

	addSlider("divergence", "divergence", (controlSpace * yOffset++), -10,
			10).addListener(new DivergenceListener());

	addSlider("xTrans", "x translate", (controlSpace * yOffset++), 0, width);
	addSlider("yTrans", "y translate", (controlSpace * yOffset++), 0,
			height);
	addSlider("zTrans", "z translate", (controlSpace * yOffset++), -500,
			250);

	addSlider("xRot", "x rotation", (controlSpace * yOffset++), 0, 360);
	addSlider("yRot", "y rotation", (controlSpace * yOffset++), 0, 360);
	addSlider("zRot", "z rotation", (controlSpace * yOffset++), 0, 360);

	addSlider("xRotSpeed", "x rotation speed", (controlSpace * yOffset++),
			-5, 5);
	addSlider("yRotSpeed", "y rotation speed", (controlSpace * yOffset++),
			-5, 5);
	addSlider("zRotSpeed", "z rotation speed", (controlSpace * yOffset++),
			-5, 5);

	addSlider("scale", "object log-scale", (controlSpace * yOffset++), -2,
			2);

	RadioButton rb = cp5.addRadioButton("colorModel")
			.setPosition(10, (controlSpace * yOffset++)).setSize(25, 18)
			.addItem("RGB", 0).addItem("HSB   Color Model", 1).activate(0)
			.setItemsPerRow(2).setSpacingColumn(25)
			.setNoneSelectedAllowed(false);
	ColorModellListener cml = new ColorModellListener();
	rb.getItem(0).addListener(cml);
	rb.getItem(1).addListener(cml);

	BackgroundColorListener bcl = new BackgroundColorListener();
	addSlider("background_v1", "background red",
			(controlSpace * yOffset++), 0, 255).addListener(bcl);
	addSlider("background_v2", "background green",
			(controlSpace * yOffset++), 0, 255).addListener(bcl);
	Slider b3 = addSlider("background_v3", "background blue",
			(controlSpace * yOffset++), 0, 255).addListener(bcl);
	addSlider("fill_v1", "fill red", (controlSpace * yOffset++), 0, 255);
	addSlider("fill_v2", "fill green", (controlSpace * yOffset++), 0, 255);
	addSlider("fill_v3", "fill blue", (controlSpace * yOffset++), 0, 255);
	addSlider("fill_v4", "fill alpha", (controlSpace * yOffset++), 0, 255);

	addSlider("strokeWeight", "stroke weight", (controlSpace * yOffset++),
			0, 10);

	addSlider("stroke_v1", "stroke red", (controlSpace * yOffset++), 0, 255);
	addSlider("stroke_v2", "stroke green", (controlSpace * yOffset++), 0,
			255);
	addSlider("stroke_v3", "stroke blue", (controlSpace * yOffset++), 0,
			255);
	addSlider("stroke_v4", "stroke alpha", (controlSpace * yOffset++), 0,
			255);

	objectList.bringToFront();
	rendererList.bringToFront();

	b3.listen(true); // trigger listener
}

Map<Integer, String> createDropdownMap(String itemList) {
	Map<Integer, String> map = new HashMap<Integer, String>();
	for (String item : itemList.split(",")) {
		map.put(map.size(), item.trim());
	}
	return map;
}

DropdownList addDropdown(String name, float y,
		Map<Integer, String> menuItems) {
	int itemHeight = 18;
	DropdownList dropdownList = cp5.addDropdownList(name)
			.setPosition(10, y).setSize(180, (menuItems.size() + 1) * itemHeight)
			.setItemHeight(itemHeight).setBarHeight(itemHeight);
	for (Entry<Integer, String> entry : menuItems.entrySet()) {
		dropdownList.addItem(entry.getValue(), entry.getKey());
	}
	return dropdownList;
}

Slider addSlider(String variable, String caption, float y, int min,
		int max) {
	Slider slider = cp5.addSlider(variable);
	slider.setPosition(10, y);
	slider.setRange(min, max);
	slider.setSize(80, 18);
	slider.setCaptionLabel(caption);

	return slider;
}

void preDraw() {
	xRot = (xRot + xRotSpeed + 360) % 360;
	yRot = (yRot + yRotSpeed + 360) % 360;
	zRot = (zRot + zRotSpeed + 360) % 360;

	cp5.getController("xRot").setValue(xRot);
	cp5.getController("yRot").setValue(yRot);
	cp5.getController("zRot").setValue(zRot);

	earth.rotateTo(0, radians(yRot), 0);
}

void draw() {
	if (rgbFlag) {
		colorMode(RGB, 255, 255, 255);
	} else {
		colorMode(HSB, 255, 100, 100);
	}

	String objectChoice = objectMenuItems.get((int) objectList.getValue());

	strokeWeight(strokeWeight);

	if (stroke_v4 == 0) {
		noStroke();
	} else {
		stroke(color(stroke_v1, stroke_v2, stroke_v3, stroke_v4));
	}

	if (fill_v4 == 0) {
		noFill();
	} else {
		fill(color(fill_v1, fill_v2, fill_v3, fill_v4));
	}

	background(background_v1, background_v2, background_v3);

	pushMatrix();
	translate(xTrans, yTrans, zTrans);
	if (!objectChoice.equals("Earth")) {
		rotateX(radians(xRot));
		rotateY(radians(yRot));
		rotateZ(radians(zRot));
	}

	shapeMode(CENTER);
	scale(pow(10, scale));

	switch ((int) objectList.getValue()) {
	case 0:
		box(100);
		break;
	case 1:
		sphereDetail(8, 6);
		sphere(100);
		break;
	case 2:
		sphereDetail(10);
		int sphereCount = 6;
		for (int ii = 0; ii < sphereCount; ++ii) {
			pushMatrix();
			rotateY(TWO_PI * ii / sphereCount);
			translate(0, 0, 200);
			sphere(50);
			popMatrix();
		}
		break;
	case 3:
		earth.draw();
		break;
	default:
		println("unknown object " + objectChoice + ". please report bug.");
	}

	popMatrix();
}

void postDraw() {
	cp5.draw();
}

/*
 * Mouse Events
 */
void mouseWheel(MouseEvent event) {
	scale += -event.getCount() / 10.;
	cp5.getController("scale").setValue(scale);
}
