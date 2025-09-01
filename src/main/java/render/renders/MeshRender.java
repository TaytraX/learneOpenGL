package render.renders;

import loader.Shader;
import loader.Texture;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class MeshRender {
    private int VAO, VBO, EBO, textureVBO;
    private final Shader shader;
    private final Texture texture1, texture2;
    private final float[] vertices = {
          //x      y
           -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
           -0.5f,  0.5f, 0.0f
    };

    private final float[] texCoords = {
            0.0f, 0.0f, 0.0f,  // lower-left corner
            1.0f, 0.0f, 0.0f,  // lower-right corner
            1.0f, 1.0f, 0.0f,   // top-center corner
            0.0f, 1.0f, 0.0f   // top-left corner
    };

    private final int[] indices = {
            0, 1, 2,  // Triangle 1
            2, 3, 0   // Triangle 2
    };

    public MeshRender() {
        shader = new Shader("basic");
        try {
            texture1 = new Texture("wall");
            texture2 = new Texture("awesomeface");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();
        textureVBO = glGenBuffers();

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(texCoords.length);
        textureBuffer.put(texCoords).flip();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render() {
        Matrix4f transform = new Matrix4f(); // JOML matrices sont initialisées à l'identité par défaut
        transform.translate(0.5f, -0.5f, 0.0f);
        transform.rotate((float)glfwGetTime(), 0.0f, 0.0f, 1.0f);

        shader.use();

        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        transform.get(matrixBuffer);
        glUniformMatrix4fv(shader.getUniforms().setInt("transform", 1), false, matrixBuffer);

        shader.getUniforms().setInt("texture1", 0);
        shader.getUniforms().setInt("texture2", 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture1.getTextureID());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2.getTextureID());

        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

    }

    public void cleanup() {
        shader.cleanUp();
        texture1.cleanUp();
        texture2.cleanUp();

        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        glDeleteBuffers(textureVBO);
    }
}