package com.github.czyzby.bj2016.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.annotation.OnMessage;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.mvc.stereotype.Asset;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.bj2016.controller.dialog.NotEnoughPlayersErrorController;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.ControlsService;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

/** Thanks to View annotation, this class will be automatically found and initiated.
 *
 * This is application's main view, displaying a menu with several options. */
@View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
public class MenuController extends StandardViewShower implements ActionContainer, ViewRenderer {
    @Asset("ui/background.png") private Texture backgroundTexture;
    @Inject private InterfaceService interfaceService;
    @Inject private ControlsService controlsService;
    @Inject private Box2DService box2dService;
    private TextureRegion background;

    @OnMessage(AutumnMessage.ASSETS_LOADED)
    public boolean assignBackground() {
        background = new TextureRegion(backgroundTexture, 0, 0, 700, 700);
        return OnMessage.REMOVE;
    }

    @Override
    public void show(final Stage stage, final Action action) {
        box2dService.reset(); // Going back to menu resets progress.
        super.show(stage, action);
    }

    @LmlAction("startGame")
    public void startPlaying() {
        if (controlsService.getActivePlayersAmount() >= 2) {
            interfaceService.show(GameController.class);
        } else {
            interfaceService.showDialog(NotEnoughPlayersErrorController.class);
        }
    }

    @Override
    public void render(final Stage stage, final float delta) {
        stage.act(delta);
        final Batch batch = stage.getBatch();
        batch.setColor(stage.getRoot().getColor());
        batch.begin();
        batch.draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        batch.end();
        stage.draw();
    }
}