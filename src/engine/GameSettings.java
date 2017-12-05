package engine;

public final class GameSettings {

    private static boolean MAG_LINEAR = false;
    private static boolean MIN_TRILINEAR = false;
    private static int LOD_BIAS = 0;
    private static boolean MSAA = true;
    private static boolean FOG = true;


    private GameSettings(){}

    public static boolean isMagLinear(){
        return MAG_LINEAR;
    }

    public static boolean isMinTrilinear(){
        return MIN_TRILINEAR;
    }
    public static int getLodBias(){
        return LOD_BIAS;
    }
    public static boolean isMSAA(){
        return MSAA;
    }
    public static boolean isFOG() {
        return FOG;
    }

    public static void toggleMagLinear(){
        MAG_LINEAR = !MAG_LINEAR;
    }
    public static void toggleMinTrilinear(){
        MIN_TRILINEAR = !MIN_TRILINEAR;
    }
    public static void setLodBias(int val){
        LOD_BIAS = val;
    }
    public static void toggleMSAA(){
        MSAA = !MSAA;
    }
    public static void toggleFog() {
        GameSettings.FOG = !GameSettings.FOG;
    }
}
