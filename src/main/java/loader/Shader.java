package loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private UniformManager uniforms;
    private String sources;

    public Shader(String shaderName) {
        try {
                loadEmbeddedShader(shaderName);

        } catch (IOException e) {
            System.err.println("Erreur de fichier shader '" + shaderName + "': " + e.getMessage());
            System.out.println("Chargement du shader par défaut...");
            loadDefaultShader();
        } catch (RuntimeException e) {
            System.err.println("Erreur OpenGL shader '" + shaderName + "': " + e.getMessage());
            System.out.println("Chargement du shader par défaut...");
            loadDefaultShader();
        } catch (Exception e) { // Fallback pour le reste
            System.err.println("Erreur inattendue shader '" + shaderName + "': " + e.getMessage());
            loadDefaultShader();
        }
    }

    private void loadEmbeddedShader(String shaderName) throws IOException{
        String vertexSource;
        String fragmentSource;

        // Try-with-resources pour auto-close
        try (InputStream vertexInputStream = getClass().getResourceAsStream("/shader/vertex/" + shaderName + ".vert")) {
            if (vertexInputStream == null) {
                throw new IOException("Vertex shader file not found: " + shaderName + ".vert");
            }
            vertexSource = new String(vertexInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        try (InputStream fragmentInputStream = getClass().getResourceAsStream("/shader/fragment/" + shaderName + ".frag")) {
            if (fragmentInputStream == null) {
                throw new IOException("Fragment shader file not found: " + shaderName + ".frag");
            }
            fragmentSource = new String(fragmentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        sources = vertexSource + "\n" + fragmentSource;
        compile(vertexSource, fragmentSource); // Peut throw ShaderCompilationException
    }

    public void compile(String vertexSource, String fragmentSource) {
        try {
            // Compiler le vertex shader
            vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShaderID, vertexSource);
            glCompileShader(vertexShaderID);

            if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.println("Erreur vertex shader: " + glGetShaderInfoLog(vertexShaderID));
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            // Compiler le fragment shader
            fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShaderID, fragmentSource);
            glCompileShader(fragmentShaderID);

            if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.println("Erreur fragment shader: " + glGetShaderInfoLog(fragmentShaderID));
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Créer le programme
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Erreur linking: " + glGetProgramInfoLog(programID));
            return;
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println("Erreur validation: " + glGetProgramInfoLog(programID));
        }
        uniforms = new UniformManager(programID);
        uniforms.parseUniformsFromSource(sources);
    }

    private void loadDefaultShader() {
        // Crée un shader par défaut en hardcoded
        String defaultVertex = """
                #version 330 core
                in vec3 position;
                void main() { gl_Position = vec4(position, 1.0); }""";

        String defaultFragment = """
                #version 330 core
                out vec4 fragColor;
                void main() { fragColor = vec4(1.0, 1.0, 1.0, 1.0); }""";

        sources = defaultVertex + "\n" + defaultFragment;// Rose shocking

        uniforms.parseUniformsFromSource(sources);
        compile(defaultVertex, defaultFragment);
    }

    public UniformManager getUniforms() {

        try {
            return uniforms;
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void use() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        uniforms.cleanup();
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }
}