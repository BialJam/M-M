package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.Player;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Manages 2D physics engine. */
@Component
public class Box2DService extends AbstractService {
    private static final Vector2 GRAVITY = new Vector2(0f, 0f); // Box2D world gravity vector.
    private static final float STEP = 1f / 30f; // Length of a single Box2D step.
    @Inject private ControlsService controlsService;
    @Inject private PlayerService playerService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(Box2DUtil.WIDTH, Box2DUtil.HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        world = new World(GRAVITY, true);
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                players.add(new Player(this, index, control, playerService.getSpriteType(index)));
            }
        }
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
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}