package state;


import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * The empty state does nothing. Really.
 *
 * @author Heiko Brumme
 */
public class EmptyState implements State {

    @Override
    public void input() {
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void enter() {
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void exit() {
    }

}
