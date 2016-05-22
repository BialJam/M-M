package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.scene2d.range.FloatRange;

/** Draws a single obstacle.
 *
 * @author MJ */
public class BlockSprite implements EntitySprite {
    private final Block block;
    private final Sprite sprite;
    private final FloatRange height = new FloatRange(0f, 0.2f);
    private boolean removing;
    private boolean hidden;

    public BlockSprite(final Block block, final Sprite sprite) {
        this.block = block;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setPosition(block.getX() - sprite.getWidth() / 2f, block.getY() - sprite.getHeight() / 2f);
        height.setTargetValue(sprite.getHeight());
    }

    @Override
    public boolean render(final Batch batch, final float delta) {
        if (block.isDestroyed()) {
            if (!removing) {
                height.setTargetValue(0f);
                removing = true;
            } else if (height.isTransitionInProgress()) {
                height.update(delta);
                sprite.setSize(sprite.getWidth(), height.getCurrentValue());
            } else {
                hidden = true;
            }
        } else if (height.isTransitionInProgress()) {
            height.update(delta);
            sprite.setSize(sprite.getWidth(), height.getCurrentValue());
        }
        sprite.draw(batch);
        return hidden;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
