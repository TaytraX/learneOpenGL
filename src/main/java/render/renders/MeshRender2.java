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
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class MeshRender2 {
    private int lightingVAO, VAO, VBO;

    private final Shader shader, lightShader;
    private final Texture texture, texture_specular;
    private final Block[] block = {
            new Block(new Vector3f(1f), new Coord(0, 0, 0), LIGHT),
            new Block(new Vector3f(6.0f, 6.0f, 6.0f), new Coord(-8.0f, -3.0f, 4.0f), WOOD),
    };

    Matrix4f model = new Matrix4f();
    Matrix4f modelLight = new Matrix4f();
    private final Coord lightPos = block[0].position();

    private final float[] vertices = {
            // positions          // normals           // texture coords
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f
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

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glBindVertexArray(VAO);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(lightingVAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        shader.use();
        shader.getUniforms().setInt("material.diffuse", 0);
        shader.getUniforms().setInt("material.specular", 1);
    }

    public void render(Camera camera) {
        renderLightBlock(camera);
        renderBlock(camera);
    }

    public void renderLightBlock(Camera camera) {
            lightShader.use();
            lightShader.getUniforms().setMatrix4f("projection", camera.getProjection());
            lightShader.getUniforms().setMatrix4f("view", camera.getView());

            modelLight.translation(lightPos.x(), lightPos.y(), lightPos.z())
                    .scale(block[0].size());

            lightShader.getUniforms().setMatrix4f("modelLight", modelLight);

            glBindVertexArray(lightingVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void renderBlock(Camera camera) {
        shader.use();
        shader.getUniforms().setVec3("light.position", lightPos);
        shader.getUniforms().setVec3("viewPos", camera.getPosition());

        // light properties
        shader.getUniforms().setVec3("light.ambient", block[0].material().getAmbient());
        shader.getUniforms().setVec3("light.diffuse", block[0].material().getDiffuseVec());
        shader.getUniforms().setVec3("light.specular", block[0].material().getSpecular());

        // material properties
        shader.getUniforms().setFloat("material.shininess", block[0].material().getShininess());

        shader.getUniforms().setMatrix4f("projection", camera.getProjection());
        shader.getUniforms().setMatrix4f("view", camera.getView());

        Vector3f pos = new Vector3f();
        float newX = (float) (block[0].position().x() + 10 * cos(45 * (glfwGetTime() / 20)));
        float newZ = (float) (block[0].position().z() + 10 * sin(45 * (glfwGetTime() / 20)));
        float newY = (float) (block[0].position().y() + 10 * sin(45 * (glfwGetTime() / 18)));

        // world transformation
        model.translation(newX, newY, newZ).scale(block[1].size());
        model.rotate(15 * (float) Math.toRadians(glfwGetTime() * 50) / 5, new Vector3f(1.0f, 0.3f, 0.5f).normalize());

        shader.getUniforms().setMatrix4f("model", model);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture_specular.getTextureID());

        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void cleanup() {
        shader.cleanUp();
        lightShader.cleanUp();

        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
    }
}