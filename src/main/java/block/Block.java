package block;

import org.joml.Vector3f;

public record Block(Vector3f size, Coord position, BlockMaterial material) {}