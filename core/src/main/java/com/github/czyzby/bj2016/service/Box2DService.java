package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.Block;
import com.github.czyzby.bj2016.entity.BoundsEntity;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.entity.sprite.BlockType;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.Grid.CellConsumer;

/** Manages 2D physics engine. */
@Component
public class Box2DService extends AbstractService {
    private static final Vector2 GRAVITY = new Vector2(0f, 0f); // Box2D world gravity vector.
    private static final float STEP = 1f / 30f; // Length of a single Box2D step.
    private static final int LIMIT = 5;
    @Inject private ControlsService controlsService;
    @Inject private PlayerService playerService;
    @Inject private GridService gridService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();
    private final ObjectSet<Block> blocks = GdxSets.newSet();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        gridService.createGrid();
        world = new World(GRAVITY, true);
        createGameBounds();
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                players.add(new Player(this, index, control, playerService.getSpriteType(index)));
            }
        }
        final Vector2 position = new Vector2();
        gridService.getGrid().forEach(new CellConsumer() {
            @Override
            public boolean consume(final Grid grid, final int x, final int y, final float value) {
                if (gridService.isFull(x, y) && validate(x, y)) {
                    final Block block = new Block(Box2DService.this, BlockType.getRandom());
                    final Body body = block.getBody();
                    position.x = -(Box2DUtil.WIDTH / 2f) + x * 48f / Box2DUtil.PPU;
                    position.y = -(Box2DUtil.HEIGHT / 2f) + y * 48f / Box2DUtil.PPU;
                    body.setTransform(position, 0f);
                    blocks.add(block);
                }
                return CONTINUE;
            }
        });
    }

    /** @param x cell X.
     * @param y cell Y.
     * @return true if this cell can be filled. */
    protected boolean validate(final int x, final int y) {
        if (x < LIMIT && y < LIMIT) {
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
            for (final Player player : players) {
                player.update(STEP);
            }
            // TODO update entities, destroy
        }
        return updated;
    }

    /** @return list of current players. */
    public Array<Player> getPlayers() {
        return players;
    }

    /** @return all blocks currently displayed on the screen. */
    public ObjectSet<Block> getBlocks() {
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
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}