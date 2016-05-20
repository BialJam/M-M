package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.mvc.stereotype.Asset;

/** Provides (semi-)static access to game assets.
 *
 * @author MJ */
@Component
public class GameAssetService {
    @Asset("game/sprites.atlas") private TextureAtlas atlas;

    /** @return atlas with all game sprites. */
    public TextureAtlas getAtlas() {
        return atlas;
    }

    /** @param drawableName name of texture region.
     * @return drawable from the atlas. */
    public TextureRegion getRegion(final String drawableName) {
        return atlas.findRegion(drawableName);
    }

    /** @param drawableName name of texture region.
     * @return drawable created using chosen region. */
    public Drawable getDrawable(final String drawableName) {
        return new TextureRegionDrawable(getRegion(drawableName));
    }

    /** @param drawableName name of texture region.
     * @return sprite created using chosen region. */
    public Sprite getSprite(final String drawableName) {
        return atlas.createSprite(drawableName);
    }
}
