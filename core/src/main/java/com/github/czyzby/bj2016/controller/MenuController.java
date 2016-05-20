package com.github.czyzby.bj2016.controller;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.bj2016.controller.dialog.NotEnoughPlayersErrorController;
import com.github.czyzby.bj2016.service.ControlsService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** Thanks to View annotation, this class will be automatically found and initiated.
 *
 * This is application's main view, displaying a menu with several options. */
@View(id = "menu", value = "ui/templates/menu.lml")
public class MenuController implements ActionContainer {
    @Inject private InterfaceService interfaceService;
    @Inject private ControlsService controlsService;

    @LmlAction("startGame")
    public void startPlaying() {
        if (isAnyPlayerActive()) {
            interfaceService.show(GameController.class);
        } else {
            interfaceService.showDialog(NotEnoughPlayersErrorController.class);
        }
    }

    private boolean isAnyPlayerActive() {
        final Array<Control> controls = controlsService.getControls();
        for (final Control control : controls) {
            if (control.isActive()) {
                return true;
            }
        }
        return false;
    }
}