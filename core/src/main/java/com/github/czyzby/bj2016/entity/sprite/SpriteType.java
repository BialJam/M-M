package com.github.czyzby.bj2016.entity.sprite;

/** Contains types of are available character sprites in the game.
 *
 * @author MJ */
public enum SpriteType {
    THUG("dres"),
    FAN("kibol"),
    LADY("moher", true),
    PUNK("punk"),
    BUM("menel"),
    BARBIE("barbie", true),
    EMO("emo"),
    NERD("kujonka", true);

    private final String id;
    private final String smallDrawable;
    private final boolean female;

    private SpriteType(final String id) {
        this(id, false);
    }

    private SpriteType(final String id, final boolean female) {
        this.id = id;
        smallDrawable = id + "-small";
        this.female = female;
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

    /** @return true if the selected sprite is female. */
    public boolean isFemale() {
        return female;
    }

    @Override
    public String toString() {
        return id;
    }
}
