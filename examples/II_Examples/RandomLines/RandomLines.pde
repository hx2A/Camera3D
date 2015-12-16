import camera3D.*;

Camera3D camera3D;
Shape[] shapeArray;

int xMin;
int xMax;
int yMin;
int yMax;
int zMin;
int zMax;

void setup() {
	size(500, 500, P3D);
	noiseSeed(42);

	camera3D = new Camera3D(this);
	camera3D.setBackgroundColor(255);
	camera3D.reportStats();
	camera3D.renderDuboisRedCyanAnaglyph().setDivergence(1.5f);

	int offset = 200;
	xMin = -width / 2 - offset;
	xMax = width / 2 + offset;
	yMin = -height / 2 - offset;
	yMax = height / 2 + offset;
	zMin = -400;
	zMax = 0;

	shapeArray = new Shape[40];
	for (int ii = 0; ii < shapeArray.length; ++ii) {
		shapeArray[ii] = new Shape(random(1000));
	}
}

void preDraw() {
	for (Shape shape : shapeArray) {
		shape.update(frameCount / 600f);
	}
}

void draw() {
	translate(width / 2, height / 2);

	strokeWeight(3);
	for (Shape shape : shapeArray) {
		shape.draw();
	}

	stroke(128);
	for (int ii = 1; ii < shapeArray.length; ++ii) {
		PVector shapeA = shapeArray[ii - 1].getPosition();
		PVector shapeB = shapeArray[ii].getPosition();
		line(shapeA.x, shapeA.y, shapeA.z, shapeB.x, shapeB.y, shapeB.z);
	}
	PVector shapeA = shapeArray[0].getPosition();
	PVector shapeB = shapeArray[shapeArray.length - 1].getPosition();
	line(shapeA.x, shapeA.y, shapeA.z, shapeB.x, shapeB.y, shapeB.z);
}
