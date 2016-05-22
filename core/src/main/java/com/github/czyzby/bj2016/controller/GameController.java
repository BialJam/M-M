package com.github.czyzby.bj2016.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.impl.StandardViewShower;
import com.github.czyzby.autumn.mvc.component.ui.dto.ThemeOrdering;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.autumn.mvc.stereotype.ViewStage;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.controller.dialog.LossController;
import com.github.czyzby.bj2016.controller.dialog.WinController;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.entity.Bonus;
import com.github.czyzby.bj2016.entity.Minion;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.BlockSprite;
import com.github.czyzby.bj2016.entity.sprite.BonusSprite;
import com.github.czyzby.bj2016.entity.sprite.EffectSprite;
import com.github.czyzby.bj2016.entity.sprite.EffectType;
import com.github.czyzby.bj2016.entity.sprite.MinionSprite;
import com.github.czyzby.bj2016.entity.sprite.PlayerSprite;
import com.github.czyzby.bj2016.service.Box2DService;
import com.github.czyzby.bj2016.service.GameAssetService;
import com.github.czyzby.bj2016.service.PlayerService;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.lml.annotation.LmlActor;

/** Renders Box2D world. */
@View(id = "game", value = "ui/templates/game.lml",
        themes = { "music/funkorama.ogg", "music/buySomething.ogg", "music/unfinishedBusiness.ogg" },
        themeOrdering = ThemeOrdering.RANDOM)
public class GameController extends StandardViewShower implements ViewResizer, ViewRenderer {
    private static final float GAME_LENGTH = 60f; // In seconds.
    private static final int BACKGROUND_X = (int) -(Box2DUtil.WIDTH / 2f),
            BACKGROUND_Y = (int) -(Box2DUtil.HEIGHT / 2f);

    @ViewStage private Stage stage;
    @Inject private InterfaceService interfaceService;
    @Inject private Box2DService box2d;
    @Inject private GameAssetService gameAssetService;
    @Inject private LocaleService localeService;
    @Inject private PlayerService playerService;
    @LmlActor("player[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") Array<Table> playerViews;
    @LmlActor("points[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") Array<Label> pointLabels;
    @LmlActor("icon[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") Array<Image> playerIcons;
    @LmlActor("name[0," + (Configuration.PLAYERS_AMOUNT - 1) + "]") Array<Label> nameLabels;
    @LmlActor("time") private Label timerLabel;
    @LmlActor("solo") private Image soloPrompt;
    private final int[] cachedPoints = new int[Configuration.PLAYERS_AMOUNT];
    private final StringBuilder helperBuilder = new StringBuilder();
    // private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
    private final Array<PlayerSprite> sprites = GdxArrays.newArray();
    private final Array<PlayerSprite> deadPlayers = GdxArrays.newArray();
    private final PooledList<BlockSprite> blocks = PooledList.newList();
    private final PooledList<MinionSprite> minions = PooledList.newList();
    private final PooledList<BonusSprite> bonuses = PooledList.newList();
    private final PooledList<EffectSprite> effects = PooledList.newList();
    private final IntMap<Sprite> minionSprites = new IntMap<Sprite>();
    private final float white = Color.WHITE.toFloatBits();
    private TextureRegion background;

    private float timer;
    private boolean running;

    @Override
    public void show(final Stage stage, final Action action) {
        bonuses.clear();
        effects.clear();
        box2d.create();
        minionSprites.clear();
        running = true;
        timer = 0f;
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
        deadPlayers.clear();
        for (final Player player : box2d.getPlayers()) {
            final Sprite sprite = gameAssetService.getSprite(player.getSprite().getDrawableName());
            sprites.add(new PlayerSprite(player, sprite));
            playerIcons.get(player.getId())
                    .setDrawable(gameAssetService.getDrawable(player.getSprite().getSmallDrawableName()));
            playerViews.get(player.getId()).setVisible(true);
            pointLabels.get(player.getId()).setText(String.valueOf(player.getId()));
            nameLabels.get(player.getId()).setText(localeService.getI18nBundle().get(player.getSprite().name()));
        }
    }

    private void createBlockSprites() {
        blocks.clear();
        for (final Block block : box2d.getBlocks()) {
            addBlock(block);
        }
    }

    /** @param block Box2D entity which will be represented by a sprite. */
    public void addBlock(final Block block) {
        final Sprite sprite = gameAssetService.getSprite(block.getBlockType().getDrawableName());
        blocks.add(new BlockSprite(block, sprite));
    }

    private void createMinionSprites() {
        minions.clear();
        for (final Minion minion : box2d.getMinions()) {
            addMinion(minion);
        }
    }

    /** @param minion will be added to the minions list. */
    public void addMinion(final Minion minion) {
        if (!minionSprites.containsKey(minion.getId())) {
            final Sprite sprite = gameAssetService.getSprite(minion.getParent().getSprite().getSmallDrawableName());
            sprite.setSize(sprite.getRegionWidth() / Box2DUtil.PPU, sprite.getRegionHeight() / Box2DUtil.PPU);
            sprite.setOrigin(sprite.getRegionWidth() / 2f / Box2DUtil.PPU,
                    sprite.getRegionWidth() / 2f / Box2DUtil.PPU);
            minionSprites.put(minion.getId(), sprite);
        }
        minions.addFirst(new MinionSprite(minion, minionSprites.get(minion.getId())));
    }

    @Override
    public void resize(final Stage stage, final int width, final int height) {
        box2d.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(final Stage stage, final float delta) {
        if (running && box2d.update(delta)) {
            sprites.sort();
        }
        // renderer.render(box2d.getWorld(), box2d.getViewport().getCamera().combined);
        final Batch batch = stage.getBatch();
        batch.begin();
        batch.setProjectionMatrix(box2d.getViewport().getCamera().combined);
        batch.setColor(white);
        batch.draw(background, BACKGROUND_X, BACKGROUND_Y, Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
        for (final BonusSprite bonus : bonuses) {
            if (bonus.render(batch, delta)) {
                bonuses.remove();
            }
        }
        for (final BlockSprite block : blocks) {
            if (block.render(batch, delta)) {
                blocks.remove();
            }
        }
        for (final PlayerSprite player : deadPlayers) {
            player.render(batch, delta);
        }
        for (final MinionSprite minion : minions) {
            if (minion.render(batch, delta)) {
                minions.remove();
            }
        }
        updateTimer(delta);
        renderPlayers(delta, batch);
        for (final EffectSprite effect : effects) {
            if (effect.render(batch, delta)) {
                effects.remove();
            }
        }
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    /** @param effect type of spawned effect.
     * @param x Box2D position on X axis.
     * @param y Box2D position on Y axis. */
    public void spawnEffect(final EffectType effect, final float x, final float y) {
        effects.add(new EffectSprite(effect, gameAssetService.getSprite(effect.getDrawableName()), x, y));
    }

    private void updateTimer(final float delta) {
        if (box2d.isSoloMode() || !running) {
            return;
        }
        timer += delta;
        Strings.clearBuilder(helperBuilder);
        if (timer >= GAME_LENGTH) {
            box2d.setSoloMode();
            showSoloPrompt();
            helperBuilder.append("!!!:!!!");
        } else {
            final int minutes = (int) timer / 60;
            final int seconds = (int) timer % 60;
            printHour(minutes);
            helperBuilder.append(':');
            printHour(seconds);
        }
        timerLabel.setText(helperBuilder);
    }

    private void showSoloPrompt() {
        final float duration = 0.8f;
        soloPrompt.setColor(1f, 1f, 1f, 0f);
        soloPrompt.addAction(Actions.sequence(Actions.moveTo(0f, 0f), Actions.alpha(0f), Actions.scaleTo(0f, 0f),
                Actions.parallel(Actions.fadeIn(duration, Interpolation.fade),
                        Actions.moveBy(stage.getWidth() * 2f / 7f, stage.getHeight() / 2f, duration,
                                Interpolation.bounceIn),
                        Actions.rotateBy(720f, duration, Interpolation.bounceIn),
                        Actions.scaleTo(1f, 1f, duration, Interpolation.bounceIn)),
                Actions.delay(0.5f), Actions.fadeOut(0.3f, Interpolation.fade), Actions.removeActor()));
        stage.addActor(soloPrompt);
    }

    private void printHour(final int minutes) {
        // GWT does not support string format.
        if (minutes < 10) {
            helperBuilder.append('0');
        }
        helperBuilder.append(minutes);
    }

    private void renderPlayers(final float delta, final Batch batch) {
        boolean removeDead = false;
        for (final PlayerSprite sprite : sprites) {
            sprite.render(batch, delta);
            final Player player = sprite.getPlayer();
            updatePlayerPoints(player, box2d.isSoloMode() ? (int) player.getHealth() : player.getMinionsAmount());
            if (sprite.isDead()) {
                deadPlayers.add(sprite);
                removeDead = true;
                validatePlayers();
            }
        }
        if (removeDead) {
            for (int index = sprites.size - 1; index >= 0; index--) {
                if (sprites.get(index).isDead()) {
                    sprites.removeIndex(index);
                }
            }
        }
    }

    /** Will end game if all players are dead. */
    private void validatePlayers() {
        if (!running) {
            return;
        }
        if (!isAnyPlayerActive()) {
            running = false;
            interfaceService.showDialog(LossController.class);
        } else if (getActivePlayersCount() == 1) {
            running = false;
            box2d.addPenalty(3);
            box2d.addPoint(getFirstPlayer().getId());
            playerService.rerollComputerSprites();
            interfaceService.showDialog(WinController.class);
        }
    }

    private int getActivePlayersCount() {
        int count = 0;
        for (final Player player : box2d.getPlayers()) {
            if (!player.isDestroyed()) {
                count++;
            }
        }
        return count;
    }

    private Player getFirstPlayer() {
        for (final Player player : box2d.getPlayers()) {
            if (!player.isDestroyed()) {
                return player;
            }
        }
        return null;
    }

    private boolean isAnyPlayerActive() {
        for (final Player player : box2d.getPlayers()) {
            if (!player.isDestroyed() && player.getControl().isHumanControlled()) {
                return true;
            }
        }
        return false;
    }

    private void updatePlayerPoints(final Player player, final int points) {
        if (points != cachedPoints[player.getId()]) {
            cachedPoints[player.getId()] = points;
            Strings.clearBuilder(helperBuilder);
            helperBuilder.append(points);
            pointLabels.get(player.getId()).setText(helperBuilder);
        }
    }

    /** @param bonus will be wrapped with a sprite. */
    public void addBonus(final Bonus bonus) {
        bonuses.add(new BonusSprite(bonus, gameAssetService.getSprite(bonus.getBonus().getDrawableName())));
    }
}