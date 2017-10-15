package graphic;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh {

    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;
    private final VertexBufferObject vboIdx;
    private final VertexBufferObject vboCol;
    private final int vtxCount;

    public Mesh(float[] positions, float[] colors, int[] indices){
        vao = new VertexArrayObject();
        vao.bind();
        vtxCount = indices.length;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Vertex data
            FloatBuffer vertices = stack.mallocFloat(positions.length);
            vertices.put(positions).flip();
            vbo = new VertexBufferObject();
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            // Color data
            FloatBuffer colorBuf = stack.mallocFloat(colors.length);
            colorBuf.put(colors).flip();
            vboCol = new VertexBufferObject();
            vboCol.bind(GL_ARRAY_BUFFER);
            vboCol.uploadData(GL_ARRAY_BUFFER, colorBuf, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            // Index data
            IntBuffer indiciesBuf = stack.mallocInt(vtxCount);
            indiciesBuf.put(indices).flip();
            vboIdx = new VertexBufferObject();
            vboIdx.bind(GL_ELEMENT_ARRAY_BUFFER);
            vboIdx.uploadData(GL_ELEMENT_ARRAY_BUFFER, indiciesBuf, GL_STATIC_DRAW);}

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
    }

    public void render(){
        // Draw the mesh
        bind();
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public int getVaoId() {
        return vao.getID();
    }

    public int getVertexCount() {
        return vtxCount;
    }

    public void bind(){
        vao.bind();
    }

    public void delete() {
        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbo.delete();
        vboIdx.delete();
        vboCol.delete();
        // Delete the VAO
        vao.delete();
    }
}
