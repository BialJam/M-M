package com.github.czyzby.bj2016.service.controls;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.bj2016.configuration.preferences.ControlsData;

/** Represents player entity controls. */
public interface Control {
    /** @param inputMultiplexer can be used to attach an input processor. */
    void attachInputListener(InputMultiplexer inputMultiplexer);

    /** @param gameViewport current state of game viewport. Might be used to convert units.
     * @param gameX x position of controlled entity in game units.
     * @param gameY y position of controlled entity in game units. */
    void update(Viewport gameViewport, float gameX, float gameY);

    /** @return current movement direction. Values should add up to [-1, 1]. */
    Vector2 getMovementDirection();

    /** @return serialized controls values that can be saved and read from. */
    ControlsData toData();

    /** @param data saved controls values that should be read. */
    void copy(ControlsData data);

    /** @return true if the player is active and can play with these controls. */
    boolean isActive();

    /** @return type of controls. */
    ControlType getType();

    /** Clears state variables. */
    void reset();
}