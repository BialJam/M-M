package com.github.czyzby.bj2016.entity.sprite;

/** Contains data of all effects displayed on the screen.
 *
 * @author MJ */
public enum EffectType {
    EXPLOSION("boom"),
    SMOKE("dymek");

    private final String id;

    private EffectType(final String id) {
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

    @Override
    public String toString() {
        return id;
    }
}
