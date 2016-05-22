package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.sprite.SpriteType;
import com.github.czyzby.bj2016.service.controls.Control;

/** Manages player game settings.
 *
 * @author MJ */
@Component
public class PlayerService {
    @Inject private ControlsService controlsService;
    private final IntMap<SpriteType> sprites = new IntMap<SpriteType>(Configuration.PLAYERS_AMOUNT);

    @Initiate
    public void initiate() {
        int spriteId = 0;
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            sprites.put(index, SpriteType.values()[spriteId++]);
            spriteId %= SpriteType.values().length;
        }
    }

    /** @return direct reference to maps storing players' sprite types. */
    public IntMap<SpriteType> getSprites() {
        return sprites;
    }

    /** @param playerId ID of the player.
     * @return current sprite type. Never null if valid ID. */
    public SpriteType getSpriteType(final int playerId) {
        return sprites.get(playerId);
    }

    /** @param playerId ID of player which changed the sprite.
     * @param spriteType should not be null. */
    public void setSpriteType(final int playerId, final SpriteType spriteType) {
        sprites.put(playerId, spriteType == null ? SpriteType.values()[0] : spriteType);
    }

    /** @param playerId ID of player which changed the sprite.
     * @return next sprite type. */
    public SpriteType setNextSpriteType(final int playerId) {
        final SpriteType type = sprites.get(playerId);
        final int next = (type.ordinal() + 1) % SpriteType.values().length;
        final SpriteType nextType = SpriteType.values()[next];
        setSpriteType(playerId, nextType);
        return nextType;
    }

    /** @param playerId ID of the player who changes sprite.
     * @return previous sprite type. */
    public SpriteType setPreviousSpriteType(final int playerId) {
        final SpriteType type = sprites.get(playerId);
        int prev = type.ordinal() - 1;
        if (prev < 0) {
            prev = SpriteType.values().length - 1;
        }
        final SpriteType previousType = SpriteType.values()[prev];
        setSpriteType(playerId, previousType);
        return previousType;
    }

    public void rerollComputerSprites() {
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < controls.size; index++) {
            final Control control = controls.get(index);
            if (control.isActive() && !control.isHumanControlled()) {
                SpriteType sprite = SpriteType.getRandom();
                while (isUsed(sprite)) {
                    sprite = SpriteType.values()[(sprite.ordinal() + 1) % SpriteType.values().length];
                }
                sprites.put(index, sprite);
            }
        }
    }

    private boolean isUsed(final SpriteType sprite) {
        for (final SpriteType currentSprite : sprites.values()) {
            if (sprite == currentSprite) {
                return true;
            }
        }
        return false;
    }
}
