package game;

import engine.GameSettings;
import engine.Utils;
import engine.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Hud {

    private static final String FONT_NAME = "BOLD";
    private long vg;
    private NVGColor colour;
    private ByteBuffer fontBuffer;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private DoubleBuffer posx;
    private DoubleBuffer posy;
    private int counter;
    public int hudWidth = 350;

    // button vars
    private String fogTxt;
    private String linMagTxt;
    private String triMinTxt;
    private String lodTxt;
    private String MSAATxt;
    private boolean[] hover;

    public void init() throws Exception {

        this.vg = GameSettings.isMSAA() ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("src/resources/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = BufferUtils.createDoubleBuffer(1);
        posy = BufferUtils.createDoubleBuffer(1);

        counter = 0;

        hover = new boolean[] {false, false, false, false, false};
    }

    public void render(Window window) {
        updateSettings();
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

        //stubHud(window);
        drawHud(window);

        nvgEndFrame(vg);

        // Restore state
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void drawHud(Window window){
        int y_offset = 30;
        int y_pos = 15;

        nvgBeginPath(vg);
        nvgRoundedRect(vg,10, 10, hudWidth, 165, 20);
        nvgFillColor(vg, rgba(0x0, 0x0, 0x0, 200, colour));
        nvgFill(vg);

        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (!hover[0])
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));
        else
            nvgFillColor(vg, rgba(0xff, 0xff, 0xff, 255, colour));
        nvgText(vg, hudWidth/2, y_pos, fogTxt);

        y_pos += y_offset;
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (!hover[1])
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));
        else
            nvgFillColor(vg, rgba(0xff, 0xff, 0xff, 255, colour));
        nvgText(vg, hudWidth/2, y_pos, linMagTxt);

        y_pos += y_offset;
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (!hover[2])
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));
        else
            nvgFillColor(vg, rgba(0xff, 0xff, 0xff, 255, colour));
        nvgText(vg, hudWidth/2, y_pos, triMinTxt);

        y_pos += y_offset;
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (!hover[3])
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));
        else
            nvgFillColor(vg, rgba(0xff, 0xff, 0xff, 255, colour));
        nvgText(vg, hudWidth/2, y_pos, lodTxt);

        y_pos += y_offset;
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (!hover[4])
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));
        else
            nvgFillColor(vg, rgba(0xff, 0xff, 0xff, 255, colour));
        nvgText(vg, hudWidth/2, y_pos,MSAATxt);

    }

    private void stubHud(Window window){
        // Upper ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
        nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        nvgFill(vg);

        // Lower ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(vg);
        nvgCircle(vg, xcenter, ycenter, radius);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        // Clicks Text
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
        nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));
    }

    public void hover(int i, boolean val){
        hover[i] = val;
    }

    private void updateSettings(){
        fogTxt = GameSettings.isFOG()? "Fog - ON" : "Fog - OFF";
        linMagTxt = GameSettings.isMagLinear()? "Linear Mag filter - ON" : "Linear Mag filter - OFF";
        triMinTxt = GameSettings.isMinTrilinear()? "Trilinear Min filtering - ON": "Trilinear Min filtering - OFF";
        lodTxt = "Level of details bias: " + GameSettings.getLodBias();
        MSAATxt = GameSettings.isMSAA()? "AntiAliasing (MSAA) - ON" : "AntiAliasing (MSAA) - OFF";
    }

    public void incCounter() {
        counter++;
        if (counter > 99) {
            counter = 0;
        }
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        nvgDelete(vg);
    }
}
