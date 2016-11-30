package camera3D.generators;

import processing.core.PApplet;

public class StereoscopicFrameSaver extends StereoscopicGenerator {

    private String leftFilename;
    private String rightFilename;
    private String leftAndRightFilename;

    /**
     * Save both left and right frames to the same directory.
     * 
     * Useful for making a Frame sequential movie file.
     * 
     * @param filename
     */
    public StereoscopicFrameSaver(String filename) {
        this.leftFilename = null;
        this.rightFilename = null;
        this.leftAndRightFilename = filename;
    }

    /**
     * Save left and right frames to two different directories.
     * 
     * Useful for making two movie files for the left and right perspectives.
     * 
     * @param leftFilename
     * @param rightFilename
     */
    public StereoscopicFrameSaver(String leftFilename, String rightFilename) {
        this.leftFilename = leftFilename;
        this.rightFilename = rightFilename;
        this.leftAndRightFilename = null;
    }

    public void generateCompositeFrame(int[] pixelDest, int[][] pixelStorage) {
        // do nothing
    }

    @Override
    public void completedDraw(int frameNum, PApplet parent) {
        if (leftFilename != null && rightFilename != null) {
            if (frameNum == 0)
                parent.saveFrame(rightFilename);
            else
                parent.saveFrame(leftFilename);
        } else if (leftAndRightFilename != null) {
            /*
             * frameNum == 0 // right frameNum == 1 // left
             * 
             * the '(1 - frameNum)' clause is so the frames are saved left frame
             * first.
             */
            parent.save(insertFrame(leftAndRightFilename, parent.frameCount * 2
                    + (1 - frameNum)));
        } else {
            // no filename to save to?
        }
    }

    /*
     * Copied from PApplet...
     */
    public String insertFrame(String what, int frameCount) {
        int first = what.indexOf('#');
        int last = what.lastIndexOf('#');

        if ((first != -1) && (last - first > 0)) {
            String prefix = what.substring(0, first);
            int count = last - first + 1;
            String suffix = what.substring(last + 1);
            return prefix + PApplet.nf(frameCount, count) + suffix;
        }
        return what; // no change
    }
}
