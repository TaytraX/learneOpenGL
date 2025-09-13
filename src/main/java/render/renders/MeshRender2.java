package render.renders;

import block.Coord;
import loader.Shader;
import loader.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import block.Block;
import render.Camera;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static block.BlockMaterial.*;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class MeshRender2 {
    private int lightingVAO, VAO, VBO, EBO, textureVBO;

    private final Shader shader, lightShader;
    private final Texture texture, texture_specular;
    private final Block[] block = {
            new Block(new Vector3f(1f), new Coord(0, 0, 0), LIGHT),
            new Block(new Vector3f(6.0f, 6.0f, 6.0f), new Coord(-8.0f, -3.0f, 4.0f), WOOD),
    };

    private final Coord lightPos = block[0].position();

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

    private final float[] textureCoords = {
            // Face 1 (vertices 0-3)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
            // Face 2 (vertices 4-7)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
            // Face 3 (vertices 8-11)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
            // Face 4 (vertices 12-15)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
            // Face 5 (vertices 16-19)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
            // Face 6 (vertices 20-23)
            1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f
    };

    public MeshRender2() {
        shader = new Shader("basic2");
        lightShader = new Shader("light_emissive");
        try {
            texture = new Texture("container2");
            texture_specular = new Texture("container2_specular");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        lightingVAO = glGenVertexArrays();
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

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
        textureBuffer.put(textureCoords).flip();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        shader.use();
        shader.getUniforms().setInt("material.diffuse", 0);
        shader.getUniforms().setInt("material.specular", 1);
    }

    public void render(Camera camera) {
        for (Block b : block) {
            shader.use();
            shader.getUniforms().setVec3("light.position", lightPos);
            shader.getUniforms().setVec3("viewPos", camera.getPosition());

            // light properties
            shader.getUniforms().setVec3("light.ambient", b.material().getAmbient());
            shader.getUniforms().setVec3("light.diffuse", b.material().getAmbient());
            shader.getUniforms().setVec3("light.specular", b.material().getAmbient());

            // material properties
            shader.getUniforms().setFloat("material.shininess", 64.0f);

            shader.getUniforms().setMatrix4f("projection", camera.getProjection());
            shader.getUniforms().setMatrix4f("view", camera.getView());

            // world transformation
            Matrix4f model = new Matrix4f();

            shader.getUniforms().setMatrix4f("model", model);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture_specular.getTextureID());

            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);

            lightShader.use();
            lightShader.getUniforms().setMatrix4f("projection", camera.getProjection());
            lightShader.getUniforms().setMatrix4f("view", camera.getView());

            model.translation(lightPos.x(), lightPos.y(), lightPos.z())
                    .scale(b.size());

            lightShader.getUniforms().setMatrix4f("model", model);

            glBindVertexArray(lightingVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
    }

    public void cleanup() {
        shader.cleanUp();
        lightShader.cleanUp();

        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        glDeleteBuffers(textureVBO);
    }
}