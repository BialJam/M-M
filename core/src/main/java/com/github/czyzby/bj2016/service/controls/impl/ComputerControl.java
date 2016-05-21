package com.github.czyzby.bj2016.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.bj2016.configuration.preferences.ControlsData;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.controls.AbstractControl;
import com.github.czyzby.bj2016.service.controls.ControlType;

/** Represents automated controls.
 *
 * @author MJ */
public class ComputerControl extends AbstractControl {
    // Updates are dependent on the time step (which is static), so they will not be affected by FPS.
    private static final int UPDATES_TO_CHANGE_TARGET = 30 * 10;
    private static final int UPDATES_TO_CHANGE_MOVEMENT = 30;
    private Player bot, target;
    private int timeSinceTarget = UPDATES_TO_CHANGE_TARGET;
    private int timeSinceMoveChange = UPDATES_TO_CHANGE_MOVEMENT;

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
    }

    @Override
    public void update(final Box2DService box2d, final Viewport viewport, final float gameX, final float gameY) {
        timeSinceTarget++;
        timeSinceMoveChange++;
        if (target != null && target.isDestroyed()) {
            timeSinceTarget += UPDATES_TO_CHANGE_TARGET;
        }
        if (timeSinceTarget >= UPDATES_TO_CHANGE_TARGET) {
            timeSinceTarget -= UPDATES_TO_CHANGE_TARGET;
            if (box2d.getPlayers().size <= 1) {
                target = bot;
                return;
            }
            target = box2d.getPlayers().random();
            if (target == bot) {
                target = box2d.getPlayers()
                        .get((box2d.getPlayers().indexOf(target, true) + 1) / box2d.getPlayers().size);
            }
        }
        if (timeSinceMoveChange >= UPDATES_TO_CHANGE_MOVEMENT) {
            timeSinceMoveChange -= UPDATES_TO_CHANGE_MOVEMENT;
            final float x = target.getX() + random();
            final float y = target.getY() + random();
            updateMovementWithAngle(MathUtils.atan2(y - bot.getY(), x - bot.getX()));
        }
    }

    private static float random() {
        return MathUtils.randomBoolean() ? MathUtils.random(10f) : -MathUtils.random(10f);
    }

    @Override
    public ControlsData toData() {
        return new ControlsData(getType());
    }

    @Override
    public void copy(final ControlsData data) {
    }

    @Override
    public ControlType getType() {
        return ControlType.BOT;
    }

    @Override
    public void reset(final Player player) {
        super.reset(player);
        bot = player;
        timeSinceTarget = UPDATES_TO_CHANGE_TARGET;
        timeSinceMoveChange = UPDATES_TO_CHANGE_MOVEMENT;
    }

    @Override
    public boolean isHumanControlled() {
        return false;
    }
}
