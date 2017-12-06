package game;

import engine.*;
import engine.Window;
import engine.graphix.*;
import engine.items.GameItem;
import game.meshes.PlaneMesh;
import game.meshes.SphereMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;

public class Scene3D implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer3D renderer;
    private final Camera camera;
    private Scene scene;
    private Hud hud = null;
    private static final float CAMERA_POS_STEP = 0.05f;

    private float lightAngle;
    private float lightLamp;

    private boolean pressedE;
    private boolean rightMouse;
    private DoubleBuffer posx;
    private DoubleBuffer posy;
    private Mesh seaMesh;

    Scene3D(){
        renderer = new Renderer3D();
        hud = new Hud();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = 0;
        lightLamp = 1.0f;
        rightMouse = false;
        pressedE = false;
    }

    @Override
    public void init(Window window) throws Exception {
        hud.init();
        renderer.init(window);

        scene = new Scene();

        //load meshes and create game items
        SphereMesh s = new SphereMesh(1f);
        Mesh sphereMesh = new Mesh(s.getVertices(),s.getTexCoords(), s.getNormals(), s.getIndices());
        Material sand = new Material(new Vector4f(0.9f, 0.85f, 0.5f,1f), 0.25f );
        sphereMesh.setMaterial(sand);

        PlaneMesh plane = new PlaneMesh();
        seaMesh = new Mesh(plane.getPositions(), plane.getTexCoords(),plane.getNormals(), plane.getIndices());
        Material seaMaterial = new Material(new Texture("src/resources/textures/sea.png"));
        //Material blue = new Material(new Vector4f(0.0f,0.4f,0.6f, 1f), reflectance);
        seaMesh.setMaterial(seaMaterial);

        Mesh palmMesh = OBJLoader.loadMesh("src/resources/models/palm_tree.obj");
        Material palmMaterial = new Material(new Texture("src/resources/models/palm-tex3.png"));
        palmMesh.setMaterial(palmMaterial);

        Mesh pierMesh = OBJLoader.loadMesh("src/resources/models/pier.obj");
        Material pierMaterial = new Material(new Texture("src/resources/models/pier-tex.png"));
        pierMesh.setMaterial(pierMaterial);

        Mesh lampMesh = OBJLoader.loadMesh("src/resources/models/streetlamp.obj");
        Material lampMaterial = new Material(new Texture("src/resources/models/streetlamp-tex.png"));
        lampMesh.setMaterial(lampMaterial);

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

        GameItem sea = new GameItem(seaMesh);
        sea.setScale(new Vector3f(10.0f));
        sea.setPosition(0, -0.1f, -2.5f);
        sea.setRotation(90f,0f,0);

        GameItem pier = new GameItem(pierMesh);
        pier.setScale(new Vector3f(0.2f));
        pier.setPosition(-0.4f,0f,-2.8f);
        pier.setRotation(0,15,0);

        GameItem[] gameItems = new GameItem[]{sea, island, lamp, pier, palm1, palm2};
        scene.setGameItems(gameItems);

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("src/resources/skybox/skybox.obj", "src/resources/skybox/skybox2.png");
        skyBox.setScale(10f);
        scene.setSkyBox(skyBox);

        setUpLight();

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.25f));

        posx = BufferUtils.createDoubleBuffer(1);
        posy = BufferUtils.createDoubleBuffer(1);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) throws Exception {
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
        if (window.isKeyPressed(GLFW_KEY_N)) {
            lightAngle += 1.0f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            if (lightLamp > 0.9f)
                lightLamp = 0.0f;
            lightLamp += 0.01f;
        } else if (window.isKeyPressed(GLFW_KEY_1)){
            Texture sea = new Texture("src/resources/textures/sea.png");
            seaMesh.getMaterial().setTexture(sea);

        } else if (window.isKeyPressed(GLFW_KEY_2)) {
            Texture sea = new Texture("src/resources/textures/sea2.png");
            seaMesh.getMaterial().setTexture(sea);
        }

        // HUD toggle
        if (!pressedE && window.isKeyPressed(GLFW_KEY_E)) {
            pressedE = true;
            GameSettings.toggleHUD();
        } else if (pressedE && !window.isKeyPressed(GLFW_KEY_E))
            pressedE = false;


        // HUD mouse interaction
        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        boolean pressed = mouseInput.isRightButtonPressed();
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);

        if (GameSettings.isHUD() && !rightMouse && pressed) {
            rightMouse = pressed;
            // mouse click detected
            reactToMouse(x, y, true, window);
        } else
            reactToMouse(x,y, false, window);
        if (GameSettings.isHUD() && rightMouse && !pressed){
            rightMouse = pressed;
        }
    }

    public void reactToMouse(int x, int y, boolean click, Window window) throws Exception {
        if (15 < x && x < hud.hudWidth) {
            int lb = 15, ub = 45;
            for (int i=0; i < 8; i++) {
                if (lb < y && y < ub) {
                    hud.hover(i, true);
                    if (click) {
                        switch (i) {
                            case 0:
                                GameSettings.toggleFog();
                                break;
                            case 1:
                                GameSettings.toggleMagLinear();
                                if (GameSettings.isMagLinear()) {
                                    setTexParam(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                                } else{
                                    setTexParam(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                                }
                                break;
                            case 2:
                                GameSettings.toggleMinTrilinear();
                                if(GameSettings.isMinTrilinear()) {
                                    setTexParam(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                                } else {
                                    setTexParam(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
                                }
                                break;
                            case 3:
                                int bias = GameSettings.getLodBias();
                                if (bias == -5)
                                    GameSettings.setLodBias(0);
                                else if (bias == 0)
                                    GameSettings.setLodBias(1);
                                else if (bias == 1)
                                    GameSettings.setLodBias(-5);
                                setTexParam(GL_TEXTURE_LOD_BIAS, GameSettings.getLodBias());
                                break;
                            case 4:
                                GameSettings.toggleMSAA();
                                if (GameSettings.isMSAA()) {
                                    glfwWindowHint(GLFW_SAMPLES, 4);
                                    glEnable(GL_MULTISAMPLE);
                                }
                                else {
                                    glfwWindowHint(GLFW_SAMPLES, 0);
                                    glDisable(GL_MULTISAMPLE);
                                }
                                break;
                            case 5:
                                GameSettings.setRES(0);
                                glfwSetWindowSize(window.getWindowHandle(), 1920, 1080);
                                break;
                            case 6:
                                GameSettings.setRES(1);
                                glfwSetWindowSize(window.getWindowHandle(), 1600, 900);
                                break;
                            case 7:
                                GameSettings.setRES(2);
                                glfwSetWindowSize(window.getWindowHandle(), 1280, 1024);
                                break;
                        }
                    }
                } else
                    hud.hover(i, false);
                lb += 30;
                ub += 30;
            }
        }
    }

    private void setTexParam(int pname, int param) throws Exception {
        // https://www.khronos.org/opengl/wiki/GLAPI/glTexParameter
        scene.setTexParam(pname, param);
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

        SceneLight sceneLight = scene.getSceneLight();
        // Update lamp light color
        sceneLight.getPointLightList()[0].setColor(new Vector3f(1f, lightLamp, 0.4f));
        // Update directional light direction, intensity and colour
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        //lightAngle += 0.5f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
            sceneLight.getSkyBoxLight().set(0.3f, 0.3f, 0.3f);
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sceneLight.getSkyBoxLight().set(factor, factor, factor);
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneLight.getSkyBoxLight().set(1.0f, 1.0f, 1.0f);
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
        renderer.render(window, camera, scene);
        if (GameSettings.isHUD())
            hud.render(window);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }

    private void setUpLight() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);
        // Ambient light
        sceneLight.setAmbientLight(new Vector3f(0.8f, 0.8f, 0.8f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));
        // Point light
        Vector3f lightColour = new Vector3f(1f, lightLamp, 0.4f);
        Vector3f lightPosition = new Vector3f(0, 1.15f, -5f);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        PointLight pl = new PointLight(lightColour, lightPosition, 1.0f);
        pl.setAttenuation(att);
        PointLight[] plArr = new PointLight[]{pl};
        sceneLight.setPointLightList(plArr);
        // Directional light
        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1f, 1f, 1f);
        sceneLight.setDirectionalLight(new DirectionalLight(lightColour, lightPosition, 0.25f));
    }
}
