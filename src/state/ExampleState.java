package state;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import graphic.Shader;
import graphic.ShaderProgram;
import graphic.VertexArrayObject;
import graphic.VertexBufferObject;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * This state is for the rendering tutorial.
 *
 * @author Heiko Brumme
 */
public class ExampleState implements State {

    public static final CharSequence vertexSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 position;\n"
            + "in vec3 color;\n"
            + "\n"
            + "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            + "    vertexColor = color;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    public static final CharSequence fragmentSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(vertexColor, 1.0);\n"
            + "}";

    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;

    private int uniModel;
    private float previousAngle = 0f;
    private float angle = 0f;
    private final float angelPerSecond = 90f;

    @Override
    public void input() {
        /* Nothing to do here */
    }

    @Override
    public void update(float delta) {
        previousAngle = angle;
        angle += delta * angelPerSecond;
    }

    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT);

        vao.bind();
        program.use();

        float lerpAngle = (1f - alpha) * previousAngle + alpha * angle;

        Matrix4f model = new Matrix4f().rotate(lerpAngle, 0f, 0f, 1f);
        program.setUniform(uniModel, model);

        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    @Override
    public void enter() {
        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Vertex data */
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            /* Generate Vertex Buffer Object */
            vbo = new VertexBufferObject();
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        /* Load shaders */
        vertexShader = Shader.createShader(GL_VERTEX_SHADER, vertexSource);
        fragmentShader = Shader.createShader(GL_FRAGMENT_SHADER, fragmentSource);

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.bindFragmentDataLocation(0, "fragcolor");
        program.link();
        program.use();

        specifyVertexAttributes();
        /*
        uniModel = program.createUniform("model");

        Matrix4f view = new Matrix4f();
        int uniView = program.createUniform("view");
        program.setUniform(uniView, view);

        float ratio;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, width, height);
            ratio = width.get() / (float) height.get();
        }

        Matrix4f projection = new Matrix4f().ortho(-ratio, ratio, -1f, 1f, -1f, 1f);
        int uniProjection = program.createUniform("projection");
        program.setUniform(uniProjection, projection);*/
    }

    @Override
    public void exit() {
        vao.delete();
        vbo.delete();
        vertexShader.delete();
        fragmentShader.delete();
        program.delete();
    }

    /**
     * Specifies the vertex attributes.
     */
    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 6 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 6 * Float.BYTES, 3 * Float.BYTES);
    }

}