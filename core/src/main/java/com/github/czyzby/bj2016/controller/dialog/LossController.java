package com.github.czyzby.bj2016.controller.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.ControlsService;
import com.github.czyzby.bj2016.service.PlayerService;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

/** Shown when there are no active human players. */
@ViewDialog(id = "loss", value = "ui/templates/dialogs/loss.lml", cacheInstance = true)
public class LossController implements ViewDialogShower {
    @LmlActor("scores") private Table scoresTable;
    @Inject private ControlsService controlsService;
    @Inject private Box2DService box2dService;
    @Inject private LocaleService localeService;
    @Inject private PlayerService playerService;

    @Override
    public void doBeforeShow(final Window dialog) {
        // Too lazy to implement in LML. I'm tired.
        scoresTable.clear();
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < controls.size; index++) {
            final Control control = controls.get(index);
            if (control.isActive() && control.isHumanControlled()) {
                final VisLabel name = new VisLabel(
                        localeService.getI18nBundle().get(playerService.getSpriteType(index).name()),
                        VisUI.getSkin().getColor("vis-blue"));
                scoresTable.add(name).expand().align(Align.left).padRight(5f);
                scoresTable.add(String.valueOf(box2dService.getPoints(index))).expand().align(Align.right).padLeft(5f)
                        .row();
            }
        }
    }
}
