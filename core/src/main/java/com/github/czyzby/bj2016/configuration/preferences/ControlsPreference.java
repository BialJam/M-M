package com.github.czyzby.bj2016.configuration.preferences;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
import com.github.czyzby.autumn.mvc.stereotype.preference.Property;
import com.github.czyzby.kiwi.util.gdx.GdxUtilities;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.bj2016.configuration.Configuration;
import com.github.czyzby.bj2016.service.controls.ControlType;
import com.github.czyzby.bj2016.service.controls.impl.KeyboardControl;
import com.github.czyzby.bj2016.service.controls.impl.TouchControl;

/** Allows to save controls in preferences. */
@Property("Controls")
public class ControlsPreference extends AbstractPreference<Array<ControlsData>> {
    private final Json json = new Json();

    @Override
    public Array<ControlsData> getDefault() {
        final Array<ControlsData> controls = GdxArrays.newArray();
        // First player defaults to touch (on mobile) or keyboard (on desktop) controls.
        controls.add(GdxUtilities.isMobile() ? new TouchControl().toData() : new KeyboardControl().toData());
        for (int index = 1; index < Configuration.PLAYERS_AMOUNT; index++) {
            // Other players are simply inactive:
            controls.add(new ControlsData(ControlType.INACTIVE));
        }
        return controls;
    }

    @Override
    public Array<ControlsData> extractFromActor(final Actor actor) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Array<ControlsData> convert(final String rawPreference) {
        return json.fromJson(Array.class, ControlsData.class, Base64Coder.decodeString(rawPreference));
    }

    @Override
    protected String serialize(final Array<ControlsData> preference) {
        return Base64Coder.encodeString(json.toJson(preference, Array.class, ControlsData.class));
    }
}