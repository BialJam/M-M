package com.github.czyzby.bj2016.service.controls;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** Abstract base for all controls. */
public abstract class AbstractControl implements Control {
    /** Sin value at NE corner. */
    protected static final float SIN = MathUtils.sin(MathUtils.atan2(1f, 1f));
    /** Cos value at NE corner. */
    protected static final float COS = MathUtils.cos(MathUtils.atan2(1f, 1f));

    protected Vector2 movement = new Vector2();

    @Override
    public Vector2 getMovementDirection() {
        return movement;
    }

    /** @param angle in radians. */
    protected void updateMovementWithAngle(final float angle) {
        movement.x = MathUtils.cos(angle);
        movement.y = MathUtils.sin(angle);
    }

    /** Stops movement. */
    protected void stop() {
        movement.set(0f, 0f);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void reset() {
        movement.set(0f, 0f);
    }
}