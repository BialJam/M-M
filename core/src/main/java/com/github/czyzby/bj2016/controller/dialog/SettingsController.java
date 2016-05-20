package com.github.czyzby.bj2016.controller.dialog;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.bj2016.service.FullscreenService;

/** This is a settings dialog, which can be shown in any view by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template. */
@ViewDialog(id = "settings", value = "ui/templates/dialogs/settings.lml", cacheInstance = true)
public class SettingsController implements ActionContainer {
    @Inject private FullscreenService fullscreenService;

    /** @return array of serialized display modes' names. */
    @LmlAction("displayModes")
    public Array<String> getDisplayModes() {
        final ObjectSet<String> alreadyAdded = GdxSets.newSet(); // Removes duplicates.
        final Array<String> displayModes = GdxArrays.newArray(); // Keeps display modes sorted.
        for (final DisplayMode mode : fullscreenService.getDisplayModes()) {
            final String modeName = fullscreenService.serialize(mode);
            if (alreadyAdded.contains(modeName)) {
                continue; // Same size already added.
            }
            displayModes.add(modeName);
            alreadyAdded.add(modeName);
        }
        return displayModes;
    }

    /** @param actor its ID must match name of a display mode. */
    @LmlAction("setFullscreen")
    public void setFullscreenMode(final Actor actor) {
        final String modeName = LmlUtilities.getActorId(actor);
        final DisplayMode mode = fullscreenService.deserialize(modeName);
        fullscreenService.setFullscreen(mode);
    }

    /** Attempts to return to the original window size. */
    @LmlAction("resetFullscreen")
    public void setWindowedMode() {
        fullscreenService.resetFullscreen();
    }
}