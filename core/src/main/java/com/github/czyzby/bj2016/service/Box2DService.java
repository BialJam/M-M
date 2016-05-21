package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.entity.BoundsEntity;
import com.github.czyzby.bj2016.entity.Minion;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.BlockType;
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

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();
    private final PooledList<Block> blocks = PooledList.newList();
    private final PooledList<Minion> minions = PooledList.newList();
    private boolean soloMode;
    private int penalty;
    private final int[] deaths = new int[Configuration.PLAYERS_AMOUNT];

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        gridService.createGrid();
        world = new World(GRAVITY, true);
        createGameBounds();
        createPlayers();
        final Vector2 position = new Vector2();
        gridService.getGrid().forEach(new CellConsumer() {
            @Override
            public boolean consume(final Grid grid, final int x, final int y, final float value) {
                if (gridService.isFull(x, y) && validate(x, y)) {
                    final Block block = new Block(Box2DService.this, BlockType.getRandom());
                    final Body body = block.getBody();
                    position.x = -(Box2DUtil.WIDTH / 2f) + x * GridService.CELL_SIZE;
                    position.y = -(Box2DUtil.HEIGHT / 2f) + y * GridService.CELL_SIZE;
                    body.setTransform(position, 0f);
                    blocks.add(block);
                }
                return CONTINUE;
            }
        });
        world.setContactListener(contactService);
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
                }
            }
        }
    }

    /** @param penalty will be subtracted from starting minions amount and health amount. */
    public void addPenalty(final int penalty) {
        this.penalty += penalty;
    }

    /** No penalty will be applied to players. */
    public void resetPenalty() {
        penalty = 0;
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
            final Minion minion = new Minion(this, player, gridService);
            minion.getBody().setTransform(x * 1.1f + playerX, y * 1.1f + playerY, 0f);
            player.addMinion();
            minions.add(minion);
        }
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
            updateMinions(delta);
        }
        return updated;
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
                block.destroy();
                // TODO Sound?
                blocks.remove();
            }
        }
    }

    private void updateMinions(final float delta) {
        for (final Minion minion : minions) {
            minion.update(delta);
            if (minion.isDestroyed()) {
                minion.destroy();
                // TODO SOUND?
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
    }

    /** @return true if game is in the second stage. */
    public boolean isSoloMode() {
        return soloMode;
    }
}