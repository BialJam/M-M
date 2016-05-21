package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.github.czyzby.bj2016.service.Box2DService;

public class Minion extends AbstractEntity {
    public Minion(final Box2DService box2d, final int playerId) {
        super(box2d);
    }

    @Override
    public EntityType getType() {
        return EntityType.MINION;
    }

    @Override
    public void update(final float delta) {
        // TODO A*
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 2f;

        return null;
    }

}
