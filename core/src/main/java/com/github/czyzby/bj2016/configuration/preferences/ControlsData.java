package com.github.czyzby.bj2016.configuration.preferences;

import com.github.czyzby.bj2016.service.controls.ControlType;

/** JSON-encoded class. Uses public fields to support LibGDX JSON utilities. */
public class ControlsData {
    /** Up movement shortcut. */
    public int up;
    /** Down movement shortcut. */
    public int down;
    /** Left movement shortcut. */
    public int left;
    /** Right movement shortcut. */
    public int right;
    /** Type of controls */
    public ControlType type;
    /** Additional data. Might be used for device ID. */
    public int index;
    /** Optional settings. Might not be supported by every controller. */
    public boolean invertX, invertY, invertXY;

    public ControlsData() {
    }

    public ControlsData(final ControlType type) {
        this.type = type;
    }
}