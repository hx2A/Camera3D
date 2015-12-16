class Shape {
  private float offset;
  private int c;

  public Shape(float offset) {
    this.offset = offset;
    this.c = color(random(255), 100, 100);
  }

  public void draw(float z) {
    float angle = z - offset;
    float zloc = z - offset - 100;

    pushMatrix();

    translate(0, 0, zloc);
    rotateZ(radians(angle));
    translate(75, 0, 0);
    fill(c);
    box(20);

    popMatrix();
  }
}