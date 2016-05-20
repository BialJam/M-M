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
}
