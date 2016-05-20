package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.bj2016.Root;

/** Handles fullscreen-related operations. */
@Component
public class FullscreenService {
    /** @return supported fullscreen display modes. Utility method. */
    public DisplayMode[] getDisplayModes() {
        return Gdx.graphics.getDisplayModes();
    }

    /** @param displayMode will be converted to string.
     * @return passed mode converted to a string. */
    public String serialize(final DisplayMode displayMode) {
        return displayMode.width + "x" + displayMode.height;
    }

    /** @param displayMode serialized display mode. See {@link #serialize(DisplayMode)}.
     * @return mode instance or null with selected size is not supported. */
    public DisplayMode deserialize(final String displayMode) {
        final String[] sizes = Strings.split(displayMode, 'x');
        final int width = Integer.parseInt(sizes[0]);
        final int height = Integer.parseInt(sizes[1]);
        for (final DisplayMode mode : Gdx.graphics.getDisplayModes()) {
            if (mode.width == width && mode.height == height) {
                return mode;
            }
        }
        return null;
    }

    /** @param displayMode must support fullscreen mode. */
    public void setFullscreen(final DisplayMode displayMode) {
        if (Gdx.graphics.setFullscreenMode(displayMode)) {
            // Explicitly trying to resize the application listener to fully support all platforms:
            Gdx.app.getApplicationListener().resize(displayMode.width, displayMode.height);
        }
    }

    /** Tries to set windowed mode with initial screen size. */
    public void resetFullscreen() {
        if (Gdx.graphics.setWindowedMode(Root.WIDTH, Root.HEIGHT)) {
            // Explicitly trying to resize the application listener to fully support all platforms:
            Gdx.app.getApplicationListener().resize(Root.WIDTH, Root.HEIGHT);
        }
    }
}