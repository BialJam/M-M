package com.github.czyzby.bj2016.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.entity.Minion;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.BlockSprite;
import com.github.czyzby.bj2016.entity.sprite.MinionSprite;
import com.github.czyzby.bj2016.entity.sprite.PlayerSprite;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.GameAssetService;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.lml.annotation.LmlActor;

/** Renders Box2D world. */
@View(id = "game", value = "ui/templates/game.lml")
public class GameController extends StandardViewShower implements ViewResizer, ViewRenderer {
    private static final int BG_X = (int) -(Box2DUtil.WIDTH / 2f), BG_Y = (int) -(Box2DUtil.HEIGHT / 2f);

    @Inject private Box2DService box2d;
    @Inject private GameAssetService gameAssetService;
    @LmlActor("player[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") Array<Table> playerViews;
    private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
    private final Array<PlayerSprite> sprites = GdxArrays.newArray();
    private final PooledList<BlockSprite> blocks = PooledList.newList();
    private final PooledList<MinionSprite> minions = PooledList.newList();
    private final float white = Color.WHITE.toFloatBits();
    private Texture background;

    @Override
    public void show(final Stage stage, final Action action) {
        box2d.create();
        background = gameAssetService.getRandomBackground();
        for (final Table table : playerViews) {
            table.setVisible(false);
        }
        createPlayerSprites();
        createBlockSprites();
        createMinionSprites();
        super.show(stage, Actions.sequence(action, Actions.run(new Runnable() {
            @Override
            public void run() { // Listening to user input events:
                final InputMultiplexer inputMultiplexer = new InputMultiplexer(stage);
                box2d.initiateControls(inputMultiplexer);
                Gdx.input.setInputProcessor(inputMultiplexer);
            }
        })));
    }

    private void createPlayerSprites() {
        sprites.clear();
        for (final Player player : box2d.getPlayers()) {
            final Sprite sprite = gameAssetService.getSprite(player.getSprite().getDrawableName());
            sprites.add(new PlayerSprite(player, sprite));
            playerViews.get(player.getId()).setVisible(true);
        }
    }

    private void createBlockSprites() {
        blocks.clear();
        for (final Block block : box2d.getBlocks()) {
            final Sprite sprite = gameAssetService.getSprite(block.getBlockType().getDrawableName());
            blocks.add(new BlockSprite(block, sprite));
        }
    }

    private void createMinionSprites() {
        minions.clear();
        final IntMap<Sprite> minionSprites = new IntMap<Sprite>();
        for (final Minion minion : box2d.getMinions()) {
            if (!minionSprites.containsKey(minion.getId())) {
                final Sprite sprite = gameAssetService.getSprite(minion.getParent().getSprite().getSmallDrawableName());
                sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
                sprite.setOrigin(sprite.getRegionWidth() / 2f / Box2DUtil.PPU,
                        sprite.getRegionWidth() / 2f / Box2DUtil.PPU);
                minionSprites.put(minion.getId(), sprite);
            }
            minions.addFirst(new MinionSprite(minion, minionSprites.get(minion.getId())));
        }
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
        batch.begin();
        batch.setProjectionMatrix(box2d.getViewport().getCamera().combined);
        batch.setColor(white);
        // batch.draw(background, BG_X, BG_Y, Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
        for (final BlockSprite block : blocks) {
            if (block.update(delta)) {
                blocks.remove();
            } else {
                block.draw(batch);
            }
        }
        for (final MinionSprite minion : minions) {
            if (minion.render(batch, delta)) {
                minions.remove();
            }
        }
        for (final PlayerSprite sprite : sprites) {
            sprite.update(delta);
            sprite.draw(batch);
        }
        batch.end();
        stage.act(delta);
        stage.draw();
    }
}