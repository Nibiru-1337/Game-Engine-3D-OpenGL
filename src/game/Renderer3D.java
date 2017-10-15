package game;

import engine.GameItem;
import engine.Utils;
import engine.Window;
import engine.graphix.Camera;
import engine.graphix.Shader;
import engine.graphix.ShaderProgram;
import engine.graphix.Transformation;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer3D {
    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;
    private GameItem[] gameItems;
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;

    public Renderer3D() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        // Create shader
        program = new ShaderProgram();
        program.createVertexShader(Utils.loadResource("src/shaders/vertex.vs"));
        program.createFragmentShader(Utils.loadResource("src/shaders/fragment.fs"));
        program.link();

        // Create uniforms for modelView and projection matrices and texture
        program.createUniform("modelView");
        program.createUniform("projection");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        program.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        program.setUniform("projection", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            program.setUniform("modelView", modelViewMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }

        program.unbind();
    }

    public void cleanup() {
        if (program != null) {
            program.cleanup();
        }
    }
}
