package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Contains all available types of bonuses.
 *
 * @author MJ */
public enum BonusType {
    TRAMPOLINE("trampoline") {
        @Override
        public void apply(final Box2DService box2d, final Player player) {
            if (box2d.getPlayers().size <= 1) {
                return;
            }
            Player target = box2d.getPlayers().random();
            while (target == player) {
                target = box2d.getPlayers().random();
            }
            final float angle = MathUtils.atan2(target.getY() - player.getY(), target.getX() - player.getX());
            player.getBody().applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.PLAYER_SPEED * 2f,
                    MathUtils.sin(angle) * Box2DUtil.PLAYER_SPEED * 2f, true);
        }
    },
    HEART("heart") {
        @Override
        public void apply(final Box2DService box2d, final Player player) {
            if (box2d.isSoloMode()) {
                player.damage(-20f);
            } else {
                for (int index = 0; index < 3; index++) { // I <3 index.
                    box2d.spawnMinion(player, player.getX() + MathUtils.random(-1f, 1f),
                            player.getY() + MathUtils.random(-1f, 1f));
                }
            }
        }
    }
    // SKULL
    //
    ;

    private final String id;

    private BonusType(final String id) {
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

    /** @param box2d manages Box2D world.
     * @param player took the bonus. */
    public abstract void apply(Box2DService box2d, Player player);

    /** @return random block type. */
    public static BonusType getRandom() {
        return values()[MathUtils.random(values().length - 1)];
    }

    @Override
    public String toString() {
        return id;
    }
}
