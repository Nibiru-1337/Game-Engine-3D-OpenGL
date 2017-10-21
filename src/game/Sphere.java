package game;

import java.util.ArrayList;

public class Sphere {

    private ArrayList<Float> positions = new ArrayList<>();
    private ArrayList<Float> colors = new ArrayList<>();
    private ArrayList<Integer> indices = new ArrayList<>();

    public Sphere(float radius) {
        generate(
                radius,
                8, 6,
                0, Math.PI * 2,
                0, Math.PI);
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
                colors.add(0.5f);
                colors.add(0.5f);
                colors.add(0.5f);

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
    }

    public float[] getVertices(){
        float[] floatArray = new float[positions.size()];
        int i = 0;
        for (Float f : positions) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    public float[] getColors(){
        float[] floatArray = new float[colors.size()];
        int i = 0;
        for (Float f : colors) {
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
