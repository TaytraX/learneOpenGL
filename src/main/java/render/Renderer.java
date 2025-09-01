package render;

import render.renders.MeshRender;
import window.Window;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    public final Window window;
    private final MeshRender meshRender;
    static int height = 800;
    static int width = 1280;

    public Renderer() {
        window = new Window("Iso_Minecraft", width, height, true);
        meshRender = new MeshRender();
        init();
    }

    private void init() {
        meshRender.initialize();
    }

    public void render() {
        clear();
        meshRender.render();
        window.update();
    }

    private void clear() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
    }

    public void cleanup() {
        window.cleanup();
        meshRender.cleanup();
    }
}