package camera3D.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import camera3D.CameraConfiguration;

/**
 * 
 * @author James Schmitz
 * 
 *         Base class for all generators.
 *
 */
public abstract class Generator {

    private int threadCount;
    private ExecutorService executor;
    private Collection<Future<?>> futures;

    Generator() {
        initExecutor(Runtime.getRuntime().availableProcessors() / 2);
    }

    protected CameraConfiguration config;

    /**
     * The number of components in the composite image, ie, the number of times
     * to call the draw method.
     * 
     * @return int
     */
    abstract public int getComponentCount();

    /**
     * Give each component a name, such as "left" and "right" for anaglyph
     * images.
     * 
     * @param frameNum
     * @return
     */
    abstract public String getComponentFrameName(int frameNum);

    /**
     * Communicate to Camera3D instance if frame pixels should be copied to
     * pixelStorage.
     * 
     * This will almost always be true but some Generators can improve
     * performance by skipping the loadPixels() call and the System.arraycopy()
     * 
     * @param frameNum
     * @return
     */
    public boolean copyFrameNumber(int frameNum) {
        return true;
    }

    /**
     * Notify renderer that something about the camera changed. For example, the
     * stereoscopic generators need to do some recalculations when the camera
     * moves.
     * 
     * @param config
     */
    public void notifyCameraConfigChange(CameraConfiguration config) {
        this.config = config;

        if (config != null && config.isReady())
            recalculateCameraSettings();
    }

    /**
     * Perform renderer recalculations due to camera config change.
     */
    abstract protected void recalculateCameraSettings();

    /**
     * This is called once before each call to the user's draw method. Typically
     * the generators need to move the camera around or change some settings.
     * 
     * @param frameNum
     * @param parent
     */
    abstract public void prepareForDraw(int frameNum, PApplet parent);

    /**
     * Combine the component frames into one composite frame.
     * 
     * @param pixels
     * @param pixelsAlt
     */
    abstract public void generateCompositeFrame(int[] pixelDest,
            int[][] pixelStorage);

    /**
     * Call generateCompositeFrame several times with pixelStorage setup as
     * empty (black) images for all but one of the pixel arrays. This lets us
     * see how the generator is modifying each of the components. This is super
     * helpful for debugging a new generator.
     * 
     * For most generators this will work as intended. If for some reason it
     * does not work for your custom generator, override this method and write
     * something that works correctly.
     * 
     * @param pixelDest
     * @param pixelStorage
     * @param parent
     * @param parentClassName
     * @param saveFrameLocation
     */
    public void generateCompositeFrameAndSaveComponents(int[] pixelDest,
            int[][] pixelStorage, PApplet parent, String parentClassName,
            String saveFrameLocation) {

        int[][] pixelStorageCopy = new int[pixelStorage.length][pixelStorage[0].length];

        for (int i = 0; i < getComponentCount(); ++i) {
            PImage frame = parent.createImage(parent.width, parent.height,
                    PConstants.RGB);
            frame.loadPixels();

            // first, prepare pixelStorageCopy and frame pixels
            for (int j = 0; j < getComponentCount(); ++j) {
                if (i == j)
                    System.arraycopy(pixelStorage[j], 0, pixelStorageCopy[j],
                            0, pixelDest.length);
                else
                    System.arraycopy(frame.pixels, 0, pixelStorageCopy[j], 0,
                            pixelDest.length);
            }
            System.arraycopy(pixelStorageCopy[getComponentCount() - 1], 0,
                    frame.pixels, 0, pixelDest.length);

            // now generate the composite frame
            generateCompositeFrame(frame.pixels, pixelStorageCopy);
            frame.updatePixels();

            frame.save(parent.insertFrame(saveFrameLocation + "####-"
                    + parentClassName + "-" + getComponentFrameName(i)
                    + "-component-modified.png"));
        }

        // We have to call generateCompositeFrame one more time so that the rest
        // of the Camera3D draw method functions correctly.
        System.arraycopy(pixelStorage[getComponentCount() - 1], 0, pixelDest,
                0, pixelDest.length);
        generateCompositeFrame(pixelDest, pixelStorage);
    }

    /**
     * This is called once after each call to the user's draw method. Typically
     * this is not needed.
     * 
     * @param frameNum
     * @param parent
     */
    abstract public void completedDraw(int frameNum, PApplet parent);

    /**
     * This is called after the last call to the user's draw method and before
     * the user's postDraw method. This should put the camera back so other
     * libraries like ControlP5 can function correctly.
     * 
     * @param parent
     */
    abstract public void cleanup(PApplet parent);

    /**
     * Initialize Parallel Executor for pixel copying.
     * 
     * @param threadCount
     */
    protected void initExecutor(int threadCount) {
        this.threadCount = Math.max(1, threadCount);
        this.executor = Executors.newFixedThreadPool(this.threadCount);
        this.futures = new ArrayList<Future<?>>();
    }

    /**
     * Functional Interface for Passing Pixel Copy Instructions.
     *
     */
    @FunctionalInterface
    protected interface PixelCopyTask {
        void run(int start, int end);
    }

    /**
     * Divide up pixels and execute groups in parallel
     * 
     * @param length
     * @param task
     */
    protected void executeTask(int length, PixelCopyTask task) {
        // submit runnables for subsets of pixels
        for (int j = 0; j < threadCount; ++j) {
            final int start = (int) (length * (j / (float) threadCount));
            final int end = (int) (length * ((j + 1) / (float) threadCount));
            futures.add(executor.submit(() -> {
                task.run(start, end);
            }));
        }

        // wait for all to complete
        futures.stream().forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        futures.clear();
    }

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @return
     */
    protected float clip(float x) {
        return Math.min(Math.max(x, 0), 1);
    }

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @param min
     * @param max
     * @return
     */
    protected float clip(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    /**
     * Copied from PApplet
     * 
     * @param what
     * @param frameCount
     * @return
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
