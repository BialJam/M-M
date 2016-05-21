package com.github.czyzby.bj2016.service.controls;

import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.bj2016.configuration.preferences.ControlsData;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.service.Box2DService;

/** Abstract base for controls that use buttons, like keyboard keys or game pads buttons. */
public abstract class AbstractButtonControl extends AbstractControl {
    protected IntSet pressedButtons = new IntSet(4);

    protected int up;
    protected int down;
    protected int left;
    protected int right;

    /** Updates current movement according to button states. */
    protected void updateMovement() {
        if (pressedButtons.size == 0) {
            stop();
        } else if (isPressed(up)) {
            if (isPressed(left)) { // Up-left.
                movement.set(-COS, SIN);
            } else if (isPressed(right)) { // Up-right.
                movement.set(COS, SIN);
            } else { // Up.
                movement.set(0f, 1f);
            }
        } else if (isPressed(down)) {
            if (isPressed(left)) { // Down-left.
                movement.set(-COS, -SIN);
            } else if (isPressed(right)) { // Down-right.
                movement.set(COS, -SIN);
            } else { // Down.
                movement.set(0f, -1f);
            }
        } else if (isPressed(left)) { // Left.
            movement.set(-1f, 0f);
        } else if (isPressed(right)) { // Right.
            movement.set(1f, 0f);
        } else {
            stop();
        }
    }

    @Override
    public void update(final Box2DService box2d, final Viewport viewport, final float gameX, final float gameY) {
        // Button controls usually do not need relative position of controlled entity.
    }

    /** @param key button code.
     * @return true if button is currently pressed. */
    protected boolean isPressed(final int key) {
        return pressedButtons.contains(key);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = new ControlsData(getType());
        data.up = up;
        data.down = down;
        data.left = left;
        data.right = right;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        up = data.up;
        down = data.down;
        left = data.left;
        right = data.right;
    }

    @Override
    public void reset(final Player player) {
        super.reset(player);
        pressedButtons.clear();
    }

    /** @return up movement button code. */
    public int getUp() {
        return up;
    }

    /** @param up will become up movement button code. */
    public void setUp(final int up) {
        pressedButtons.remove(this.up);
        updateMovement();
        this.up = up;
    }

    /** @return down movement button code. */
    public int getDown() {
        return down;
    }

    /** @param down will become down movement button code. */
    public void setDown(final int down) {
        pressedButtons.remove(this.down);
        updateMovement();
        this.down = down;
    }

    /** @return left movement button code. */
    public int getLeft() {
        return left;
    }

    /** @param left will become left movement button code. */
    public void setLeft(final int left) {
        pressedButtons.remove(this.left);
        updateMovement();
        this.left = left;
    }

    /** @return right movement button code. */
    public int getRight() {
        return right;
    }

    /** @param right will become right movement button code. */
    public void setRight(final int right) {
        pressedButtons.remove(this.right);
        updateMovement();
        this.right = right;
    }
}