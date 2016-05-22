package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
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
    private static final float TRACK_REFRESH = 0.2f;
    private final Player parent;
    private final Vector2 movement = new Vector2();
    private float timeSinceLastTrack = MathUtils.random(TRACK_REFRESH);
    private float health = 20f;

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
        timeSinceLastTrack += delta;
        if (timeSinceLastTrack > TRACK_REFRESH) {
            timeSinceLastTrack = 0;
            updateMovement();
        }
        body.applyForceToCenter(delta * movement.x * Box2DUtil.MINION_SPEED,
                delta * movement.y * Box2DUtil.MINION_SPEED, true);
    }

    private void updateMovement() {
        final float test = MathUtils.random();
        if (test < 0.1f) { // Completely random movement;
            goTo(MathUtils.random(-Box2DUtil.WIDTH / 2f, Box2DUtil.WIDTH / 2f),
                    MathUtils.random(-Box2DUtil.HEIGHT / 2f, Box2DUtil.HEIGHT / 2f));
        } else if (test < 0.6f) {// Going directly to parent:
            goTo(parent.getX(), parent.getY());
        } else { // Going near parent:
            goTo(parent.getX() + MathUtils.random(-4f, -4f), parent.getY() + +MathUtils.random(-4f, -4f));
        }
    }

    private void goTo(final float x, final float y) {
        final float angle = MathUtils.atan2(y - getY(), x - getX());
        movement.x = MathUtils.cos(angle);
        movement.y = MathUtils.sin(angle);
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

    @Override
    public void beginCollision(final Entity entity) {
        if (entity.getType() == EntityType.MINION) {
            final Minion other = (Minion) entity;
            if (other.getId() <= getId()) {
                // We want just one of the minions to handle collision.
                return;
            }
            final float offset = getDirectionOffset(body, other.body);
            final float otherOffset = getDirectionOffset(other.body, body);
            if (offset > otherOffset) {
                damage(other.body);
            } else if (otherOffset > offset) {
                other.damage(body);
            } else {
                final float force = getTotalForce(body);
                final float otherForce = getTotalForce(other.body);
                if (force > otherForce) {
                    other.damage(body);
                } else if (force < otherForce) {
                    damage(other.body);
                }
            }
        }
    }

    /** @param body body of the minion that damaged this entity. */
    private void damage(final Body body) {
        final float angle = MathUtils.atan2(body.getPosition().y - this.body.getPosition().y,
                body.getPosition().x - this.body.getPosition().x);
        this.body.applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.MINION_SPEED,
                MathUtils.sin(angle) * Box2DUtil.MINION_SPEED, true);
        damage(getTotalForce(body));
    }

    /** @param health will be subtracted from minion's health. */
    public void damage(final float health) {
        this.health -= health;
        if (this.health <= 0f) {
            setDestroyed(true);
        }
    }

    @Override
    public void setDestroyed(final boolean destroyed) {
        if (isDestroyed()) {
            return;
        }
        super.setDestroyed(destroyed);
        parent.removeMinion();
    }
}
