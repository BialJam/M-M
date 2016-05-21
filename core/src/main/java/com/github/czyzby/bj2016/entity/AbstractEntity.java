package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.github.czyzby.bj2016.service.Box2DService;

/** Abstract base for Box2D entities.
 *
 * @author MJ */
public abstract class AbstractEntity implements Entity {
    protected final Box2DService box2d;
    protected final Body body;
    private boolean destroyed;
    private final int id;

    public AbstractEntity(final Box2DService box2d) {
        this(box2d, -1);
    }

    public AbstractEntity(final Box2DService box2d, final int id) {
        this.id = id;
        this.box2d = box2d;
        body = createBody(box2d);
        body.setUserData(this);
        for (final Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }
    }

    /** @return player ID of the sprite. */
    public int getId() {
        return id;
    }

    /** @param box2d manages Box2D world.
     * @return entity unique body. */
    protected abstract Body createBody(Box2DService box2d);

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setDestroyed(final boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void destroy() {
        box2d.getWorld().destroyBody(body);
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }

    @Override
    public void beginCollision(final Entity entity) {
    }

    @Override
    public void endCollision(final Entity entity) {
    }

    /** @param body entity body.
     * @return combined values of x and y velocities. */
    public static float getTotalForce(final Body body) {
        return Math.abs(body.getLinearVelocity().x) + Math.abs(body.getLinearVelocity().y);
    }

    /** @param body entity body.
     * @param other collider.
     * @return difference between entity velocity and ideal vector pointing at the collider. */
    public static float getDirectionOffset(final Body body, final Body other) {
        final Vector2 velocity = body.getLinearVelocity();
        float angle = MathUtils.atan2(velocity.y, velocity.x);
        final float x = MathUtils.cos(angle);
        final float y = MathUtils.sin(angle);
        angle = MathUtils.atan2(other.getPosition().y - body.getPosition().y,
                other.getPosition().x - body.getPosition().x);
        return Math.abs(x - MathUtils.cos(angle)) + Math.abs(y - MathUtils.sin(angle));
    }
}
