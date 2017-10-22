package game;

public class PlaneMesh {

    private float[] positions;
    private float[] colors;
    private int[] indices;

    public PlaneMesh(){
        positions = new float[] {
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        };
        colors = new float[]{
                0.0f, 0.4f, 0.6f,
                0.0f, 0.4f, 0.6f,
                0.0f, 0.4f, 0.6f,
                0.0f, 0.4f, 0.6f,
        };
        indices = new int[] {
                0, 1, 3, 3, 1, 2,
        };
    }

    public float[] getPositions(){
        return positions;
    }

    public float[] getColors(){
        return colors;
    }

    public int[] getIndices(){
        return indices;
    }

}
