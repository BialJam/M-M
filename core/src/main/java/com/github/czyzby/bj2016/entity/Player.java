package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.bj2016.entity.sprite.SpriteType;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Represents player Box2D entities.
 *
 * @author MJ */
public class Player extends AbstractEntity {
    private final Array<Minion> minions = GdxArrays.newArray();
    private final Control control;
    private final SpriteType sprite;
    private int minionsAmount;
    private float health = 100f;

    public Player(final Box2DService box2d, final int id, final Control control, final SpriteType sprite) {
        super(box2d, id);
        this.control = control;
        this.sprite = sprite;
        setPosition(id);
    }

    /** @param health will become health amount. */
    public void setHealth(final float health) {
        this.health = health;
    }

    /** @return current health amount. */
    public float getHealth() {
        return health;
    }

    private void setPosition(final int id) {
        final Vector2 pos = new Vector2();
        switch (id) {
            case 0:
                pos.set(-Box2DUtil.WIDTH / 3f, Box2DUtil.HEIGHT / 3f);
                break;
            case 1:
                pos.set(Box2DUtil.WIDTH / 3f, -Box2DUtil.HEIGHT / 3f);
                break;
            case 2:
                pos.set(Box2DUtil.WIDTH / 3f, Box2DUtil.HEIGHT / 3f);
                break;
            default:
                pos.set(-Box2DUtil.WIDTH / 3f, -Box2DUtil.HEIGHT / 3f);
        }
        body.setTransform(pos, 0f);
    }

    /** @return image representing the player. */
    public SpriteType getSprite() {
        return sprite;
    }

    @Override
    protected Body createBody(final Box2DService box2d) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 2f;
        final Body body = box2d.getWorld().createBody(bodyDef);
        addHeadFixture(body);
        addBodyFixture(body);
        return body;
    }

    private static FixtureDef getFixtureDef(final Shape shape) {
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Box2DUtil.CAT_PLAYERS;
        fixtureDef.filter.maskBits = Box2DUtil.MASK_PLAYER;
        fixtureDef.density = 0.4f;
        fixtureDef.restitution = 0.3f;
        return fixtureDef;
    }

    private static void addHeadFixture(final Body body) {
        final CircleShape shape = new CircleShape();
        shape.setRadius(48f / 18f);
        shape.setPosition(new Vector2(0f, 0.3f));
        body.createFixture(getFixtureDef(shape));
        shape.dispose();
    }

    private static void addBodyFixture(final Body body) {
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(48f / 35f, 96f / 40f, new Vector2(0f, -1f), 0f);
        body.createFixture(getFixtureDef(shape));
        shape.dispose();
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    /** @return input listener attached to player's entity. */
    public Control getControl() {
        return control;
    }

    @Override
    public void update(final float delta) {
        final Vector2 pos = body.getPosition();
        control.update(box2d, box2d.getViewport(), pos.x, pos.y);
        final Vector2 dir = control.getMovementDirection();
        body.applyForceToCenter(dir.x * Box2DUtil.PLAYER_SPEED * delta, dir.y * Box2DUtil.PLAYER_SPEED * delta, true);
    }

    /** @return true if player is currently moving. */
    public boolean isMoving() {
        final Vector2 dir = control.getMovementDirection();
        return dir.x != 0f || dir.y != 0f;
    }

    /** @return list of all minions, including the dead ones. */
    public Array<Minion> getMinions() {
        return minions;
    }

    @Override
    public void destroy() {
        super.destroy();
        minions.clear();
        if (sprite.isFemale()) {
            box2d.getSoundService().playRandomFemaleScream();
        } else {
            box2d.getSoundService().playRandomMaleScream();
        }
    }

    /** Increments minions counter. */
    public void addMinion(final Minion minion) {
        minionsAmount++;
        minions.add(minion);
    }

    /** Decrements minions counter. */
    public void removeMinion() {
        minionsAmount--;
        if (minionsAmount == 0 && !box2d.isSoloMode()) {
            setDestroyed(true);
        }
    }

    /** @return current amount of minions. */
    public int getMinionsAmount() {
        return minionsAmount;
    }

    @Override
    public void beginCollision(final Entity entity) {
        if (box2d.isSoloMode() && entity.getType() == EntityType.PLAYER) {
            final Player other = (Player) entity;
            if (other.getId() < getId()) {
                // Applying the effect only once.
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
        damage(getTotalForce(body) / 2f);
        box2d.getSoundService().playRandomPunchSound();
        this.body.applyForceToCenter(MathUtils.cos(angle) * Box2DUtil.PLAYER_SPEED,
                MathUtils.sin(angle) * Box2DUtil.PLAYER_SPEED, true);
    }

    /** @param health will be subtracted from current health amount. */
    public void damage(final float health) {
        this.health -= health;
        if (this.health <= 0f) {
            setDestroyed(true);
            this.health = 0f;
        }
    }
}
