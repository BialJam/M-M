package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Draws a single obstacle.
 *
 * @author MJ */
public class BlockSprite {
    private final Block block;
    private final Sprite sprite;

    public BlockSprite(final Block block, final Sprite sprite) {
        this.block = block;
        this.sprite = sprite;
        sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
        sprite.setPosition(block.getX() - sprite.getWidth() / 2f, block.getY() - sprite.getHeight() / 2f);
    }

    /** @param delta time since last update.
     * @return true if block should be removed. */
    public boolean update(final float delta) {
        return block.isDestroyed();
    }

    /** @param batch must be begun. */
    public void draw(final Batch batch) {
        sprite.draw(batch);
    }
}
