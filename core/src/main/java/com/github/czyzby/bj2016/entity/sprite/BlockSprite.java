package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Draws a single obstacle.
 *
 * @author MJ */
public class BlockSprite {
    private final Block block;
    private final Sprite sprite;
    private FloatRange height;
    private boolean hidden;

    public BlockSprite(final Block block, final Sprite sprite) {
        this.block = block;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setPosition(block.getX() - sprite.getWidth() / 2f, block.getY() - sprite.getHeight() / 2f);
    }

    /** @param delta time since last update.
     * @return true if block should be removed. */
    public boolean update(final float delta) {
        if (block.isDestroyed()) {
            if (height == null) {
                height = new FloatRange(sprite.getHeight(), 0.2f);
                height.setTargetValue(0f);
            } else if (height.isTransitionInProgress()) {
                height.update(delta);
                sprite.setSize(sprite.getWidth(), height.getCurrentValue());
            } else {
                hidden = true;
            }
        }
        return hidden;
    }

    /** @param batch must be begun. */
    public void draw(final Batch batch) {
        sprite.draw(batch);
    }
}
