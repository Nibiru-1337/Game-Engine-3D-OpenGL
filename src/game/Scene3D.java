package game;

import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import engine.graphix.Camera;
import engine.graphix.Mesh;
import engine.graphix.OBJLoader;
import game.meshes.PlaneMesh;
import game.meshes.SphereMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Scene3D implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer3D renderer;
    private final Camera camera;
    private GameItem[] gameItems;
    private static final float CAMERA_POS_STEP = 0.05f;

    Scene3D(){
        renderer = new Renderer3D();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }


    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        //Texture texture = new Texture("/textures/grassblock.png");
        //load meshes
        SphereMesh s = new SphereMesh(1f);
        Mesh sphereMesh = new Mesh(s.getVertices(),new float[0], new float[0], s.getIndices());
        sphereMesh.setColour(new Vector3f(0.9f, 0.85f, 0.5f));

        PlaneMesh plane = new PlaneMesh();
        Mesh floorMesh = new Mesh(plane.getPositions(), plane.getTexCoords(),plane.getNormals(), plane.getIndices());
        floorMesh.setColour(new Vector3f(0.0f,0.4f,0.6f));

        Mesh boxMesh = OBJLoader.loadMesh("src/resources/models/cube.obj");
        boxMesh.setColour(new Vector3f(0.54f, 0.27f, 0.07f));

        Mesh palmMesh = OBJLoader.loadMesh("src/resources/models/palm_tree.obj");
        palmMesh.setColour(new Vector3f(0.33f, 0.41f, 0.18f));

        //make game item objects
        GameItem palm1 = new GameItem(palmMesh);
        palm1.setPosition(-0.5f,0,-5);
        palm1.setRotation(0,25f,-10f);
        palm1.setScale(0.005f);

        GameItem palm2 = new GameItem(palmMesh);
        palm2.setPosition(0.3f,0,-4);
        palm2.setScale(0.005f);

        GameItem box1 = new GameItem(boxMesh);
        box1.setScale(0.2f);
        box1.setPosition(0, 0.7f, -5);

        GameItem island = new GameItem(sphereMesh);
        island.setScale(2.0f);
        island.setPosition(0, -1.5f, -5);
        island.setRotation(0,0,180f);

        GameItem sea = new GameItem(floorMesh);
        sea.setScale(20.0f);
        sea.setPosition(0, -0.1f, -2.5f);
        sea.setRotation(90f,0f,0);

        gameItems = new GameItem[]{sea, island, box1, palm1, palm2 };
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            //if (camera.getPosition().y > 0)
                cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().delete();
        }
    }
}
