/*
This Generator extends the StereoscopicGenerator class so it
inherits all of that code. I only need to implement the 
generateCompositeFrame method, which is used to combine the
component frames into one composite frame. In this example it
is using one of the two component frames as the composite frame.
For the a bitmask anaglyph, that function will look at each pixel
in both component frames and combine the red, green and blue values
to make a new color.

Custom generators need not extend the StereoscopicGenerator, but
if you don't you will need to inherit more abstract methods.
Look at the camera3D.generators.Generator.java source code and
comments to learn more.
*/
import camera3D.generators.*;

public class WiggleStereoscopyGenerator extends StereoscopicGenerator {

  private int counter;
  private int multiple;

  public WiggleStereoscopyGenerator(int multiple) {
    this.multiple = multiple;
    this.counter = 0;
  }

  /*
  This method does not return anything. You must put the desired
  result in the pixelDest[] array.
  
  The pixelStorage[][] array contains the pixels for each
  component frame. It happens that pixelStorage[1][] will be a
  copy of pixelDest[]. 
  */
  public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
    counter++;

    if ((counter / multiple) % 2 == 0) {
      System.arraycopy(pixelStorage[0], 0, pixelDest, 0,
          pixelDest.length);
    }
  }
}