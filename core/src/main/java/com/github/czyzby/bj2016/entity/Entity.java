package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.physics.box2d.Body;

/** Box2D entity base.
 *
 * @author MJ */
public interface Entity {
    /** @return entities' Box2D body. */
    Body getBody();

    /** @return function of the entity. */
    EntityType getType();

    /** @param delta time since last update. */
    void update(float delta);

    /** @return if entity was destroyed this turn. */
    boolean isDestroyed();

    /** @param destroyed if true, entity will be removed after this update. */
    void setDestroyed(boolean destroyed);

    /** Invoked if the entity reports as destroyed. */
    void destroy();

    /** @return position on X axis. */
    float getX();

    /** @return position on Y axis. */
    float getY();
}
