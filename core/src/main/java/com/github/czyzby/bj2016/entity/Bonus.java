package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents bonuses entities.
 *
 * @author MJ */
public class Bonus extends AbstractEntity {
    private float duration = 10f;
    private final BonusType bonus;
    private Player target;

    public Bonus(final Box2DService box2d, final BonusType bonusType) {
        super(box2d);
        bonus = bonusType;
    }

    /** @return type of this bonus. */
    public BonusType getBonus() {
        return bonus;
    }

    @Override
    public EntityType getType() {
        return EntityType.BONUS;
    }

    @Override
    public void update(final float delta) {
        duration -= delta;
        if (duration <= 0f) {
            setDestroyed(true);
        }
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(Block.HALF_SIZE, Block.HALF_SIZE);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_BLOCK;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_BLOCK;
        final Body body = box2d.getWorld().createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (target != null) {
            bonus.apply(box2d, target);
        }
    }

    @Override
    public void beginCollision(final Entity entity) {
        if (!isDestroyed() && duration > 1f && entity.getType() == EntityType.PLAYER) {
            target = (Player) entity;
            setDestroyed(true);
        }
    }
}
