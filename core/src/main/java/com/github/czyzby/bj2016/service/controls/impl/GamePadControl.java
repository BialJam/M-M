package com.github.czyzby.bj2016.service.controls.impl;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.MathUtils;
import com.github.czyzby.bj2016.configuration.preferences.ControlsData;
import com.github.czyzby.bj2016.service.controls.AbstractButtonControl;
import com.github.czyzby.bj2016.service.controls.ControlType;

/** Allows to control an entity with controller events. */
public class GamePadControl extends AbstractButtonControl {
    private static final float DEADZONE = 0.2f;
    /** Axis alignment. */
    protected static final int X_LEFT = 0, X_RIGHT = 3, Y_LEFT = 1, Y_RIGHT = 2;
    /** Left becomes right. */
    private boolean invertX;
    /** Up becomes down. */
    private boolean invertY;
    /** Left becomes up. */
    private boolean invertXY;

    protected float axisX;
    protected float axisY;
    private Controller controller;
    private int controllerIndex;
    private final ControllerListener controllerListener = new ControllerAdapter() {
        @Override
        public boolean axisMoved(final Controller controller, final int axisIndex, final float value) {
            if (isAssignedTo(controller)) {
                updateAxisValue(axisIndex, value);
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonDown(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.add(buttonIndex);
                    updateMovement();
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean buttonUp(final Controller controller, final int buttonIndex) {
            if (isAssignedTo(controller)) {
                if (buttonIndex == up || buttonIndex == down || buttonIndex == left || buttonIndex == right) {
                    pressedButtons.remove(buttonIndex);
                    updateMovement();
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean povMoved(final Controller controller, final int povIndex, final PovDirection direction) {
            if (isAssignedTo(controller)) {
                if (direction != null) {
                    if (direction == PovDirection.center) {
                        stop();
                    } else {
                        movement.x = getX(direction);
                        movement.y = getY(direction);
                    }
                }
                return true;
            }
            return false;
        }
    };

    public GamePadControl() {
        up = 0;
        down = 2;
        left = 3;
        right = 1;
    }

    /** @param controller will be used to control the entity. */
    public GamePadControl(final Controller controller) {
        this();
        this.controller = controller;
    }

    /** @return the device that this input processor is assigned to. */
    public Controller getController() {
        return controller;
    }

    /** @param controller will be used to control the entity. */
    public void setController(final Controller controller) {
        if (this.controller != null) {
            this.controller.removeListener(controllerListener);
        }
        this.controller = controller;
        if (controller != null) {
            controllerIndex = Controllers.getControllers().indexOf(controller, true);
        }
    }

    /** @param controller a {@link Controller} instance. Can be null.
     * @return true if this input processor is assigned to passed controller. */
    public boolean isAssignedTo(final Controller controller) {
        return this.controller.equals(controller);
    }

    protected void updateAxisValue(final int axisIndex, float value) {
        if (isY(axisIndex)) { // Inverting Y coordinates.
            value = -value;
        }
        if (!invertXY && isX(axisIndex) || invertXY && isY(axisIndex)) {
            if (value > DEADZONE || value < -DEADZONE) {
                axisX = invertX ? -value : value;
            } else {
                axisX = 0f;
            }
        } else {
            if (value > DEADZONE || value < -DEADZONE) {
                axisY = invertY ? -value : value;
            } else {
                axisY = 0f;
            }
        }
        if (Float.compare(axisX, 0f) == 0 && Float.compare(axisY, 0f) == 0) {
            stop();
        } else {
            updateMovementWithAngle(MathUtils.atan2(axisY, axisX));
        }

    }

    protected float getX(final PovDirection direction) {
        final float x;
        if (invertXY) { // Checking Y axis (north=east, south=west):
            x = getAbsoluteY(direction);
        } else { // Checking X axis:
            x = getAbsoluteX(direction);
        }
        if (invertX) {
            return -x;
        }
        return x;
    }

    protected float getY(final PovDirection direction) {
        final float y;
        if (invertXY) { // Checking X axis (north=east, south=west):
            y = getAbsoluteX(direction);
        } else { // Checking Y axis:
            y = getAbsoluteY(direction);
        }
        if (invertY) {
            return -y;
        }
        return y;
    }

    protected float getAbsoluteX(final PovDirection direction) {
        if (direction == PovDirection.east) {
            return 1f;
        } else if (direction == PovDirection.northEast || direction == PovDirection.southEast) {
            return COS;
        } else if (direction == PovDirection.west) {
            return -1f;
        } else if (direction == PovDirection.northWest || direction == PovDirection.southWest) {
            return -COS;
        }
        return 0f;
    }

    protected float getAbsoluteY(final PovDirection direction) {
        if (direction == PovDirection.north) {
            return 1f;
        } else if (direction == PovDirection.northEast || direction == PovDirection.northWest) {
            return SIN;
        } else if (direction == PovDirection.south) {
            return -1f;
        } else if (direction == PovDirection.southWest || direction == PovDirection.southEast) {
            return -SIN;
        } else {
            return 0f;
        }
    }

    private static boolean isX(final int axisIndex) {
        return axisIndex == X_LEFT || axisIndex == X_RIGHT;
    }

    private static boolean isY(final int axisIndex) {
        return axisIndex == Y_LEFT || axisIndex == Y_RIGHT;
    }

    protected float getAxisAngle() {
        return MathUtils.atan2(axisY, axisX) * MathUtils.radiansToDegrees;
    }

    /** @return true if X movement is inverted. */
    public boolean isInvertX() {
        return invertX;
    }

    /** @param invertX true to invert X movement. */
    public void setInvertX(final boolean invertX) {
        this.invertX = invertX;
    }

    /** @return true if Y movement is inverted. */
    public boolean isInvertY() {
        return invertY;
    }

    /** @param invertY true to invert Y movement. */
    public void setInvertY(final boolean invertY) {
        this.invertY = invertY;
    }

    /** @return true if X and Y movement are inverted with each other. */
    public boolean isInvertXY() {
        return invertXY;
    }

    /** @param invertXY true to invert X and Y movement with each other. */
    public void setInvertXY(final boolean invertXY) {
        this.invertXY = invertXY;
    }

    @Override
    public void attachInputListener(final InputMultiplexer inputMultiplexer) {
        controller.removeListener(controllerListener); // Making sure listener is not added twice.
        controller.addListener(controllerListener);
    }

    @Override
    public ControlsData toData() {
        final ControlsData data = super.toData();
        data.invertX = invertX;
        data.invertY = invertY;
        data.invertXY = invertXY;
        data.index = controllerIndex;
        return data;
    }

    @Override
    public void copy(final ControlsData data) {
        super.copy(data);
        invertX = data.invertX;
        invertY = data.invertY;
        invertXY = data.invertXY;
    }

    @Override
    public ControlType getType() {
        return ControlType.PAD;
    }
}