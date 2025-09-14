package block;

import org.joml.Vector3f;

public enum BlockMaterial {
    WOOD(3.0f,
            new Vector3f(0.05f, 0.025f, 0.01f),   // ambient - très faible
            new Vector3f(0.6f, 0.3f, 0.1f),       // diffuse - brun chaud
            new Vector3f(0.1f, 0.1f, 0.1f)),      // specular - faible (mat)

    STONE(8.0f,
            new Vector3f(0.02f, 0.02f, 0.02f),    // ambient - très sombre
            new Vector3f(0.5f, 0.5f, 0.5f),       // diffuse - gris neutre
            new Vector3f(0.1f, 0.1f, 0.1f)),      // specular - très faible

    STEEL(128.0f,                                 // Corrigé + augmenté pour plus de brillance
            new Vector3f(0.01f, 0.01f, 0.01f),    // ambient - quasi noir
            new Vector3f(0.4f, 0.4f, 0.45f),      // diffuse - gris bleuté métallique
            new Vector3f(0.8f, 0.8f, 0.9f)),      // specular - très brillant

    DIRT(4.0f,
            new Vector3f(0.03f, 0.02f, 0.01f),    // ambient - très faible
            new Vector3f(0.45f, 0.3f, 0.15f),     // diffuse - brun terre
            new Vector3f(0.02f, 0.02f, 0.02f)),   // specular - quasi mat

    SAND(2.0f,
            new Vector3f(0.04f, 0.03f, 0.015f),   // ambient - faible
            new Vector3f(0.7f, 0.55f, 0.35f),     // diffuse - beige sable
            new Vector3f(0.05f, 0.05f, 0.05f)),   // specular - très faible

    WATER(64.0f,                                  // Augmenté pour l'effet miroir de l'eau
            new Vector3f(0.0f, 0.02f, 0.04f),     // ambient - bleu très sombre
            new Vector3f(0.15f, 0.4f, 0.7f),      // diffuse - bleu eau
            new Vector3f(0.9f, 0.95f, 1.0f)),     // specular - très réfléchissant

    LIGHT(8.0f,
            new Vector3f(1.0f, 1.0f, 1.0f),       // ambient - blanc pur
            new Vector3f(0.2f, 0.2f, 0.2f),       // diffuse - blanc pur
            new Vector3f(0.5f, 0.5f, 0.5f));                                            // specular - pas de reflets    // specular - pas de reflets (source lumineuse)

    private final float shininess;
    private final Vector3f specular;
    private final Vector3f ambient;
    private int diffuse;
    private Vector3f diffuseVec;

    BlockMaterial(float shininess, Vector3f specular, Vector3f ambient, Vector3f diffuse) {
        this.shininess = shininess;
        this.specular = specular;
        this.ambient = ambient;
        this.diffuseVec = diffuse;
    }


    BlockMaterial(float shininess, Vector3f specular, Vector3f ambient, int diffuse) {
        this.shininess = shininess;
        this.specular = specular;
        this.ambient = ambient;
        this.diffuse = diffuse;
    }

    public float getShininess() {
        return shininess;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public int getSpecularInt() {
        return (specular).hashCode();
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public Vector3f getDiffuseVec() {
        return diffuseVec;
    }

    public int getDiffuse() {
        return diffuse;
    }
}