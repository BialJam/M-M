package com.github.czyzby.bj2016.entity.sprite;

/** Contains types of are available character sprites in the game.
 *
 * @author MJ */
public enum SpriteType {
    THUG("dres"),
    FAN("kibol");

    private final String id;
    private final String smallDrawable;

    private SpriteType(final String id) {
        this.id = id;
        smallDrawable = id + "-small";
    }

    /** @return unique ID of sprite. */
    public String getId() {
        return id;
    }

    /** @return name of drawable as it appears in atlas file. */
    public String getDrawableName() {
        return id;
    }

    /** @return name of small drawable as it appears in atlas file. */
    public String getSmallDrawableName() {
        return smallDrawable;
    }

    @Override
    public String toString() {
        return id;
    }
}
