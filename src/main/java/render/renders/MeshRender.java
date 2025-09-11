package render.renders;

import loader.Shader;
import loader.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import render.Camera;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
    private final Vector3f[] positions = {
            new Vector3f( 0.0f,  0.0f,  0.0f),
            new Vector3f( 2.0f,  5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f( 2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f,  3.0f, -7.5f),
            new Vector3f( 1.3f, -2.0f, -2.5f),
            new Vector3f( 1.5f,  2.0f, -2.5f),
            new Vector3f( 1.5f,  0.2f, -1.5f),
            new Vector3f(-1.3f,  1.0f, -1.5f)
    };

    private final float[][] vertices = {
            {
                    -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,
                     0.5f, -0.5f, -0.5f,    1.0f, 0.0f,
                     0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
                    -0.5f,  0.5f, -0.5f,    0.0f, 1.0f
            },
            {
                    -0.5f, -0.5f, 0.5f,     0.0f, 0.0f,
                     0.5f, -0.5f, 0.5f,     1.0f, 0.0f,
                     0.5f,  0.5f, 0.5f,     1.0f, 1.0f,
                    -0.5f,  0.5f, 0.5f,     0.0f, 1.0f
            },
            {
                    -0.5f,  0.5f,  0.5f,    1.0f, 0.0f,
                    -0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
                    -0.5f, -0.5f,  0.5f,    0.0f, 0.0f
            },
            {
                    0.5f,  0.5f,  0.5f,     1.0f, 0.0f,
                    0.5f,  0.5f, -0.5f,     1.0f, 1.0f,
                    0.5f, -0.5f, -0.5f,     0.0f, 1.0f,
                    0.5f, -0.5f,  0.5f,     0.0f, 0.0f
            },
            {
                    -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
                     0.5f, -0.5f, -0.5f,    1.0f, 1.0f,
                     0.5f, -0.5f,  0.5f,    1.0f, 0.0f,
                    -0.5f, -0.5f,  0.5f,    0.0f, 0.0f
            },
            {
                    -0.5f, 0.5f, -0.5f,     0.0f, 1.0f,
                     0.5f, 0.5f, -0.5f,     1.0f, 1.0f,
                     0.5f, 0.5f,  0.5f,     1.0f, 0.0f,
                    -0.5f, 0.5f,  0.5f,     0.0f, 0.0f
            }
    };

    private final int[] indices = {
            // face 1
            0, 1, 2,  // Triangle 1
            2, 3, 0,   // Triangle 2

            //face 2
            4, 5, 6,  // Triangle 1
            6, 7, 4,    // Triangle 2

            //face 3
            8, 9, 10,  // Triangle 1
            10, 11, 8,   // Triangle 2

            //face 4
            12, 13, 14,  // Triangle 1
            14, 15, 12,   // Triangle 2

            //face 5
            16, 17, 18,  // Triangle 1
            18, 19, 16,   // Triangle 2

            //face 6
            20, 21, 22,  // Triangle 1
            22, 23, 20   // Triangle 2
    };

    public MeshRender() {
        shader = new Shader("basic");
        try {
            texture1 = new Texture("container");
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

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(120);

        for (float[] face : vertices) {
            vertexBuffer.put(face);
        }
        vertexBuffer.flip();

        Vector3f lightColor = new Vector3f(0.33f, 0.42f, 0.18f);
        Vector3f toyColor = new Vector3f(1.0f, 0.5f, 0.31f);
        Vector3f result = lightColor.mul(toyColor); // = (0.33f, 0.21f, 0.06f);

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render(Camera camera) {
        shader.use();

        shader.getUniforms().setMatrix4f("view", camera.getView());
        shader.getUniforms().setMatrix4f("projection", camera.getProjection());

        shader.getUniforms().setInt("texture1", 0);
        shader.getUniforms().setInt("texture2", 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture1.getTextureID());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2.getTextureID());

        glBindVertexArray(VAO);
        // Boucle pour chaque position
        for (Vector3f position : positions) {
            Matrix4f model = new Matrix4f().translation(position);
            shader.getUniforms().setMatrix4f("model", model);
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
        }
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