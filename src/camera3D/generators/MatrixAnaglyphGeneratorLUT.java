package camera3D.generators;

import camera3D.generators.StereoscopicGenerator;
import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

/**
 * 
 * Optimized version of a general matrix anaglyph algorithm.
 * 
 * This works by precomputing a set of lookup tables for every possible color.
 * There is a small up-front cost but after that the performance speedup of the
 * generateCompositeFrame method is about 8-10x.
 * 
 * The lookup tables will use 128 MB of RAM.
 * 
 * @author James Schmitz
 *
 */
public class MatrixAnaglyphGeneratorLUT extends AnaglyphGenerator {

    private int[] leftLUT;
    private int[] rightLUT;

    public MatrixAnaglyphGeneratorLUT(AnaglyphMatrix left, AnaglyphMatrix right) {
        // Validate the inputs
        if (((left.r1c1 != 0 || left.r1c2 != 0 || left.r1c3 != 0) && (right.r1c1 != 0
                || right.r1c2 != 0 || right.r1c3 != 0))
                || ((left.r2c1 != 0 || left.r2c2 != 0 || left.r2c3 != 0) && (right.r2c1 != 0
                        || right.r2c2 != 0 || right.r2c3 != 0))
                || ((left.r3c1 != 0 || left.r3c2 != 0 || left.r3c3 != 0) && (right.r3c1 != 0
                        || right.r3c2 != 0 || right.r3c3 != 0))) {
            System.err.println("Invalid left and right matrix values.");
            System.err
                    .println("Each matrix row can be non-zero in at most one of the (left, right) matrix pair.");
            System.err
                    .println("Fix the matrix or use the MatrixAnaglyphGenerator class instead.");
            throw new RuntimeException("Invalid matrix inputs");
        }

        System.out.println("precomputing lookup tables...");

        leftLUT = preComputeMatrixLUT(left);
        rightLUT = preComputeMatrixLUT(right);

        System.out.println("done!");
    }

    public static StereoscopicGenerator createTrueAnaglyphGenerator() {
        return new MatrixAnaglyphGeneratorLUT(LEFT_TRUE_ANAGLYPH,
                RIGHT_TRUE_ANAGLYPH);
    }

    public static StereoscopicGenerator createGrayAnaglyphGenerator() {
        return new MatrixAnaglyphGeneratorLUT(LEFT_GRAY_ANAGLYPH,
                RIGHT_GRAY_ANAGLYPH);
    }

    public static StereoscopicGenerator createHalfColorAnaglyphGenerator() {
        return new MatrixAnaglyphGeneratorLUT(LEFT_HALF_COLOR_ANAGLYPH,
                RIGHT_HALF_COLOR_ANAGLYPH);
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        for (int ii = 0; ii < pixelDest.length; ++ii) {
            pixelDest[ii] = 0xFF000000 | leftLUT[pixelDest[ii] & 0x00FFFFFF]
                    | rightLUT[pixelStorage[0][ii] & 0x00FFFFFF];
        }
    }
}
