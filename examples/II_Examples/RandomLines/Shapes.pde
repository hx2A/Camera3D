class Shape {
  private PVector position;
  private float noiseOffset;

  public Shape(float noiseOffset) {
    this.noiseOffset = noiseOffset;
    update(0);
  }

  public PVector getPosition() {
    return position;
  }

  public void update(float t) {
    position = new PVector(lerp(xMin, xMax, noise(t + noiseOffset)),
        lerp(yMin, yMax, noise(t + 13 * noiseOffset)), lerp(zMin,
            zMax, noise(t + 7 * noiseOffset)));
  }

  private void drawSelf(PVector drawPosition) {
    pushMatrix();
    pushStyle();
    translate(drawPosition.x, drawPosition.y, drawPosition.z);
    fill(0);
    noStroke();
    sphere(5);
    popStyle();
    popMatrix();
  }

  public void draw() {
    drawSelf(position);
  }
}
