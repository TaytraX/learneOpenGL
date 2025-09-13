package render.lighting;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class LightManager {
    public static final int MAX_LIGHTS = 8;
    private final List<LightSource> lights = new ArrayList<>();

    public void clear() {
        lights.clear();
    }

    public void addLight(LightSource light) {
        if (lights.size() < MAX_LIGHTS) {
            lights.add(light);
        }
    }

    public void addLight(Vector3f position, Vector3f color, float intensity) {
        addLight(new LightSource(position, color, intensity));
    }

    public List<LightSource> getLights() {
        return lights;
    }

    public int getLightCount() {
        return lights.size();
    }
}