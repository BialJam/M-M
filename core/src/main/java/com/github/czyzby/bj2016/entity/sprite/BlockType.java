package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.math.MathUtils;

/** Represents all available obstacles.
 *
 * @author MJ */
public enum BlockType {
    BUSH("bush");

    private final String id;

    private BlockType(final String id) {
        this.id = id;
    }

    /** @return unique ID of sprite. */
    public String getId() {
        return id;
    }

    /** @return name of drawable as it appears in atlas file. */
    public String getDrawableName() {
        return id;
    }

    /** @return random block type. */
    public static BlockType getRandom() {
        return values()[MathUtils.random(values().length - 1)];
    }

    @Override
    public String toString() {
        return id;
    }
}
