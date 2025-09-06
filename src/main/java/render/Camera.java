package render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import window.Window;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static render.Camera.Camera_Movement.*;

public class Camera {
    float deltaTime;

    private FloatBuffer matrixBufferView;

    private Vector3f cameraPos;
    private Vector3f cameraTarget;
    private Vector3f cameraDirection;
    private Matrix4f view;

    // Default camera values
    final float YAW         = -90.0f;
    final float PITCH       =  0.0f;
    final float SPEED       =  2.5f;
    final float SENSITIVITY =  0.1f;
    final float ZOOM        =  45.0f;

    enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public Camera(float deltaTime) {
        this.deltaTime = deltaTime;
    }

    public void init() {
        view = new Matrix4f();
        matrixBufferView = BufferUtils.createFloatBuffer(16);

        cameraPos = new Vector3f(0.0f, 0.0f, -10.0f);
    }

    public void processInput(Window window) {
        if(glfwGetKey(window.getWindowID(), GLFW_KEY_W) == GLFW_PRESS) processKeyboard(FORWARD);
        else if(glfwGetKey(window.getWindowID(), GLFW_KEY_S) == GLFW_PRESS) processKeyboard(BACKWARD);

        if(glfwGetKey(window.getWindowID(), GLFW_KEY_A) == GLFW_PRESS) processKeyboard(LEFT);
        else if(glfwGetKey(window.getWindowID(), GLFW_KEY_D) == GLFW_PRESS) processKeyboard(RIGHT);
    }

    private void processKeyboard(Camera_Movement mouvement) {
        float velocity = SPEED * deltaTime;

        if (mouvement == FORWARD)
            cameraPos.z += velocity;
        if (mouvement == BACKWARD)
            cameraPos.z -= velocity;
        if (mouvement == LEFT)
            cameraPos.x -= velocity;
        if (mouvement == RIGHT)
            cameraPos.x += velocity;
    }

    public Vector3f getCameraPos() {
        return cameraPos;
    }

    public Matrix4f getView() {
        return view;
    }

    public FloatBuffer getMatrixBufferView() {
        return matrixBufferView;
    }
}