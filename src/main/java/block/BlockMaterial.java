package block;

import org.joml.Vector3f;

public enum BlockMaterial {
    WOOD(32.0f,
            new Vector3f(0.1f, 0.05f, 0.02f),    // ambient - brun foncé
            new Vector3f(0.55f, 0.27f, 0.07f),   // diffuse - brun bois
            new Vector3f(0.3f, 0.15f, 0.05f)),   // specular - faible brillance

    STONE(8.0f,
            new Vector3f(0.1f, 0.1f, 0.1f),     // ambient - gris foncé
            new Vector3f(0.4f, 0.4f, 0.4f),     // diffuse - gris moyen
            new Vector3f(0.2f, 0.2f, 0.2f)),    // specular - peu brillant

    METAL(256.0f,
            new Vector3f(0.05f, 0.05f, 0.05f),  // ambient - très sombre
            new Vector3f(0.3f, 0.3f, 0.3f),     // diffuse - gris métallique
            new Vector3f(0.9f, 0.9f, 0.9f)),    // specular - très brillant

    DIRT(4.0f,
            new Vector3f(0.1f, 0.07f, 0.03f),    // ambient - brun terreux foncé
            new Vector3f(0.4f, 0.25f, 0.1f),     // diffuse - brun terre
            new Vector3f(0.1f, 0.05f, 0.02f)),   // specular - très mat

    SAND(2.0f,
            new Vector3f(0.15f, 0.12f, 0.05f),   // ambient - beige foncé
            new Vector3f(0.8f, 0.6f, 0.3f),      // diffuse - beige sable
            new Vector3f(0.2f, 0.15f, 0.1f)),    // specular - légèrement brillant

    WATER(1.0f,
            new Vector3f(0.0f, 0.05f, 0.1f),    // ambient - bleu très foncé
            new Vector3f(0.1f, 0.3f, 0.6f),     // diffuse - bleu eau
            new Vector3f(0.8f, 0.9f, 1.0f)),    // specular - très réfléchissant

    GRASS(16.0f,
            new Vector3f(0.02f, 0.1f, 0.02f),   // ambient - vert foncé
            new Vector3f(0.1f, 0.6f, 0.1f),     // diffuse - vert herbe
            new Vector3f(0.2f, 0.4f, 0.2f)),    // specular - moyennement brillant

    LIGHT(1.0f,
            new Vector3f(1.0f, 1.0f, 1.0f),     // ambient - blanc pur (émissif)
            new Vector3f(1.0f, 1.0f, 1.0f),     // diffuse - blanc pur
            0);    // specular - pas de reflets (source lumineuse)

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