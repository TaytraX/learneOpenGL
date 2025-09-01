package render;

import render.renders.MeshRender;
import window.Window;

public class Renderer {
    public final Window window;
    private final MeshRender meshRender;

    public Renderer() {
        int height = 800;
        int width = 1280;
        window = new Window("Iso_Minecraft", width, height, true);
        meshRender = new MeshRender();
        init();
    }

    private void init() {
        meshRender.initialize();
    }

    public void render() {
        window.clear();
        meshRender.render();
        window.update();
    }

    public void cleanup() {
        window.cleanup();
        meshRender.cleanup();
    }
}