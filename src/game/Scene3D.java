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
    private DirectionalLight directionalLight;
    private float lightAngle;
    private float lightLamp;

    Scene3D(){
        renderer = new Renderer3D();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
        lightLamp = 1.0f;
    }


    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;
        //Texture texture = new Texture("/textures/grassblock.png");
        //load meshes

        SphereMesh s = new SphereMesh(1f);
        Mesh sphereMesh = new Mesh(s.getVertices(),s.getTexCoords(), s.getNormals(), s.getIndices());
        Material sand = new Material(new Vector4f(0.9f, 0.85f, 0.5f,1f), 0.25f );
        sphereMesh.setMaterial(sand);

        PlaneMesh plane = new PlaneMesh();
        Mesh floorMesh = new Mesh(plane.getPositions(), plane.getTexCoords(),plane.getNormals(), plane.getIndices());
        Material blue = new Material(new Vector4f(0.0f,0.4f,0.6f, 1f), reflectance);
        floorMesh.setMaterial(blue);

        //Mesh boxMesh = OBJLoader.loadMesh("src/resources/models/cube.obj");
        Material wood = new Material(new Vector4f(0.54f, 0.27f, 0.07f, 1f), reflectance);
        //boxMesh.setMaterial(wood);

        Mesh palmMesh = OBJLoader.loadMesh("src/resources/models/palm_tree.obj");
        Material green = new Material(new Vector4f(0.33f, 0.41f, 0.18f, 1f), reflectance);
        palmMesh.setMaterial(green);


        Mesh pierMesh = OBJLoader.loadMesh("src/resources/models/pier.obj");
        pierMesh.setMaterial(wood);

        Mesh lampMesh = OBJLoader.loadMesh("src/resources/models/streetlamp.obj");
        Material grey = new Material(new Vector4f(0.5f, 0.5f, 0.5f, 1.0f), 0.5f);
        lampMesh.setMaterial(grey);

        //make game item objects
        GameItem palm1 = new GameItem(palmMesh);
        palm1.setPosition(-0.5f,0,-5);
        palm1.setRotation(0,25f,-10f);
        palm1.setScale(new Vector3f(0.005f, 0.005f, 0.005f));

        GameItem palm2 = new GameItem(palmMesh);
        palm2.setPosition(0.3f,0,-4);
        palm2.setScale(new Vector3f(0.005f, 0.004f, 0.005f));

        GameItem lamp = new GameItem(lampMesh);
        lamp.setScale(new Vector3f(0.15f));
        lamp.setPosition(0f, 0.4f, -5.0f);

        GameItem island = new GameItem(sphereMesh);
        island.setScale(new Vector3f(3.0f, 2.0f, 2.0f));
        island.setPosition(0, -1.5f, -5);
        island.setRotation(0,0,180f);

        GameItem sea = new GameItem(floorMesh);
        sea.setScale(new Vector3f(20.0f));
        sea.setPosition(0, -0.1f, -2.5f);
        sea.setRotation(90f,0f,0);

        GameItem pier = new GameItem(pierMesh);
        pier.setScale(new Vector3f(0.2f));
        pier.setPosition(-0.4f,0f,-2.8f);
        pier.setRotation(0,15,0);

        gameItems = new GameItem[]{sea, island, lamp, palm1, palm2, pier};

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
            lightAngle += 1.1f;
            //this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            if (lightLamp > 0.9f)
                lightLamp = 0.0f;
            lightLamp += 0.01f;
            //this.pointLight.getPosition().z = lightPos - 0.1f;
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

        // Update lamp light color
        pointLight.setColor(new Vector3f(1f, lightLamp, 0.4f));

        // Update directional light direction, intensity and colour
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().delete();
        }
    }

    private void setUpLight() {
        // Ambient light
        ambientLight = new Vector3f(0.8f, 0.8f, 0.8f);
        // Point light
        Vector3f lightColour = new Vector3f(1f, lightLamp, 0.4f);
        Vector3f lightPosition = new Vector3f(0, 1.15f, -5f);
        pointLight = new PointLight(lightColour, lightPosition, 1.0f);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        // Directional light
        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1f, 1f, 1f);
        directionalLight = new DirectionalLight(lightColour, lightPosition, 0.25f);
    }
}
