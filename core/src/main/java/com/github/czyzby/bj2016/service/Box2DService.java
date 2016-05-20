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
import com.github.czyzby.bj2016.Root;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.entity.SpriteType;
import com.github.czyzby.bj2016.service.controls.Control;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Manages 2D physics engine. */
@Component
public class Box2DService {
    private static final Vector2 GRAVITY = new Vector2(0f, -9.81f); // Box2D world gravity vector.
    private static final float STEP = 1f / 30f; // Length of a single Box2D step.
    private static final float WIDTH = Root.WIDTH / 10f; // Width of Box2D world.
    private static final float HEIGHT = Root.HEIGHT / 10f; // Height of Box2D world.
    @Inject private ControlsService controlsService;
    @Inject private PlayerService playerService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(WIDTH, HEIGHT);
    private final Array<Control> activeControls = GdxArrays.newArray();
    private final Array<SpriteType> spriteTypes = GdxArrays.newArray();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        world = new World(GRAVITY, true);
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                activeControls.add(control);
                spriteTypes.add(playerService.getSpriteType(index));
            }
        }
    }

    /** @param delta time passed since last update. Will be used to update Box2D world. */
    public void update(final float delta) {
        timeSinceUpdate += delta;
        while (timeSinceUpdate > STEP) {
            timeSinceUpdate -= STEP;
            world.step(STEP, 8, 3);
            // TODO Update entities.
        }
    }

    /** @param inputMultiplexer will listen to player input events. */
    public void initiateControls(final InputMultiplexer inputMultiplexer) {
        for (final Control control : activeControls) {
            control.attachInputListener(inputMultiplexer);
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
        activeControls.clear();
        spriteTypes.clear();
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}