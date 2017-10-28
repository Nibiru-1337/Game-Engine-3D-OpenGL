package game;

import engine.GameItem;
import engine.Utils;
import engine.Window;
import engine.graphix.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer3D {
    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;
    private ShaderProgram program;

    private float specularPower;


    public Renderer3D() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        // Create shader
        program = new ShaderProgram();
        program.createVertexShader(Utils.loadResource("src/resources/shaders/vertex.vs"));
        program.createFragmentShader(Utils.loadResource("src/resources/shaders/fragment.fs"));
        program.link();

        // Create uniforms for modelView and projection matrices and texture
        program.createUniform("modelView");
        program.createUniform("projection");
        program.createUniform("texture_sampler");
        // Create uniform for material
        program.createMaterialUniform("material");
        // Create lighting related uniforms
        program.createUniform("specularPower");
        program.createUniform("ambientLight");
        program.createPointLightUniform("pointLight");
        program.createDirectionalLightUniform("directionalLight");
    }



    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight,
                       PointLight pointLight, DirectionalLight directionalLight) {
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

        // Update Light Uniforms
        program.setUniform("ambientLight", ambientLight);
        program.setUniform("specularPower", specularPower);
        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        program.setUniform("pointLight", currPointLight);
        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        program.setUniform("directionalLight", currDirLight);

        program.setUniform("texture_sampler", 0);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            program.setUniform("modelView", modelViewMatrix);
            // Render the mesh for this game item
            program.setUniform("material", mesh.getMaterial());
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
