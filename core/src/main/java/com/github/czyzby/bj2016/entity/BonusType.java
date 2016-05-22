package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.bj2016.entity.sprite.EffectType;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.SoundService;
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

        @Override
        public void playSound(final SoundService soundService) {
            soundService.playRandomJumpSound();
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
    },
    SKULL("czaszka") {
        @Override
        public void apply(final Box2DService box2d, final Player player) {
            if (box2d.isSoloMode()) {
                for (final Player entity : box2d.getPlayers()) {
                    if (entity != player) {
                        entity.damage(20f);
                    }
                }
            } else {
                for (final Player entity : box2d.getPlayers()) {
                    if (entity != player) {
                        final Array<Minion> minions = entity.getMinions();
                        for (int index = 0; index < 10; index++) {
                            final Minion minion = minions.random();
                            if (!minion.isDestroyed()) {
                                minion.setDestroyed(true);
                            }
                        }
                    }
                }
            }
        }
    },
    MAGNET("magnez") {
        @Override
        public void apply(final Box2DService box2d, final Player player) {
            for (final Player entity : box2d.getPlayers()) {
                if (entity != player) {
                    final float angle = MathUtils.atan2(player.getY() - entity.getY(), player.getX() - entity.getX());
                    entity.getBody().applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.PLAYER_SPEED,
                            MathUtils.sin(angle) * Box2DUtil.PLAYER_SPEED, true);
                }
            }
        }

        @Override
        public void playSound(final SoundService soundService) {
            soundService.playRandomJumpSound();
        }
    },
    BOOTS("buty") {
        @Override
        public void apply(final Box2DService box2d, final Player player) {
            player.addSpeed(1500f);
        }
    },
    BOMB("bomba") {
        private final Vector2 explosionStart = new Vector2();
        private final Vector2 explosionEnd = new Vector2();
        private final Vector2 currentPosition = new Vector2();
        private final float angle15 = 10f * MathUtils.degreesToRadians;
        private final float radius = 5f;
        private Box2DService box2d;
        private Player player;
        private final RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(final Fixture fixture, final Vector2 point, final Vector2 normal,
                    final float fraction) {
                final Object data = fixture.getUserData();
                if (data != null) {
                    final Entity entity = (Entity) data;
                    if (entity.getType() == EntityType.BLOCK) {
                        entity.setDestroyed(true);
                    } else if (entity.getType() == EntityType.MINION) {
                        final Minion minion = (Minion) entity;
                        if (minion.getId() == player.getId()) {
                            minion.damage(5f);
                        } else {
                            minion.setDestroyed(true);
                        }
                    } else if (entity.getType() == EntityType.PLAYER) {
                        if (box2d.isSoloMode() && entity != player) {
                            ((Player) entity).damage(-20f);
                        }
                        final float angle = MathUtils.atan2(entity.getY() - player.getY(),
                                entity.getX() - player.getX());
                        entity.getBody().applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.PLAYER_SPEED,
                                MathUtils.sin(angle) * Box2DUtil.PLAYER_SPEED, true);
                    }
                }
                return 1f;
            }
        };

        @Override
        public void apply(final Box2DService box2d, final Player player) {
            this.box2d = box2d;
            this.player = player;
            currentPosition.set(player.getX(), player.getY());
            for (float angle = 0f; angle < MathUtils.PI * 2; angle += angle15) {
                explosionStart.set(currentPosition.x, currentPosition.y);
                explosionEnd.set(currentPosition.x + radius * MathUtils.cos(angle),
                        currentPosition.y + radius * MathUtils.sin(angle));
                box2d.getWorld().rayCast(callback, explosionStart, explosionEnd);
            }
            box2d.spawnEffect(EffectType.EXPLOSION, currentPosition.x, currentPosition.y);
        }

        @Override
        public void playSound(final SoundService soundService) {
            soundService.playRandomCrushSound();
        }
    };

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

    /** @param soundService should be used to play bonus sound. */
    public void playSound(final SoundService soundService) {
        soundService.playRandomBonusSound();
    }

    @Override
    public String toString() {
        return id;
    }
}
