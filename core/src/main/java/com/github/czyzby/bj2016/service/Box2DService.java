package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.controller.GameController;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.entity.Bonus;
import com.github.czyzby.bj2016.entity.BonusType;
import com.github.czyzby.bj2016.entity.BoundsEntity;
import com.github.czyzby.bj2016.entity.Minion;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.BlockType;
import com.github.czyzby.bj2016.entity.sprite.EffectType;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.Grid.CellConsumer;

/** Manages 2D physics engine. */
@Component
public class Box2DService extends AbstractService {
    private static final Vector2 GRAVITY = new Vector2(0f, 0f); // Box2D world gravity vector.
    public static final float STEP = 1f / 30f; // Length of a single Box2D step.
    private static final int LIMIT = 5; // Amount of free squares around the players.
    private static final int BOUND = 2; // Amount of free squares around the bounds.
    private static final int MINIONS_AMOUNT = 81;
    private static final int MINION_ROW_SIZE = 9;
    @Inject private ControlsService controlsService;
    @Inject private PlayerService playerService;
    @Inject private GridService gridService;
    @Inject private ContactService contactService;
    @Inject private SoundService soundService;

    @Inject private GameController gameController; // "Because rules are meant to be broken."

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();
    private final PooledList<Block> blocks = PooledList.newList();
    private final PooledList<Minion> minions = PooledList.newList();
    private final PooledList<Bonus> bonuses = PooledList.newList();
    private boolean soloMode;
    private int penalty;
    private final int[] deaths = new int[Configuration.PLAYERS_AMOUNT];
    private final IntIntMap scores = new IntIntMap();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        gridService.createGrid();
        world = new World(GRAVITY, true);
        createGameBounds();
        createPlayers();
        createBlocks();
        world.setContactListener(contactService);
    }

    private void createBlocks() {
        gridService.getGrid().forEach(new CellConsumer() {
            @Override
            public boolean consume(final Grid grid, final int x, final int y, final float value) {
                if (gridService.isFull(x, y) && validate(x, y)) {
                    spawnBlock(-(Box2DUtil.WIDTH / 2f) + x * GridService.CELL_SIZE,
                            -(Box2DUtil.HEIGHT / 2f) + y * GridService.CELL_SIZE);
                }
                return CONTINUE;
            }
        });
    }

    private Block spawnBlock(final float x, final float y) {
        final Block block = new Block(Box2DService.this, BlockType.getRandom());
        final Body body = block.getBody();
        body.setTransform(x, y, 0f);
        blocks.add(block);
        return block;
    }

    /** @return service which allows to play sounds. */
    public SoundService getSoundService() {
        return soundService;
    }

    private void createPlayers() {
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                final Player player = new Player(this, index, control, playerService.getSpriteType(index));
                players.add(player);
                control.reset(player);
                spawnMinions(player);
                if (control.isHumanControlled()) {
                    player.setHealth(100f - penalty - deaths[player.getId()]);
                    if (!scores.containsKey(index)) {
                        scores.put(index, 0);
                    }
                } else {
                    player.setHealth(150f);
                }
            }
        }
    }

    /** @param penalty will be subtracted from starting minions amount and health amount. */
    public void addPenalty(final int penalty) {
        this.penalty += penalty;
    }

    /** @param playerId has just won. */
    public void addPoint(final int playerId) {
        scores.getAndIncrement(playerId, 0, 1);
    }

    /** @param playerId ID of the player.
     * @return current amount of points or 0. */
    public int getPoints(final int playerId) {
        return scores.get(playerId, 0);
    }

    /** No penalty will be applied to players, scores will be cleared. */
    public void reset() {
        penalty = 0;
        scores.clear();
        for (int index = 0; index < deaths.length; index++) {
            deaths[index] = 0;
        }
    }

    private void spawnMinions(final Player player) {
        float playerX = player.getX();
        float playerY = player.getY();
        if (playerX > 0f) {
            playerX -= 10f; // X offset to prevent from going out of the bounds.
        }
        if (playerY > 0f) {
            playerY -= 8f; // Y offset to prevent from going out of the bounds.
        }
        final int minionsAmount = player.getControl().isHumanControlled()
                ? MINIONS_AMOUNT - penalty - deaths[player.getId()] : MINIONS_AMOUNT;
        for (int index = 0; index < minionsAmount; index++) {
            final float x = index % MINION_ROW_SIZE;
            final float y = index / MINION_ROW_SIZE;
            final float posX = x * 1.1f + playerX;
            final float posY = y * 1.1f + playerY;
            addMinion(player, posX, posY);
        }
    }

    /** @param player owner of the minion.
     * @param x starting X position.
     * @param y starting Y position.
     * @return minion entity. */
    public Minion addMinion(final Player player, final float x, final float y) {
        final Minion minion = new Minion(this, player);
        minion.getBody().setTransform(x, y, 0f);
        player.addMinion(minion);
        minions.add(minion);
        return minion;
    }

    /** @param player owner of the minion.
     * @param x starting X position.
     * @param y starting Y position. */
    public void spawnMinion(final Player player, final float x, final float y) {
        gameController.addMinion(addMinion(player, x, y));
    }

    /** @return list of all player minions. */
    public PooledList<Minion> getMinions() {
        return minions;
    }

    /** @param x cell X.
     * @param y cell Y.
     * @return true if this cell can be filled. */
    protected boolean validate(final int x, final int y) {
        if (x < BOUND || y < BOUND || GridService.WIDTH - x < BOUND || GridService.HEIGHT - y < BOUND) {
            return false;
        } else if (x < LIMIT && y < LIMIT) {
            return !controlsService.isActive(3);
        } else if (x < LIMIT && GridService.HEIGHT - y < LIMIT) {
            return !controlsService.isActive(0);
        } else if (GridService.WIDTH - x < LIMIT && y < LIMIT) {
            return !controlsService.isActive(1);
        } else if (GridService.WIDTH - x < LIMIT && GridService.HEIGHT - y < LIMIT) {
            return !controlsService.isActive(2);
        }
        return true;
    }

    private BoundsEntity createGameBounds() {
        return new BoundsEntity(this);
    }

    /** @param delta time passed since last update. Will be used to update Box2D world.
     * @return true if world was updated. */
    public boolean update(final float delta) {
        timeSinceUpdate += delta;
        boolean updated = false;
        while (timeSinceUpdate > STEP) {
            updated = true;
            timeSinceUpdate -= STEP;
            world.step(STEP, 8, 3);
            updatePlayers();
            updateBlocks(delta);
            updateBonuses(delta);
            updateMinions(delta);
        }
        return updated;
    }

    private void updateBonuses(final float delta) {
        for (final Bonus bonus : bonuses) {
            bonus.update(delta);
            if (bonus.isDestroyed()) {
                bonus.destroy();
                bonuses.remove();
            }
        }
    }

    private void updatePlayers() {
        for (int index = players.size - 1; index >= 0; index--) {
            final Player player = players.get(index);
            player.update(STEP);
            if (player.isDestroyed()) {
                deaths[player.getId()] = deaths[player.getId()] + 1;
                player.destroy();
                players.removeValue(player, true);
            }
        }
    }

    private void updateBlocks(final float delta) {
        for (final Block block : blocks) {
            block.update(delta);
            if (block.isDestroyed()) {
                spawnBonus(block);
                block.destroy();
                blocks.remove();
            }
        }
    }

    private void spawnBonus(final Block block) {
        if (MathUtils.randomBoolean(soloMode ? 0.666f : 0.333f)) {
            final Bonus bonus = new Bonus(this, BonusType.getRandom());
            bonus.getBody().setTransform(block.getBody().getPosition().x, block.getBody().getPosition().y, 0f);
            bonuses.add(bonus);
            gameController.addBonus(bonus);
        }
    }

    private void updateMinions(final float delta) {
        for (final Minion minion : minions) {
            minion.update(delta);
            if (minion.isDestroyed()) {
                minion.destroy();
                soundService.playRandomPunchSound();
                minions.remove();
            }
        }
    }

    /** @return list of current players. */
    public Array<Player> getPlayers() {
        return players;
    }

    /** @return all blocks currently displayed on the screen. */
    public PooledList<Block> getBlocks() {
        return blocks;
    }

    /** @param inputMultiplexer will listen to player input events. */
    public void initiateControls(final InputMultiplexer inputMultiplexer) {
        for (final Player player : players) {
            player.getControl().attachInputListener(inputMultiplexer);
        }
    }

    /** @param width new screen width.
     * @param height new screen height. */
    public void resize(final int width, final int height) {
        viewport.update(width, height);
    }

    /** @return direct reference to current Box2D world. Might be null. */
    public World getWorld() {
        return world;
    }

    /** @return viewport with game coordinates. */
    public Viewport getViewport() {
        return viewport;
    }

    @Destroy
    public void dispose() {
        players.clear();
        blocks.clear();
        minions.clear();
        bonuses.clear();
        soloMode = false;
        if (world != null) {
            world.dispose();
            world = null;
        }
    }

    /** Changes to second stage of the game. */
    public void setSoloMode() {
        soloMode = true;
        for (final Minion minion : minions) {
            minion.setDestroyed(true);
        }
        // Spawning additional obstacles:
        final boolean[] isTaken = new boolean[1];
        final QueryCallback callback = new QueryCallback() {
            @Override
            public boolean reportFixture(final Fixture fixture) {
                isTaken[0] = true;
                return false;
            }
        };
        final float width = Box2DUtil.WIDTH * 2f / 5f - Block.HALF_SIZE;
        final float height = Box2DUtil.HEIGHT * 2f / 5f - Block.HALF_SIZE;
        for (int index = 0; index < 15; index++) {
            final float x = MathUtils.random(-width, width);
            final float y = MathUtils.random(-height, height);
            isTaken[0] = false;
            world.QueryAABB(callback, x - Block.HALF_SIZE, y - Block.HALF_SIZE, x + Block.HALF_SIZE,
                    y + Block.HALF_SIZE);
            if (!isTaken[0]) {
                gameController.addBlock(spawnBlock(x, y));
            }
        }
    }

    /** @param effect type of spawned effect.
     * @param x Box2D position on X axis.
     * @param y Box2D position on Y axis. */
    public void spawnEffect(final EffectType effect, final float x, final float y) {
        gameController.spawnEffect(effect, x, y);
    }

    /** @return true if game is in the second stage. */
    public boolean isSoloMode() {
        return soloMode;
    }
}