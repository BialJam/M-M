package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents players minion.
 *
 * @author MJ */
public class Minion extends AbstractEntity {
    private final Player parent;

    public Minion(final Box2DService box2d, final Player parent) {
        super(box2d, parent.getId());
        this.parent = parent;
    }

    @Override
    public EntityType getType() {
        return EntityType.MINION;
    }

    /** @return owner of the minion. */
    public Player getParent() {
        return parent;
    }

    @Override
    public void update(final float delta) {
        // TODO A* to parent
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 2f;
        final Body body = box2d.getWorld().createBody(bodyDef);

        final CircleShape shape = new CircleShape();
        shape.setRadius(1f);
        shape.setPosition(new Vector2(0f, 0.3f));

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = getCatBits();
        fixtureDef.filter.maskBits = getMaskBits();
        fixtureDef.density = 0.4f;
        fixtureDef.restitution = 0.3f;
        body.createFixture(fixtureDef);
        shape.dispose();

        final PolygonShape coreShape = new PolygonShape();
        coreShape.setAsBox(0.5f, 0.5f);
        fixtureDef.shape = coreShape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_MINION;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_MINION_CORE;
        body.createFixture(fixtureDef);
        coreShape.dispose();

        return body;
    }

    private short getCatBits() {
        switch (getId()) {
            case 0:
                return Box2DUtil.CAT_MINION0;
            case 1:
                return Box2DUtil.CAT_MINION1;
            case 2:
                return Box2DUtil.CAT_MINION2;
            default:
                return Box2DUtil.CAT_MINION3;
        }
    }

    private short getMaskBits() {
        switch (getId()) {
            case 0:
                return Box2DUtil.MASK_MINION_P0;
            case 1:
                return Box2DUtil.MASK_MINION_P1;
            case 2:
                return Box2DUtil.MASK_MINION_P2;
            default:
                return Box2DUtil.MASK_MINION_P3;
        }
    }
}
