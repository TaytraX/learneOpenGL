package render;

import render.renders.MeshRender;
import render.renders.MeshRender2;
import window.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    float deltaTime;
    private double lastTime;
    public final Window window;
    private final Camera camera;
    private MeshRender meshRender = null;
    private MeshRender2 meshRender2 = null;
    static int height = 800;
    static int width = 1280;

    public int render = 2;

    public Renderer() {
        window = new Window("Learn_OpenGL", width, height, true);
        camera = new Camera(deltaTime, (float) width/height);

        switch(render) {
            case 2 -> meshRender2 = new MeshRender2();
            default -> meshRender = new MeshRender();
        }

        init();
    }

    private void init() {
        camera.init();
        if (render == 2) meshRender2.initialize();
        else meshRender.initialize();
    }

    public void render() {
        // Calcul du deltaTime
        double currentTime = glfwGetTime();
        if (lastTime == 0.0) lastTime = currentTime;

        deltaTime = (float)(currentTime - lastTime);
        lastTime = currentTime;

        camera.setDeltaTime(deltaTime);

        clear();

        if (render == 2) meshRender2.render(camera);
        else meshRender.render(camera);
        window.update();
        camera.update(window);
    }

    private void clear() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
    }

    public void cleanup() {
        window.cleanup();
        if (render == 2) meshRender2.cleanup();
        else meshRender.cleanup();
    }
}