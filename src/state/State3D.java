package state;

import graphic.*;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * This state is for the rendering tutorial.
 *
 * @author Heiko Brumme
 */
public class State3D implements State {

    private final CharSequence vertexSource
            = "#version 150 core\n"
            //= "#version 330\n"
            + "\n"
            + "in vec3 position;\n"
            //+ "layout (location=0) in vec3 position;\n"
            //+ "layout (location=1) in vec3 color;\n"
            + "\n"
            //+ "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            //+ "    vertexColor = color;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    private final CharSequence fragmentSource
            = "#version 150 core\n"
            //= "#version 330\n"
            + "\n"
            //+ "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(0.5,0.5,0.5, 1.0);\n"
            + "}";

    private Mesh mesh;
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

        mesh.bind();
        program.use();

        Matrix4f model = new Matrix4f().identity();
        program.setUniform(uniModel, model);

        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public void enter() {

        // Load model
        float[] positions = new float[]{
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        mesh = new Mesh(positions, colours, indices);

        /* Load shaders */
        vertexShader = Shader.createShader(GL_VERTEX_SHADER, vertexSource);
        fragmentShader = Shader.createShader(GL_FRAGMENT_SHADER, fragmentSource);

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.link();
        program.use();

        specifyVertexAttributes();

        /* Get uniform location for the model matrix */
        uniModel = program.getUniformLocation("model");

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);

        /* Get width and height for calculating the ratio */
        float ratio;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, width, height);
            ratio = width.get() / (float) height.get();
        }

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = new Matrix4f().ortho(-ratio, ratio, -1f, 1f, -1f, 1f);
        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);

    }

    @Override
    public void exit() {
        mesh.delete();
        vertexShader.delete();
        fragmentShader.delete();
        program.delete();
    }

    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        //program.pointVertexAttribute(posAttrib, 3, 0, 0);
        program.pointVertexAttribute(posAttrib, 3, 6 * Float.BYTES, 0);
        // Specify color pointer
        //int colAttrib = program.getAttributeLocation("color");
        //program.enableVertexAttribute(colAttrib);
        //program.pointVertexAttribute(colAttrib, 3, 0, 0);
    }

}