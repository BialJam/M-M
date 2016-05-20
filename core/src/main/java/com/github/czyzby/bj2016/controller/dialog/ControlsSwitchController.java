package com.github.czyzby.bj2016.controller.dialog;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.bj2016.service.ControlsService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.service.controls.ControlType;
import com.github.czyzby.bj2016.service.controls.impl.GamePadControl;
import com.github.czyzby.bj2016.service.controls.impl.InactiveControl;
import com.github.czyzby.bj2016.service.controls.impl.KeyboardControl;
import com.github.czyzby.bj2016.service.controls.impl.TouchControl;

/** Allows to switch control types. */
@ViewDialog(id = "switch", value = "ui/templates/dialogs/switch.lml", cacheInstance = true)
public class ControlsSwitchController implements ActionContainer, ViewDialogShower {
    @Inject private ControlsService service;
    @Inject private InterfaceService interfaceService;

    @Inject private ControlsController controlsController;
    @Inject private ControlsEditController editController;

    @LmlActor("PAD") private Button gamePadControlButton;
    private int playerId;

    /** @param playerId this screen will be used to choose controls for this player. */
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void doBeforeShow(final Window dialog) {
        gamePadControlButton.setDisabled(GdxArrays.isEmpty(Controllers.getControllers()));
    }

    @LmlAction("controls")
    public Iterable<ControlType> getControlTypes() {
        if (GdxUtilities.isRunningOnAndroid()) {
            // Keyboard controls on Android do not work well...
            return GdxArrays.newArray(ControlType.TOUCH, ControlType.PAD, ControlType.INACTIVE);
        } else if (GdxUtilities.isRunningOnIOS()) {
            // Controllers (pads) do not exactly work on iOS.
            return GdxArrays.newArray(ControlType.TOUCH, ControlType.INACTIVE);
        } // Desktop supports all controllers:
        return GdxArrays.newArray(ControlType.values());
    }

    @LmlAction("TOUCH")
    public void setTouchControls() {
        changeControls(new TouchControl());
    }

    @LmlAction("INACTIVE")
    public void setInactiveControls() {
        changeControls(new InactiveControl());
    }

    @LmlAction("KEYBOARD")
    public void setKeyboardControls() {
        changeControls(new KeyboardControl());
    }

    @LmlAction("PAD")
    public void setGamePadControls() {
        final Array<Controller> controllers = Controllers.getControllers();
        if (GdxArrays.isEmpty(controllers)) {
            changeControls(new InactiveControl());
        } else {
            changeControls(new GamePadControl(controllers.first()));
        }
    }

    private void changeControls(final Control control) {
        service.setControl(playerId, control);
        controlsController.refreshPlayerView(playerId, control);
        if (control.isActive()) {
            editController.setControl(control);
            interfaceService.showDialog(ControlsEditController.class);
        }
    }
}