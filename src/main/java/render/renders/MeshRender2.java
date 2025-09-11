package render.renders;

import loader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import render.Block;
import render.Camera;

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

public class MeshRender2 {
    private int VAO, VBO, EBO, textureVBO;

    private final Shader shader;
    private final Block[] block = {
            new Block(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f)),
            new Block(new Vector3f(3.0f, -2.0f, 2.0f), new Vector3f(1.0f, 0.5f, 0.31f))
    };

    private final Vector3f lightPos = block[0].position();

    private final float[][] vertices = {
            {
                    -0.5f, -0.5f, -0.5f,     0.0f,  0.0f, -1.0f,
                     0.5f, -0.5f, -0.5f,     0.0f,  0.0f, -1.0f,
                     0.5f,  0.5f, -0.5f,     0.0f,  0.0f, -1.0f,
                    -0.5f,  0.5f, -0.5f,     0.0f,  0.0f, -1.0f
            },
            {
                    -0.5f, -0.5f, 0.5f,      0.0f,  0.0f, 1.0f,
                     0.5f, -0.5f, 0.5f,      0.0f,  0.0f, 1.0f,
                     0.5f,  0.5f, 0.5f,      0.0f,  0.0f, 1.0f,
                    -0.5f,  0.5f, 0.5f,      0.0f,  0.0f, 1.0f,
            },
            {
                    -0.5f,  0.5f,  0.5f,    -1.0f,  0.0f,  0.0f,
                    -0.5f,  0.5f, -0.5f,    -1.0f,  0.0f,  0.0f,
                    -0.5f, -0.5f, -0.5f,    -1.0f,  0.0f,  0.0f,
                    -0.5f, -0.5f,  0.5f,    -1.0f,  0.0f,  0.0f
            },
            {
                    0.5f,  0.5f,  0.5f,      1.0f,  0.0f,  0.0f,
                    0.5f,  0.5f, -0.5f,      1.0f,  0.0f,  0.0f,
                    0.5f, -0.5f, -0.5f,      1.0f,  0.0f,  0.0f,
                    0.5f, -0.5f,  0.5f,      1.0f,  0.0f,  0.0f
            },
            {
                    -0.5f, -0.5f, -0.5f,     0.0f, -1.0f,  0.0f,
                     0.5f, -0.5f, -0.5f,     0.0f, -1.0f,  0.0f,
                     0.5f, -0.5f,  0.5f,     0.0f, -1.0f,  0.0f,
                    -0.5f, -0.5f,  0.5f,     0.0f, -1.0f,  0.0f
            },
            {
                    -0.5f, 0.5f, -0.5f,     0.0f,  1.0f,  0.0f,
                     0.5f, 0.5f, -0.5f,     0.0f,  1.0f,  0.0f,
                     0.5f, 0.5f,  0.5f,     0.0f,  1.0f,  0.0f,
                    -0.5f, 0.5f,  0.5f,     0.0f,  1.0f,  0.0f
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

    public MeshRender2() {
        shader = new Shader("basic2");
    }

    public void initialize() {
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();
        textureVBO = glGenBuffers();

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length * vertices[0].length);

        for (float[] face : vertices) {
            vertexBuffer.put(face);
        }
        vertexBuffer.flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render(Camera camera) {
        shader.use();

        shader.getUniforms().setMatrix4f("view", camera.getView());
        shader.getUniforms().setMatrix4f("projection", camera.getProjection());
        shader.getUniforms().setVec3("viewPos", camera.getPosition());

        glBindVertexArray(VAO);
        // Boucle pour chaque position
        for (Block aBlock : block) {
            Matrix4f model = new Matrix4f().translation(aBlock.position());
            shader.getUniforms().setMatrix4f("model", model);
            shader.getUniforms().setVec3("objectColor", aBlock.color());
            shader.getUniforms().setVec3("lightColor", block[0].color());
            shader.getUniforms().setVec3("lightPos", lightPos);

            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);

    }

    public void cleanup() {
        shader.cleanUp();

        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        glDeleteBuffers(textureVBO);
    }
}