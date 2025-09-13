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
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class MeshRender2 {
    private int VAO, VBO, EBO, textureVBO;

    private final Shader shader, lightShader;
    private final Texture texture;
    private final Block[] block = {
            new Block(new Vector3f(1f), new Coord(0, 0, 0), LIGHT),
            new Block(new Vector3f(6.0f, 6.0f, 6.0f), new Coord(-8.0f, -3.0f, 4.0f), GRASS),
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
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };

    public MeshRender2() {
        shader = new Shader("basic2");
        lightShader = new Shader("light_emissive");
        try {
            texture = new Texture("container2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
        textureBuffer.put(textureCoords).flip();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render(Camera camera) {
        glBindVertexArray(VAO);
        Matrix4f model;

        for (int i = 0; i < block.length; i++) {
            Block aBlock = block[i];
            model = new Matrix4f()
                    .translation(new Vector3f(aBlock.position().x(), aBlock.position().y(), aBlock.position().z()))
                    .scale(aBlock.size());

            if (i == 0) {
                // Source lumineuse
                lightShader.use();  // Shader émissif
                lightShader.getUniforms().setMatrix4f("model", model);
                lightShader.getUniforms().setMatrix4f("view", camera.getView());
                lightShader.getUniforms().setMatrix4f("projection", camera.getProjection());
                lightShader.getUniforms().setVec3("lightColor", aBlock.material().getAmbient());
            } else {
                // Objets éclairés
                shader.use();  // Shader Phong
                shader.getUniforms().setMatrix4f("model", model);
                shader.getUniforms().setMatrix4f("view", camera.getView());
                shader.getUniforms().setVec3("viewPos", camera.getPosition());
                shader.getUniforms().setMatrix4f("projection", camera.getProjection());
                shader.getUniforms().setVec3("lightColor", new Vector3f(0.6f));
                shader.getUniforms().setVec3("lightPos", lightPos);
                shader.getUniforms().setVec3("material.ambient", aBlock.material().getAmbient());
                shader.getUniforms().setInt("material.diffuse", 0);
                shader.getUniforms().setVec3("material.specular", aBlock.material().getSpecular());
                shader.getUniforms().setFloat("material.shininess", aBlock.material().getShininess());
            }

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);
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