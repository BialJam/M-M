package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Draws a single player.
 *
 * @author MJ */
public class PlayerSprite implements Comparable<PlayerSprite> {
    private final Player player;
    private final Sprite sprite;
    private final FloatRange rotation = new FloatRange(0f, 0.3f);
    private boolean stopped = true;
    private boolean removing;

    public PlayerSprite(final Player player, final Sprite sprite) {
        this.player = player;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setOrigin(sprite.getRegionWidth() / 2f / Box2DUtil.PPU, sprite.getRegionWidth() / 2f / Box2DUtil.PPU);
    }

    /** @return game entity. */
    public Player getPlayer() {
        return player;
    }

    /** @param delta time since last update. */
    public void update(final float delta) {
        if (player.isDestroyed()) {
            if (!removing) {
                removing = true;
                rotation.setTargetValue(MathUtils.randomBoolean() ? 90f : 270f);
            }
            rotation.update(delta);
            sprite.setRotation(rotation.getCurrentValue());
            return;
        } else if (player.isMoving()) {
            if (stopped) {
                stopped = false;
                rotation.setTargetValue(MathUtils.randomBoolean() ? 11f : -11f);
            }
            if (rotation.getCurrentValue() > 10f) {
                rotation.setTargetValue(-11f);
            } else if (rotation.getCurrentValue() < -10f) {
                rotation.setTargetValue(11f);
            }
        } else {
            stopped = true;
            rotation.setTargetValue(0f);
        }
        rotation.update(delta);
        sprite.setRotation(rotation.getCurrentValue());
        sprite.setPosition(player.getX() - sprite.getWidth() / 2f, player.getY() - sprite.getHeight() / 2f);
    }

    /** @param batch must be begun. */
    public void draw(final Batch batch) {
        sprite.draw(batch);
    }

    @Override
    public int compareTo(final PlayerSprite other) {
        final float y = player.getY(), otherY = other.player.getY();
        return y > otherY ? -1 : y < otherY ? 1 : 0;
    }

    /** @return true if player is dead. */
    public boolean isDead() {
        return player.isDestroyed();
    }
}
