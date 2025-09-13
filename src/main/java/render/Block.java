package render;

import org.joml.Vector3f;
import render.lighting.LightSource;

public record Block(Vector3f size, Vector3f position, Vector3f color, boolean isEmissive, float intensity) {

    public Block(Vector3f size, Vector3f position, Vector3f color) {
        this(size, position, color, false, 0.0f);
    }

    public LightSource toLightSource() {
        if (isEmissive) {
            return new LightSource(position, color, intensity);
        }
        return null;
    }
}