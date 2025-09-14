package loader;

import block.Coord;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL20.*;

public class UniformManager {
    private final Map<String, Integer> uniforms = new HashMap<>();
    private final int programID;

    public UniformManager(int programID) {
        this.programID = programID;
    }

    public void createUniform(String name) {
        int location = glGetUniformLocation(programID, name);
        uniforms.put(name, location);
    }

    void parseUniformsFromSource(String shaderSource) {
        String[] lines = shaderSource.split("\n");

        for (String line : lines) {
            line = line.trim(); // Supprimer espaces

            // Chercher les lignes qui commencent par "uniform"
            if (line.startsWith("uniform") && !line.startsWith("//")) {
                String uniformName = extractUniformName(line);
                if (uniformName != null) {
                    System.out.println("Uniform détecté : " + uniformName);
                    createUniform(uniformName);
                }
            }
        }
    }

    String extractUniformName(String uniformLine) {
        String parts = uniformLine.split("//")[0].trim();
        if (!parts.startsWith("uniform")) return null;

        // Nouvelle regex pour gérer les arrays
        Pattern pattern = Pattern.compile("uniform\\s+\\w+\\s+(\\w+)(?:\\s*\\[\\s*(\\d+)\\s*\\])?\\s*;");
        Matcher matcher = pattern.matcher(parts);

        if (matcher.find()) {
            String uniformName = matcher.group(1);
            String arraySize = matcher.group(2);

            if (arraySize != null) {
                // C'est un array, créer les uniforms individuels
                int size = Integer.parseInt(arraySize);
                for (int i = 0; i < size; i++) {
                    createUniform(uniformName + "[" + i + "]");
                }
                return null; // Pas besoin de créer l'uniform de base
            }
            return uniformName;
        }
        return null;
    }

    void cleanup() {
        uniforms.clear();
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(programID, name), value);
    }

    public void setVec3(String name, float x, float y, float z) {
            glUniform3f(glGetUniformLocation(programID, name), x, y, z);
    }

    public void setVec3(String name, Vector3f vec) {
        glUniform3f(glGetUniformLocation(programID, name), vec.x, vec.y, vec.z);
    }

    public void setVec3(String name, Coord vec) {
        glUniform3f(glGetUniformLocation(programID, name), vec.x(), vec.y(), vec.z());

    }

    public void setInt(String name, int value) {
            glUniform1i(glGetUniformLocation(programID, name), value);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            matrix.get(buffer);
            glUniformMatrix4fv(glGetUniformLocation(programID, name), false, buffer);
    }
}