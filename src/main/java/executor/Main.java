package executor;

import render.Renderer;

public class Main {
    private Renderer renderer;

    public void init() {
        renderer = new Renderer();
    }

    public void start() {
        init();
        run();
    }

    public void run() {
        while(!renderer.window.windowShouldClose()) {
            renderer.render();
        }
        cleanup();
    }

    private void cleanup() {
        renderer.cleanup();
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
