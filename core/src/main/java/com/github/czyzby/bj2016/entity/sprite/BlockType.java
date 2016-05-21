package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.math.MathUtils;

/** Represents all available obstacles.
 *
 * @author MJ */
public enum BlockType {
    BUSH("bush", 20f),
    STONE("stone", 30f),
    ROCK("rock", 35f),
    TRASH("kosz", 25f),
    BENCH("lawka", 30f);

    private final String id;
    private float health;

    private BlockType(final String id, final float health) {
        this.id = id;
        this.health = health;
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

    /** @return duration of the obstacle. */
    public float getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return id;
    }
}
