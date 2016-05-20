package com.github.czyzby.bj2016.util;

import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** Contains Box2D constants.
 *
 * @author MJ */
public class Box2DUtil extends UtilitiesClass {
    private Box2DUtil() {
    }

    /** Player 1 minions core. */
    public static final short CAT_PLAYER0 = 1 << 0;
    /** Player 2 minions core. */
    public static final short CAT_PLAYER1 = 1 << 1;
    /** Player 3 minions core. */
    public static final short CAT_PLAYER2 = 1 << 2;
    /** Player 4 minions core. */
    public static final short CAT_PLAYER3 = 1 << 3;
    /** Players collide with each other. */
    public static final short CAT_PLAYERS = 1 << 4;
    /** Minions collide with each other. */
    public static final short CAT_MINION = 1 << 5;
}
