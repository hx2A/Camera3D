package camera3D.generators;

import camera3D.generators.util.AnaglyphMatrix;
import camera3D.generators.util.ColorVector;

/**
 * Unoptimized implementation of the Dubois Anaglyph algorithm.
 * 
 * More information can be found here:
 * 
 * http://www.site.uottawa.ca/~edubois/anaglyph/
 * 
 * particularly:
 * 
 * http://www.site.uottawa.ca/~edubois/anaglyph/LeastSquaresHowToPhotoshop.pdf
 * http://www.site.uottawa.ca/~edubois/icassp01/anaglyphdubois.pdf
 * 
 * This generator is slow. Please don't use it. This is here mainly for
 * comparison purposes with the optimized versions.
 * 
 * @author James Schmitz
 *
 */

public class DuboisAnaglyphGeneratorNaive extends AnaglyphGenerator {

    private AnaglyphMatrix left;
    private AnaglyphMatrix right;

    public DuboisAnaglyphGeneratorNaive(AnaglyphMatrix left,
            AnaglyphMatrix right) {
        this.left = left;
        this.right = right;
    }

    public static StereoscopicGenerator createRedCyanGenerator() {
        return new DuboisAnaglyphGeneratorNaive(LEFT_DUBOIS_REDCYAN,
                RIGHT_DUBOIS_REDCYAN);
    }

    public static StereoscopicGenerator createMagentaGreenGenerator() {
        return new DuboisAnaglyphGeneratorNaive(LEFT_DUBOIS_MAGENTAGREEN,
                RIGHT_DUBOIS_MAGENTAGREEN);
    }

    public static StereoscopicGenerator createAmberBlueGenerator() {
        return new DuboisAnaglyphGeneratorNaive(LEFT_DUBOIS_AMBERBLUE,
                RIGHT_DUBOIS_AMBERBLUE);
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {

        for (int ii = 0; ii < pixelDest.length; ++ii) {
            float Rr = removeGammaCorrectionStandardRGB(((pixelDest[ii] & 0x00FF0000) >> 16) / 255f);
            float Rg = removeGammaCorrectionStandardRGB(((pixelDest[ii] & 0x0000FF00) >> 8) / 255f);
            float Rb = removeGammaCorrectionStandardRGB((pixelDest[ii] & 0x000000FF) / 255f);
            float Lr = removeGammaCorrectionStandardRGB(((pixelStorage[0][ii] & 0x00FF0000) >> 16) / 255f);
            float Lg = removeGammaCorrectionStandardRGB(((pixelStorage[0][ii] & 0x0000FF00) >> 8) / 255f);
            float Lb = removeGammaCorrectionStandardRGB((pixelStorage[0][ii] & 0x000000FF) / 255f);

            ColorVector AR = right.rightMult(new ColorVector(Rr, Rg, Rb));
            ColorVector AL = left.rightMult(new ColorVector(Lr, Lg, Lb));

            float Ar = applyGammaCorrectionStandardRGB(clip(clip(AR.red)
                    + clip(AL.red)));
            float Ag = applyGammaCorrectionStandardRGB(clip(clip(AR.green)
                    + clip(AL.green)));
            float Ab = applyGammaCorrectionStandardRGB(clip(clip(AR.blue)
                    + clip(AL.blue)));

            pixelDest[ii] = 0xFF000000 | (((int) (Ar * 255)) << 16)
                    | (((int) (Ag * 255)) << 8) | ((int) (Ab * 255));
        }
    }

}
