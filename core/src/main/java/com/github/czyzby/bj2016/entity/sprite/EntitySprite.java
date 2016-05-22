package com.github.czyzby.bj2016.entity.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** Common interface for all entity sprites.
 *
 * @author MJ */
public interface EntitySprite {
    /** @param batch must be already begun.
     * @param delta time since last update.
     * @return true if the sprite should be removed. */
    boolean render(Batch batch, float delta);

    /** @return rendered sprite instance. */
    Sprite getSprite();
}
