package game.meshes;

public class PlaneMesh {

    private float[] positions;
    private float[] texCoords;
    private float[] normals;
    private int[] indices;

    public PlaneMesh(){
        positions = new float[] {
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        };

        texCoords = new float[]{};

        normals = new float[]{};

        indices = new int[] {
                0, 1, 3, 3, 1, 2,
        };
    }

    public float[] getPositions(){
        return positions;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices(){
        return indices;
    }

}
