package camera3D;

public class CameraConfiguration {
    public float cameraPositionX = Float.NaN;
    public float cameraPositionY = Float.NaN;
    public float cameraPositionZ = Float.NaN;
    public float cameraTargetX = Float.NaN;
    public float cameraTargetY = Float.NaN;
    public float cameraTargetZ = Float.NaN;
    public float cameraUpX = Float.NaN;
    public float cameraUpY = Float.NaN;
    public float cameraUpZ = Float.NaN;

    public float fovy = Float.NaN;
    public float frustumLeft = Float.NaN;
    public float frustumRight = Float.NaN;
    public float frustumBottom = Float.NaN;
    public float frustumTop = Float.NaN;
    public float frustumNear = Float.NaN;
    public float frustumFar = Float.NaN;

    public int frameLimit = 0;

    /**
     * This determines if both the camera and perspective functions have been
     * called.
     * 
     * @return
     */
    public boolean isReady() {
        if (Float.isNaN(cameraPositionX) || Float.isNaN(fovy))
            return false;
        else
            return true;
    }
}
