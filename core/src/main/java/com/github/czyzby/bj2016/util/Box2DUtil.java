package com.github.czyzby.bj2016.util;

import com.github.czyzby.bj2016.Root;

/** Contains Box2D constants.
 *
 * @author MJ */ // ARGHRGH, no time for public static final. Let's go with an interface.
public interface Box2DUtil {
    /** Pixel per unit ratio. */
    float PPU = 10f;
    /** Width of Box2D world. */
    float WIDTH = Root.WIDTH / Box2DUtil.PPU;
    /** Height of Box2D world. */
    float HEIGHT = Root.HEIGHT / Box2DUtil.PPU;

    /** Player 1 minions. */
    short CAT_PLAYER0 = 1 << 0;
    /** Player 2 minions. */
    short CAT_PLAYER1 = 1 << 1;
    /** Player 3 minions. */
    short CAT_PLAYER2 = 1 << 2;
    /** Player 4 minions. */
    short CAT_PLAYER3 = 1 << 3;
    /** Controllable players. */
    short CAT_PLAYERS = 1 << 4;
    /** Minions core body. */
    short CAT_MINION = 1 << 5;
    /** Scenery. */
    short CAT_BLOCK = 1 << 6;
    /** Game bounds. */
    short CAT_BOUNDS = 1 << 7;

    /** Players collide with others players and blocks. */
    short MASK_PLAYER = CAT_PLAYERS | CAT_BLOCK | CAT_BOUNDS;
    /** Minions collide with enemy minions and blocks */
    short MASK_MINION_P0 = CAT_PLAYER1 | CAT_PLAYER2 | CAT_PLAYER3 | CAT_BLOCK | CAT_BOUNDS;
    /** Minions collide with enemy minions and blocks */
    short MASK_MINION_P1 = CAT_PLAYER0 | CAT_PLAYER2 | CAT_PLAYER3 | CAT_BLOCK | CAT_BOUNDS;
    /** Minions collide with enemy minions and blocks */
    short MASK_MINION_P2 = CAT_PLAYER0 | CAT_PLAYER1 | CAT_PLAYER3 | CAT_BLOCK | CAT_BOUNDS;
    /** Minions collide with enemy minions and blocks */
    short MASK_MINION_P3 = CAT_PLAYER0 | CAT_PLAYER1 | CAT_PLAYER2 | CAT_BLOCK | CAT_BOUNDS;
    /** Minions core collide with other cores and blocks (although they should never touch blocks, but oh-well). */
    short MASK_MINION_CORE = CAT_MINION | CAT_BLOCK | CAT_BOUNDS;
    /** Blocks and bounds collide with all entities, but not with each other. */
    short MASK_BLOCK = CAT_MINION | CAT_PLAYER0 | CAT_PLAYER1 | CAT_PLAYER2 | CAT_PLAYER3 | CAT_PLAYERS;

    /** Speed of players (delta-dependent). */
    float PLAYER_SPEED = 17500f;
}
