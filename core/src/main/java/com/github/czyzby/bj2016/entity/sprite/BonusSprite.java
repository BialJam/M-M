package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.czyzby.bj2016.entity.Bonus;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Draws a single bonus sprite.
 *
 * @author MJ */
public class BonusSprite {
    private final Bonus bonus;
    private final Sprite sprite;
    private final FloatRange alpha = new FloatRange(1f, 0.2f);
    private boolean removing;
    private boolean hidden;

    public BonusSprite(final Bonus bonus, final Sprite sprite) {
        this.bonus = bonus;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setPosition(bonus.getX() - sprite.getWidth() / 2f, bonus.getY() - sprite.getHeight() / 2f);
    }

    /** @return bonus Box2D entity. */
    public Bonus getBonus() {
        return bonus;
    }

    /** @return bonus drawable. */
    public Sprite getSprite() {
        return sprite;
    }

    /** @param must be begun.
     * @param delta time passed since last render.
     * @return true if sprite should be removed. */
    public boolean render(final Batch batch, final float delta) {
        if (bonus.isDestroyed()) {
            if (!removing) {
                removing = true;
                alpha.setTargetValue(0f);
            } else if (alpha.isTransitionInProgress()) {
                alpha.update(delta);
            } else {
                hidden = true;
            }
        }
        sprite.draw(batch, alpha.getCurrentValue());
        return hidden;
    }
}
