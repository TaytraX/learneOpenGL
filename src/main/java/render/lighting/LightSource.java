package render.lighting;

import org.joml.Vector3f;

public record LightSource(Vector3f position, Vector3f color, float intensity) {
    public LightSource(Vector3f position, Vector3f color) {
        this(position, color, 1.0f);
    }
}