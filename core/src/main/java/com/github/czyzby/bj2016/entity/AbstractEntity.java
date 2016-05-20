package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.github.czyzby.bj2016.service.Box2DService;

/** Abstract base for Box2D entities.
 *
 * @author MJ */
public abstract class AbstractEntity implements Entity {
    protected final Box2DService box2d;
    protected final Body body;

    public AbstractEntity(final Box2DService box2d) {
        this.box2d = box2d;
        body = createBody(box2d);
        body.setUserData(this);
        for (final Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }
    }

    /** @param box2d manages Box2D world.
     * @return entity unique body. */
    protected abstract Body createBody(Box2DService box2d);

    @Override
    public Body getBody() {
        return body;
    }
}
