package state;

import graphic.*;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * This state is for the rendering tutorial.
 *
 * @author Heiko Brumme
 */
public class State3D implements State {

    private final CharSequence vertexSource
            = "#version 330\n"
            + "\n"
            + "layout (location=0) in vec3 position;\n"
            + "layout (location=1) in vec3 color;\n"
            + "\n"
            + "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 world;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            + "    vertexColor = color;\n"
            + "    mat4 mvp = projection * world;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    private final CharSequence fragmentSource
            = "#version 330\n"
            + "\n"
            + "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(vertexColor, 1.0);\n"
            + "}";

    private Transformation transformation;
    private GameItem[] gameItems;
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;

    private float previousAngle = 0f;
    private float angle = 0f;
    private final float angelPerSecond = 90f;

    private static final float FOV = (float) Math.toRadians(60f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

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
        glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);
        program.use();

        updateProjection();

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Update rotation angle
            float rotation = gameItem.getRotation().x + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            gameItem.setRotation(rotation, rotation, rotation);

            // Set world matrix for this item
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                            gameItem.getPosition(),
                            gameItem.getRotation(),
                            gameItem.getScale());
            program.setUniform("world", worldMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }
    }

    @Override
    public void enter() {
        transformation = new Transformation();
        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };
        Mesh mesh = new Mesh(positions, colours, indices);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[] { gameItem };

        /* Load shaders */
        vertexShader = Shader.createShader(GL_VERTEX_SHADER, vertexSource);
        fragmentShader = Shader.createShader(GL_FRAGMENT_SHADER, fragmentSource);

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.link();
        program.use();

        //specifyVertexAttributes();

        // Create uniforms
        try {
            //Matrix4f model = new Matrix4f();
            //Matrix4f view = new Matrix4f();
            Matrix4f projection = new Matrix4f();
            //program.createUniform("model");
            //program.setUniform("model", model);
            //program.createUniform("view");
            //program.setUniform("view", view);
            program.createUniform("projection");
            program.createUniform("world");
            //program.setUniform("projection", projection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateProjection();
    }

    private void updateProjection(){
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
        //Matrix4f projection = new Matrix4f().ortho(-ratio, ratio, -1f, 1f, -1f, 1f);
        Matrix4f projection = transformation.getProjectionMatrix(FOV, ratio, Z_NEAR, Z_FAR);
        program.setUniform("projection", projection);
    }

    @Override
    public void exit() {
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().delete();
        }
        vertexShader.delete();
        fragmentShader.delete();
        program.delete();
    }

}