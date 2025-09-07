package render;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import window.Window;

import java.nio.FloatBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.*;
import static render.Camera.Camera_Movement.*;

public class Camera {
    float deltaTime;

    private FloatBuffer matrixBufferView;

    private Vector3f cameraPos;
    private Vector3f cameraFront;
    private Matrix4f view;

    boolean firstMouse = true;

    // Default camera values
    float YAW         = -90.0f;
    float PITCH       =  0.0f;
    final float SPEED       =  2.5f;
    final float SENSITIVITY =  0.1f;
    final float ZOOM        =  45.0f;

    float lastX, lastY;

    enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Camera(float deltaTime) {
        this.deltaTime = deltaTime;
    }

    public void init() {
        view = new Matrix4f();
        matrixBufferView = BufferUtils.createFloatBuffer(16);

        cameraFront = new Vector3f(0.0f, 0.0f, -2.0f);
        cameraPos = new Vector3f(0.0f, 0.0f, 10.0f);
    }

    public void update(Window window) {
        processKeyboard(window);
        processMouseMovement(window);
    }

    private void processKeyboard(Window window) {
        if(glfwGetKey(window.getWindowID(), GLFW_KEY_S) == GLFW_PRESS) applyMouvement(FORWARD);
        else if(glfwGetKey(window.getWindowID(), GLFW_KEY_W) == GLFW_PRESS) applyMouvement(BACKWARD);

        if(glfwGetKey(window.getWindowID(), GLFW_KEY_A) == GLFW_PRESS) applyMouvement(LEFT);
        else if(glfwGetKey(window.getWindowID(), GLFW_KEY_D) == GLFW_PRESS) applyMouvement(RIGHT);

        if(glfwGetKey(window.getWindowID(), GLFW_KEY_SPACE) == GLFW_PRESS) applyMouvement(UP);
        else if(glfwGetKey(window.getWindowID(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) applyMouvement(DOWN);
    }

    private void applyMouvement(Camera_Movement mouvement) {
        float velocity = SPEED * deltaTime;

        if (mouvement == FORWARD)
            cameraPos.z += velocity;
        if (mouvement == BACKWARD)
            cameraPos.z -= velocity;
        if (mouvement == LEFT)
            cameraPos.x -= velocity;
        if (mouvement == RIGHT)
            cameraPos.x += velocity;
        if (mouvement == UP)
            cameraPos.y += velocity;
        if (mouvement == DOWN)
            cameraPos.y -= velocity;
    }

    private void mouse_callback(float xpos, float ypos) {
        if (firstMouse)
        {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos;
        lastX = xpos;
        lastY = ypos;

        float sensitivity = 0.1f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        YAW   += xoffset;
        PITCH += yoffset;

        if(PITCH > 89.0f)
            PITCH = 89.0f;
        if(PITCH < -89.0f)
            PITCH = -89.0f;

        Vector3f direction = new Vector3f();
        direction.x = (float) (cos(Math.toRadians(YAW)) * cos(Math.toRadians(PITCH)));
        direction.y = (float) sin(Math.toRadians(PITCH));
        direction.z = (float) (sin(Math.toRadians(YAW)) * cos(Math.toRadians(PITCH)));
        cameraFront = direction.normalize();
    }

    private void processMouseMovement(@NotNull Window window) {
        glfwSetCursorPosCallback(window.getWindowID(), (win,  xPos, yPos) -> mouse_callback((float)xPos, (float)yPos));
    }

    public Vector3f getCameraPos() {
        return cameraPos;
    }

    public Vector3f getCameraTarget() {
        return new Vector3f(cameraPos).add(cameraFront);
    }

    public Matrix4f getView() {
        return view;
    }

    public FloatBuffer getMatrixBufferView() {
        return matrixBufferView;
    }

    public void setDeltaTime(float deltatime) {
        this.deltaTime = deltatime;
    }
}