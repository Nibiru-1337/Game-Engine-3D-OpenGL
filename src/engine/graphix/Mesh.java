package engine.graphix;

import engine.items.GameItem;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh {

    private final VertexArrayObject vao;
    private ArrayList<VertexBufferObject> vboList;
    private final int vtxCount;
    private Material material;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        vao = new VertexArrayObject();
        vao.bind();
        vtxCount = indices.length;
        vboList = null;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vboList = new ArrayList<>(5);
            // Vertex data
            FloatBuffer vertices = stack.mallocFloat(positions.length);
            vertices.put(positions).flip();
            VertexBufferObject vboPos = new VertexBufferObject();
            vboPos.bind(GL_ARRAY_BUFFER);
            vboPos.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            vboList.add(vboPos);

            // Texture coordinates VBO
            FloatBuffer textCoordsBuffer = stack.mallocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            VertexBufferObject vboTex = new VertexBufferObject();
            vboTex.bind(GL_ARRAY_BUFFER);
            vboTex.uploadData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            vboList.add(vboTex);

            // Vertex normals VBO
            FloatBuffer vecNormalsBuffer = stack.mallocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            VertexBufferObject vboNor = new VertexBufferObject();
            vboNor.bind(GL_ARRAY_BUFFER);
            vboNor.uploadData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
            vboList.add(vboNor);

            // Index data
            IntBuffer indiciesBuf = stack.mallocInt(vtxCount);
            indiciesBuf.put(indices).flip();
            VertexBufferObject vboIdx = new VertexBufferObject();
            vboIdx.bind(GL_ELEMENT_ARRAY_BUFFER);
            vboIdx.uploadData(GL_ELEMENT_ARRAY_BUFFER, indiciesBuf, GL_STATIC_DRAW);
            vboList.add(vboIdx);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initRender() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    private void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        initRender();

        for (GameItem gameItem : gameItems) {
            // Set up data required by gameItem
            consumer.accept(gameItem);
            // Render this game item
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
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

    public void cleanUp() {
        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (VertexBufferObject vbo : vboList) {
            vbo.delete();
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        vao.delete();
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (VertexBufferObject vbo : vboList) {
            vbo.delete();
        }

        // Delete the VAO
        vao.delete();
    }
}
