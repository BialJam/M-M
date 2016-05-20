package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents player sprites.
 *
 * @author MJ */
public class Player extends AbstractEntity {
    private final Control control;

    public Player(final Box2DService box2d, final Control control) {
        super(box2d);
        this.control = control;
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 1f; // TODO

        final CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_PLAYERS;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_PLAYER;
        fixtureDef.density = 0.4f; // TODO
        fixtureDef.restitution = 0.3f;

        final Body body = box2d.getWorld().createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void update(final float delta) {
        final Vector2 pos = body.getPosition();
        control.update(box2d.getViewport(), pos.x, pos.y);
        final Vector2 dir = control.getMovementDirection();
        body.applyForceToCenter(dir.x * Box2DUtil.PLAYER_SPEED * delta, dir.y * Box2DUtil.PLAYER_SPEED, true);
    }
}
