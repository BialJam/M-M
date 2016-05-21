package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.czyzby.bj2016.entity.sprite.BlockType;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents a single block entity.
 *
 * @author MJ */
public class Block extends AbstractEntity {
    private static final float HALF_SIZE = 48f / Box2DUtil.PPU / 2f;
    private final BlockType blockType;

    public Block(final Box2DService box2d, final BlockType blockType) {
        super(box2d);
        this.blockType = blockType;
    }

    @Override
    public EntityType getType() {
        return EntityType.BLOCK;
    }

    @Override
    public void update(final float delta) {
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(HALF_SIZE, HALF_SIZE);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_BLOCK;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_BLOCK;
        final Body body = box2d.getWorld().createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    /** @return displayed block type. */
    public BlockType getBlockType() {
        return blockType;
    }
}
