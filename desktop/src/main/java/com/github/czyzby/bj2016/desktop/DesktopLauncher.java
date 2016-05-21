package com.github.czyzby.bj2016.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.bj2016.Root;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
    public static void main(final String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {
        return new LwjglApplication(new AutumnApplication(new DesktopClassScanner(), Root.class),
                getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "BialJam, M&M 2016";
        configuration.width = Root.WIDTH;
        configuration.height = Root.HEIGHT;
        configuration.resizable = false; // Meh.
        return configuration;
    }
}