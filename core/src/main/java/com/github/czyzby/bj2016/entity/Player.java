package com.github.czyzby.bj2016.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.github.czyzby.bj2016.entity.sprite.SpriteType;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;

/** Represents player Box2D entities.
 *
 * @author MJ */
public class Player extends AbstractEntity {
    private final Control control;
    private final SpriteType sprite;
    private int minionsAmount;

    public Player(final Box2DService box2d, final int id, final Control control, final SpriteType sprite) {
        super(box2d, id);
        this.control = control;
        this.sprite = sprite;
        setPosition(id);
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

    /** Increments minions counter. */
    public void addMinion() {
        minionsAmount++;
    }

    /** Decrements minions counter. */
    public void removeMinion() {
        minionsAmount--;
        if (minionsAmount == 0) {
            setDestroyed(true);
        }
    }

    /** @return current amount of minions. */
    public int getMinionsAmount() {
        return minionsAmount;
    }
}
