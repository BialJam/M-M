package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Renders a single effect.
 *
 * @author MJ */
public class EffectSprite implements EntitySprite {
    private final EffectType effect;
    private final Sprite sprite;
    private final FloatRange alpha = new FloatRange(0f, 0.25f);
    private boolean removing;

    public EffectSprite(final EffectType effect, final Sprite sprite, final float x, final float y) {
        this.effect = effect;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);
        alpha.setTargetValue(1f);
    }

    @Override
    public boolean render(final Batch batch, final float delta) {
        if (alpha.isTransitionInProgress()) {
            alpha.update(delta);
        } else {
            if (removing) {
                return true;
            }
            removing = true;
            alpha.setTargetValue(0f);
        }
        sprite.draw(batch, alpha.getCurrentValue());
        return false;
    }

    /** @return type of displayed effect. */
    public EffectType getEffect() {
        return effect;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
