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
import static java.lang.Math.*;
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
    private final Block[] blockLight = {
            new Block(new Vector3f(1f), new Coord(0.7f,  0.2f,  2.0f), LIGHT),
            new Block(new Vector3f(1), new Coord(2.3f, -3.3f, -4.0f), LIGHT),
            new Block(new Vector3f(1), new Coord(-4.0f,  2.0f, -12.0f), LIGHT),
            new Block(new Vector3f(1), new Coord(0.0f,  0.0f, -3.0f), LIGHT)
    };

    private final Block[] block = {
            new Block(new Vector3f(6f), new Coord(-10f, 10f, 5), WOOD),
            new Block(new Vector3f(6f), new Coord(20f, 38, 18f), WOOD),
            new Block(new Vector3f(6), new Coord(0.0f, -6.0f, 0.0f), WOOD),
            new Block(new Vector3f(6), new Coord(32.0f, 0.0f, -5.0f), WOOD),
            new Block(new Vector3f(6), new Coord(-25.0f, 0.0f, -15.0f), WOOD)
    };

    Matrix4f model = new Matrix4f();
    Matrix4f modelLight = new Matrix4f();

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
        for (Block b : blockLight) {
            lightShader.use();
            lightShader.getUniforms().setMatrix4f("projection", camera.getProjection());
            lightShader.getUniforms().setMatrix4f("view", camera.getView());

            modelLight.translation(b.position().x(), b.position().y(), b.position().z())
                    .scale(b.size());

            lightShader.getUniforms().setMatrix4f("modelLight", modelLight);

            glBindVertexArray(lightingVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
    }

    public void renderBlock(Camera camera) {
        shader.use();
        shader.getUniforms().setVec3("dirLight.direction", -0.2f, -1.0f, -0.3f);
        shader.getUniforms().setVec3("dirLight.ambient", 0.05f, 0.05f, 0.05f);
        shader.getUniforms().setVec3("dirLight.diffuse", 0.4f, 0.4f, 0.4f);
        shader.getUniforms().setVec3("dirLight.specular", 0.5f, 0.5f, 0.5f);
        // point light 1
        shader.getUniforms().setVec3("pointLights[0].position", blockLight[0].position());
        shader.getUniforms().setVec3("pointLights[0].ambient", 0.05f, 0.05f, 0.05f);
        shader.getUniforms().setVec3("pointLights[0].diffuse", 0.8f, 0.8f, 0.8f);
        shader.getUniforms().setVec3("pointLights[0].specular", 1.0f, 1.0f, 1.0f);
        shader.getUniforms().setFloat("pointLights[0].constant", 1.0f);
        shader.getUniforms().setFloat("pointLights[0].linear", 0.09f);
        shader.getUniforms().setFloat("pointLights[0].quadratic", 0.032f);
        // point light 2
        shader.getUniforms().setVec3("pointLights[1].position", blockLight[1].position());
        shader.getUniforms().setVec3("pointLights[1].ambient", 0.05f, 0.05f, 0.05f);
        shader.getUniforms().setVec3("pointLights[1].diffuse", 0.8f, 0.8f, 0.8f);
        shader.getUniforms().setVec3("pointLights[1].specular", 1.0f, 1.0f, 1.0f);
        shader.getUniforms().setFloat("pointLights[1].constant", 1.0f);
        shader.getUniforms().setFloat("pointLights[1].linear", 0.09f);
        shader.getUniforms().setFloat("pointLights[1].quadratic", 0.032f);
        // point light 3
        shader.getUniforms().setVec3("pointLights[2].position", blockLight[2].position());
        shader.getUniforms().setVec3("pointLights[2].ambient", 0.05f, 0.05f, 0.05f);
        shader.getUniforms().setVec3("pointLights[2].diffuse", 0.8f, 0.8f, 0.8f);
        shader.getUniforms().setVec3("pointLights[2].specular", 1.0f, 1.0f, 1.0f);
        shader.getUniforms().setFloat("pointLights[2].constant", 1.0f);
        shader.getUniforms().setFloat("pointLights[2].linear", 0.09f);
        shader.getUniforms().setFloat("pointLights[2].quadratic", 0.032f);
        // point light 4
        shader.getUniforms().setVec3("pointLights[3].position", blockLight[3].position());
        shader.getUniforms().setVec3("pointLights[3].ambient", 0.05f, 0.05f, 0.05f);
        shader.getUniforms().setVec3("pointLights[3].diffuse", 0.8f, 0.8f, 0.8f);
        shader.getUniforms().setVec3("pointLights[3].specular", 1.0f, 1.0f, 1.0f);
        shader.getUniforms().setFloat("pointLights[3].constant", 1.0f);
        shader.getUniforms().setFloat("pointLights[3].linear", 0.09f);
        shader.getUniforms().setFloat("pointLights[3].quadratic", 0.032f);
        // spotLight
        shader.getUniforms().setVec3("viewPos", camera.getPosition());

        // material properties
        shader.getUniforms().setFloat("material.shininess", block[0].material().getShininess());

        shader.getUniforms().setMatrix4f("projection", camera.getProjection());
        shader.getUniforms().setMatrix4f("view", camera.getView());

        // world transformation
        model.translation(block[1].position().x(), block[1].position().y(), block[1].position().z()).scale(block[1].size());

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