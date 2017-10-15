package core;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import graphic.Renderer;
import graphic.Window;
import org.joml.sampling.UniformSampling;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import state.ExampleState;
import state.State3D;
import state.StateMachine;
//import silvertiger.tutorial.lwjgl.state.StateMachine;
//import silvertiger.tutorial.lwjgl.state.state.ExampleState;
//import silvertiger.tutorial.lwjgl.state.TextureState;
//import silvertiger.tutorial.lwjgl.state.GameState;
//import silvertiger.tutorial.lwjgl.graphic.Renderer;
//import silvertiger.tutorial.lwjgl.graphic.Window;
import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;


/**
 * The game class just initializes the game and starts the game loop. After
 * ending the loop it will get disposed.
 *
 */
public abstract class Game {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    /**
     * The error callback for GLFW.
     */
    private GLFWErrorCallback errorCallback;

    /**
     * Shows if the game is running.
     */
    protected boolean running;

    /**
     * The GLFW window used by the game.
     */
    protected Window window;
    /**
     * Used for timing calculations.
     */
    protected Timer timer;
    /**
     * Used for rendering.
     */
    protected Renderer renderer;
    /**
     * Stores the current state.
     */
    protected StateMachine state;

    /**
     * Default contructor for the game.
     */
    public Game() {
        timer = new Timer();
        renderer = new Renderer();
        state = new StateMachine();
    }

    /**
     * This should be called to initialize and start the game.
     */
    public void start() {
        init();
        gameLoop();
        dispose();
    }

    /**
     * Releases resources that where used by the game.
     */
    public void dispose() {
        /* Dipose renderer */
        renderer.dispose();

        /* Set empty state to trigger the exit method in the current state */
        state.change(null);

        /* Release window and its callbacks */
        if (window != null)
            window.destroy();

        /* Terminate GLFW and release the error callback */
        glfwTerminate();
        errorCallback.free();
    }

    /**
     * Initializes the game.
     */
    public void init() {
        /* Set error callback */
        errorCallback = GLFWErrorCallback.createPrint();
        glfwSetErrorCallback(errorCallback);

        /* Initialize GLFW */
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        /* Creating a temporary window for getting the available OpenGL version */
        long temp = glfwCreateWindow(1, 1, "", NULL, NULL);
        glfwMakeContextCurrent(temp);
        GL.createCapabilities();
        GLCapabilities caps = GL.getCapabilities();
        glfwDestroyWindow(temp);
        // Exit if OpenGL 3.2 is not supported
        if (!caps.OpenGL32){
            System.err.println("No OpenGL 3.2, plz update...");
            JOptionPane.showMessageDialog(null, "No OpenGL 3.2, plz update...");
            return;
        }

        /* Create GLFW window */
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) Math.round(screenDimensions.width/1.25);
        int height = (int) Math.round(screenDimensions.height/1.25);
        window = new Window(width, height, "Computer Graphix 3D scene", true);

        /* Initialize timer */
        timer.init();

        /* Initialize renderer */
        renderer.init();

        /* Initialize states */
        initStates();

        /* Initializing done, set running to true */
        running = true;
    }

    /**
     * Initializes the states.
     */
    public void initStates() {
        state.add("example", new State3D());
        //state.add("example", new ExampleState());
        //state.add("texture", new TextureState());
        //state.add("game", new GameState(renderer));
        state.change("example");
    }

    /**
     * The game loop. <br>
     * For implementation take a look at <code>VariableDeltaGame</code> and
     * <code>FixedTimestepGame</code>.
     */
    public abstract void gameLoop();

    /**
     * Handles input.
     */
    public void input() {
        state.input();
    }

    /**
     * Updates the game (fixed timestep).
     */
    public void update() {
        state.update();
    }

    /**
     * Updates the game (variable timestep).
     *
     * @param delta Time difference in seconds
     */
    public void update(float delta) {
        state.update(delta);
    }

    /**
     * Renders the game (no interpolation).
     */
    public void render() {
        state.render();
    }

    /**
     * Renders the game (with interpolation).
     *
     * @param alpha Alpha value, needed for interpolation
     */
    public void render(float alpha) {
        state.render(alpha);
    }

    /**
     * Synchronizes the game at specified frames per second.
     *
     * @param fps Frames per second
     */
    public void sync(int fps) {
        double lastLoopTime = timer.getLastLoopTime();
        double now = timer.getTime();
        float targetTime = 1f / fps;

        while (now - lastLoopTime < targetTime) {
            Thread.yield();

            /* This is optional if you want your game to stop consuming too much
             * CPU but you will loose some accuracy because Thread.sleep(1)
             * could sleep longer than 1 millisecond */
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }

            now = timer.getTime();
        }
    }

}