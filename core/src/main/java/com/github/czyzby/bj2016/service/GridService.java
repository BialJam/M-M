package com.github.czyzby.bj2016.service;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;

/** Manages underlying Noise4J map.
 *
 * @author MJ */
@Component
public class GridService {
    private final Grid grid = new Grid((int) (Box2DUtil.WIDTH / (48f / Box2DUtil.PPU)),
            (int) (Box2DUtil.HEIGHT / (48f / Box2DUtil.PPU)));

    /** Uses cellular automata generator to fill map with random obstacles. */
    public void createGrid() {
        grid.set(0f);
        final CellularAutomataGenerator generator = new CellularAutomataGenerator();
        generator.setAliveChance(0.6f);
        generator.generate(grid);
    }

    /** @return current state of grid.
     * @see #createGrid() */
    public Grid getGrid() {
        return grid;
    }

    /** @param x position on X axis.
     * @param y position on Y axis.
     * @return true if the selected cell is considered filled. */
    public boolean isFull(final int x, final int y) {
        if (x < 0 || x > grid.getWidth() || y < 0 || y > grid.getHeight()) {
            return true;
        }
        return grid.get(x, y) > 0f;
    }

    /** Clears selected cell.
     *
     * @param x position on X axis.
     * @param y position on Y axis. */
    public void setFull(final int x, final int y) {
        grid.set(x, y, 0f);
    }
}
