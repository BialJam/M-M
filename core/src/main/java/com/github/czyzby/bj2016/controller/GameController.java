package com.github.czyzby.bj2016.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.PlayerSprite;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.GameAssetService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Renders Box2D world. */
@View(id = "game", value = "ui/templates/game.lml")
public class GameController extends StandardViewShower implements ViewResizer, ViewRenderer {
    @Inject private Box2DService box2d;
    @Inject private GameAssetService gameAssetService;
    private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
    private final Array<PlayerSprite> sprites = GdxArrays.newArray();
    private final float white = Color.WHITE.toFloatBits();

    @Override
    public void show(final Stage stage, final Action action) {
        box2d.create();
        sprites.clear();
        for (final Player player : box2d.getPlayers()) {
            final Sprite sprite = gameAssetService.getSprite(player.getSprite().getDrawableName());
            sprites.add(new PlayerSprite(player, sprite));
        }
        super.show(stage, Actions.sequence(action, Actions.run(new Runnable() {
            @Override
            public void run() { // Listening to user input events:
                final InputMultiplexer inputMultiplexer = new InputMultiplexer(stage);
                box2d.initiateControls(inputMultiplexer);
                Gdx.input.setInputProcessor(inputMultiplexer);
            }
        })));
    }

    @Override
    public void resize(final Stage stage, final int width, final int height) {
        box2d.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(final Stage stage, final float delta) {
        if (box2d.update(delta)) {
            sprites.sort();
        }
        renderer.render(box2d.getWorld(), box2d.getViewport().getCamera().combined);
        final Batch batch = stage.getBatch();
        batch.setProjectionMatrix(box2d.getViewport().getCamera().combined);
        batch.begin();
        batch.setColor(white);
        for (final PlayerSprite sprite : sprites) {
            sprite.update(delta);
            sprite.draw(batch);
        }
        batch.end();
        stage.act(delta);
        stage.draw();
    }
}