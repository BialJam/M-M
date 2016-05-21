package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents bounds.
 *
 * @author MJ */
public class BoundsEntity extends AbstractEntity {
    public BoundsEntity(final Box2DService box2d) {
        super(box2d);
    }

    @Override
    public EntityType getType() {
        return EntityType.BOUND;
    }

    @Override
    public void update(final float delta) {
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        final ChainShape shape = new ChainShape();
        final float w = Box2DUtil.WIDTH * 2f / 5f, h = Box2DUtil.HEIGHT * 2f / 5f;
        shape.createLoop(new float[] { -w, -h, -w, h, w, h, w, -h });
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 0.9f;
        fixtureDef.friction = 0.2f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_BOUNDS;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_BLOCK;
        final Body body = box2d.getWorld().createBody(bodyDef);
        body.createFixture(fixtureDef);
        return body;
    }

    @Override
    public void beginCollision(final Entity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            if (box2d.isSoloMode()) {
                ((Player) entity).damage(Minion.getTotalForce(entity.getBody()));
            }
            final Vector2 velocity = entity.getBody().getLinearVelocity();
            final float angle = MathUtils.atan2(velocity.y + MathUtils.random(-1f, 1f),
                    velocity.x + MathUtils.random(-1f, 1f));
            entity.getBody().applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.PLAYER_SPEED,
                    MathUtils.sin(angle) * Box2DUtil.PLAYER_SPEED, true);
        }
    }
}
