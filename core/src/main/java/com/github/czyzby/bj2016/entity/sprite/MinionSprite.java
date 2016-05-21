package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.github.czyzby.bj2016.entity.Minion;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Draws minions.
 *
 * @author MJ */
public class MinionSprite {
    private final FloatRange rotation = new FloatRange(MathUtils.random(40f) - 20f, 0.3f);
    private final FloatRange alpha = new FloatRange(1f, 0.3f);
    private final Sprite sprite;
    private final Minion minion;
    private boolean removing;
    private boolean hidden;

    public MinionSprite(final Minion minion, final Sprite sprite) {
        this.minion = minion;
        this.sprite = sprite;
        rotation.setTargetValue(MathUtils.randomBoolean() ? -21f : 21f);
    }

    /** @param batch used to draw the sprite.
     * @param delta time since last render.
     * @return true if entity should be removed. */
    public boolean render(final Batch batch, final float delta) {
        if (minion.isDestroyed()) {
            if (!removing) {
                removing = true;
                rotation.setTargetValue(90f);
                alpha.setTargetValue(0f);
            } else if (rotation.isTransitionInProgress()) {
                rotation.update(delta);
                alpha.update(delta);
            } else {
                hidden = true;
            }
        } else {
            rotation.update(delta);
            if (rotation.getCurrentValue() > 20f) {
                rotation.setTargetValue(-21f);
            } else if (rotation.getCurrentValue() < -20f) {
                rotation.setTargetValue(21f);
            }
        }
        sprite.setPosition(minion.getX() - sprite.getWidth() / 2f, minion.getY() - sprite.getWidth() / 2f);
        sprite.setRotation(rotation.getCurrentValue());
        sprite.draw(batch, alpha.getCurrentValue());
        return hidden;
    }
}
