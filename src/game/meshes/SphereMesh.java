package game.meshes;

import org.joml.GeometryUtils;
import org.joml.Vector3f;

import java.util.ArrayList;

public class SphereMesh {

    private ArrayList<Float> positions = new ArrayList<>();
    private ArrayList<Float> normals = new ArrayList<>();
    private ArrayList<Float> texCoords = new ArrayList<>();
    private ArrayList<Integer> indices = new ArrayList<>();


    public SphereMesh(float radius) {
        generate(
                radius,
                32, 32,
                0, Math.PI * 2,
                Math.PI/2, Math.PI);
    }

    private void generate(
            float radius,
            int widthSegments, int heightSegments,
            double phiStart, double phiLength,
            double thetaStart, double thetaLength) {

        double thetaEnd = thetaStart + thetaLength;
        int vertexCount = ( ( widthSegments + 1 ) * ( heightSegments + 1 ) );

        int index = 0;
        ArrayList<ArrayList<Integer>> vertices = new ArrayList<>();

        for (int y = 0; y <= heightSegments; y ++ ) {

            ArrayList<Integer> verticesRow = new ArrayList<>();
            float v = y / (float)heightSegments;

            for (int x = 0; x <= widthSegments; x ++ ) {

                float u = x/(float)widthSegments;
                float px = (float)(- radius * Math.cos( phiStart + u * phiLength ) * Math.sin( thetaStart + v * thetaLength));
                float py = (float)(radius * Math.cos( thetaStart + v * thetaLength));
                float pz = (float)(radius * Math.sin( phiStart + u * phiLength ) * Math.sin( thetaStart + v * thetaLength));

                positions.add(px);
                positions.add(py);
                positions.add(pz);



                verticesRow.add(index);
                index++;
            }

            vertices.add(verticesRow);
        }

        for ( int y = 0; y < heightSegments; y ++ ) {
            for ( int x = 0; x < widthSegments; x ++ ) {

                int v1 = vertices.get(y).get(x + 1);
                int v2 = vertices.get(y).get(x);
                int v3 = vertices.get(y+1).get(x);
                int v4 = vertices.get(y+1).get(x + 1);

                if ( y != 0 || thetaStart > 0 ) {
                    indices.add(v1);
                    indices.add(v2);
                    indices.add(v4);
                }

                if ( y != heightSegments - 1 || thetaEnd < Math.PI ) {
                    indices.add(v2);
                    indices.add(v3);
                    indices.add(v4);
                }
            }
        }

        addNormals();

        addTexCoords();

    }

    private void addNormals(){

        for (int vIdx = 0; vIdx < positions.size(); vIdx += 3){
            Vector3f v1 = new Vector3f(positions.get(vIdx), positions.get(vIdx+1), positions.get(vIdx+2));

            Vector3f normal = new Vector3f(v1).normalize();
            normals.add(normal.x);
            normals.add(normal.y);
            normals.add(normal.z);
        }
    }

    private void addTexCoords(){

        for (int vIdx = 0; vIdx < positions.size(); vIdx += 3){
            texCoords.add(0.0f);
            texCoords.add(0.0f);
        }
    }

    public float[] getVertices(){
        float[] floatArray = new float[positions.size()];
        int i = 0;
        for (Float f : positions) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    public float[] getTexCoords(){
        float[] floatArray = new float[texCoords.size()];
        int i = 0;
        for (Float f : texCoords) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    public float[] getNormals(){
        float[] floatArray = new float[normals.size()];
        int i = 0;
        for (Float f : normals) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    public int[] getIndices(){
        int[] intArray = new int[indices.size()];
        int i = 0;
        for (Integer f : indices) {
            intArray[i++] = f; // Or whatever default you want.
        }
        return intArray;
    }
}
