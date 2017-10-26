package game.meshes;

import org.joml.GeometryUtils;
import org.joml.Vector3f;

public class PlaneMesh {

    private float[] positions;
    private float[] texCoords;
    private float[] normals;
    private int[] indices;

    public PlaneMesh(){
        Vector3f v1= new Vector3f(-0.5f,  0.5f, 0.0f);
        Vector3f v2 = new Vector3f(-0.5f, -0.5f, 0.0f);
        Vector3f v3 = new Vector3f(0.5f, -0.5f, 0.0f);
        Vector3f v4 = new Vector3f(0.5f,  0.5f, 0.0f);
        positions = new float[] {
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                v3.x, v3.y, v3.z,
                v4.x, v4.y, v4.z,
        };

        texCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                0.1f, 0.0f,
                0.1f, 1.0f,
        };

        Vector3f normal = new Vector3f();
        GeometryUtils.normal(v1, v2, v3, normal);
        normals = new float[]{
                normal.x, normal.y, normal.z,
                normal.x, normal.y, normal.z,
                normal.x, normal.y, normal.z,
                normal.x, normal.y, normal.z,
        };

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
