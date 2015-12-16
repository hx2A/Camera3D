public class Shape {
  private PVector position;
  private PVector orientation;
  private PVector velocity;
  private PVector rotation;
  color myColor;

  public Shape(PVector start, PVector velocity, PVector orientation,
      PVector rotation, color myColor) {
    this.position = start;
    this.velocity = velocity;
    this.orientation = orientation;
    this.rotation = rotation;
    this.myColor = myColor;
  }

  public void update() {
    position.add(velocity);
    orientation.add(rotation);

    int barrier = 20;
    if (position.x > xMax - barrier) {
      velocity.x = -velocity.x;
    } else if (position.x < xMin + barrier) {
      velocity.x = -velocity.x;
    }
    if (position.y > yMax - barrier) {
      velocity.y = -velocity.y;
    } else if (position.y < yMin + barrier) {
      velocity.y = -velocity.y;
    }
    if (position.z > zMaxShape - barrier) {
      velocity.z = -velocity.z;
    } else if (position.z < zMin + barrier) {
      velocity.z = -velocity.z;
    }
  }

  private void drawSelf(PVector drawPosition) {
    pushMatrix();
    translate(drawPosition.x, drawPosition.y, drawPosition.z);
    rotateX(radians(orientation.x));
    rotateY(radians(orientation.y));
    rotateZ(radians(orientation.z));
    fill(myColor);
    box(50);
    popMatrix();
  }

  public void draw() {
    drawSelf(position);
  }
}
