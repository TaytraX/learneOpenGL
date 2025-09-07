package render;

import render.renders.MeshRender;
import window.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    float deltaTime;
    private double lastTime;
    public final Window window;
    private Camera camera;
    private final MeshRender meshRender;
    static int height = 800;
    static int width = 1280;

    public Renderer() {
        window = new Window("Iso_Minecraft", width, height, true);
        meshRender = new MeshRender();
        camera = new Camera(deltaTime);
        init();
    }

    private void init() {
        camera.init();
        meshRender.initialize();
    }

    public void render() {
        // Calcul du deltaTime
        double currentTime = glfwGetTime();
        if (lastTime == 0.0) lastTime = currentTime;

        float deltaTime = (float)(currentTime - lastTime);
        lastTime = currentTime;

        camera.setDeltaTime(deltaTime);

        clear();
        meshRender.render(camera);
        window.update();
        camera.update(window);
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