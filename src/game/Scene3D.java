package game;

import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import engine.graphix.*;
import game.meshes.PlaneMesh;
import game.meshes.SphereMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Scene3D implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer3D renderer;
    private final Camera camera;
    private GameItem[] gameItems;
    private static final float CAMERA_POS_STEP = 0.05f;

    private Vector3f ambientLight;
    private PointLight pointLight;

    Scene3D(){
        renderer = new Renderer3D();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }


    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;
        //Texture texture = new Texture("/textures/grassblock.png");
        //load meshes

        SphereMesh s = new SphereMesh(1f);
        Mesh sphereMesh = new Mesh(s.getVertices(),s.getTexCoords(), s.getNormals(), s.getIndices());
        Material sand = new Material(new Vector4f(0.9f, 0.85f, 0.5f,0.5f), reflectance);
        sphereMesh.setMaterial(sand);

        PlaneMesh plane = new PlaneMesh();
        Mesh floorMesh = new Mesh(plane.getPositions(), plane.getTexCoords(),plane.getNormals(), plane.getIndices());
        Material blue = new Material(new Vector4f(0.0f,0.4f,0.6f, 0.5f), reflectance);
        floorMesh.setMaterial(blue);

        Mesh boxMesh = OBJLoader.loadMesh("src/resources/models/cube.obj");
        Material wood = new Material(new Vector4f(0.54f, 0.27f, 0.07f, 0.5f), reflectance);
        boxMesh.setMaterial(wood);

        Mesh palmMesh = OBJLoader.loadMesh("src/resources/models/palm_tree.obj");
        Material green = new Material(new Vector4f(0.33f, 0.41f, 0.18f, 0.5f), reflectance);
        palmMesh.setMaterial(green);

        //make game item objects
        GameItem palm1 = new GameItem(palmMesh);
        palm1.setPosition(-0.5f,0,-5);
        palm1.setRotation(0,25f,-10f);
        palm1.setScale(new Vector3f(0.005f));

        GameItem palm2 = new GameItem(palmMesh);
        palm2.setPosition(0.3f,0,-4);
        palm2.setScale(new Vector3f(0.005f));

        GameItem box1 = new GameItem(boxMesh);
        box1.setScale(new Vector3f(0.2f));
        box1.setPosition(0, 0.7f, -5);

        GameItem island = new GameItem(sphereMesh);
        island.setScale(new Vector3f(3.0f, 2.0f, 2.0f));
        island.setPosition(0, -1.5f, -5);
        island.setRotation(0,0,180f);

        GameItem sea = new GameItem(floorMesh);
        sea.setScale(new Vector3f(20.0f));
        sea.setPosition(0, -0.1f, -2.5f);
        sea.setRotation(90f,0f,0);

        gameItems = new GameItem[]{sea, island, box1, palm1, palm2};
        setUpLight();
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
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
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
        renderer.render(window, camera, gameItems, ambientLight, pointLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().delete();
        }
    }

    private void setUpLight() {
        ambientLight = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 3, -5);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
    }
}
