package com.github.czyzby.bj2016.service;

import java.util.PriorityQueue;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.bj2016.entity.grid.Cell;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;

/** Manages underlying Noise4J map.
 *
 * @author MJ */
@Component
public class GridService {
    public static final int WIDTH = (int) (Box2DUtil.WIDTH / (48f / Box2DUtil.PPU));
    public static final int HEIGHT = (int) (Box2DUtil.HEIGHT / (48f / Box2DUtil.PPU));
    private final Grid grid = new Grid(WIDTH, HEIGHT);
    // A*:
    private final PriorityQueue<Cell> queue = new PriorityQueue<Cell>();
    private final IntSet visited = new IntSet();
    private final IntArray path = new IntArray();

    /** Uses cellular automata generator to fill map with random obstacles. */
    public void createGrid() {
        grid.set(0f);
        final CellularAutomataGenerator generator = new CellularAutomataGenerator();
        generator.setAliveChance(0.55f);
        generator.setIterationsAmount(2);
        generator.generate(grid);
        final float size = grid.getWidth() * grid.getHeight();
        final int count = getFullCellsAmount();
        if (count < size / 10f || count > size * 3f / 5f) {
            createGrid();
        }
    }

    private int getFullCellsAmount() {
        int count = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (isFull(x, y)) {
                    count++;
                }
            }
        }
        return count;
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

    /** @param fromX start cell X.
     * @param fromY start cell Y.
     * @param toX destination cell X.
     * @param toY destination cell Y.
     * @return path BACKWARDS. */
    public IntArray findPath(final int fromX, final int fromY, final int toX, final int toY) {
        queue.clear();
        visited.clear();
        path.clear();
        queue.offer(new Cell(fromX, fromY, getManhattanDistance(fromX, fromY, toX, toY)));
        while (!queue.isEmpty()) {
            final Cell cell = queue.poll();
            visited.add(cell.hashCode());
            final int x = cell.getX();
            final int y = cell.getY();
            if (x == toX && y == toY) {
                Cell current = cell;
                while (current != null) {
                    path.add(current.getX());
                    path.add(current.getY());
                    current = cell.getParent();
                }
                return path;
            }
            check(cell, x - 1, y, 10, toX, toY);
            check(cell, x - 1, y - 1, 14, toX, toY);
            check(cell, x - 1, y + 1, 14, toX, toY);
            check(cell, x, y - 1, 10, toX, toY);
            check(cell, x, y + 1, 10, toX, toY);
            check(cell, x + 1, y, 10, toX, toY);
            check(cell, x + 1, y - 1, 14, toX, toY);
            check(cell, x + 1, y + 1, 14, toX, toY);
        }
        return path;
    }

    private void check(final Cell parent, final int x, final int y, final int score, final int toX, final int toY) {
        if (x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight() || isFull(x, y)
                || visited.contains(hash(x, y))) {
            return;
        }
        queue.add(new Cell(parent, x, y, score + parent.getScore() + getManhattanDistance(x, y, toX, toY)));
    }

    private static int getManhattanDistance(final int fromX, final int fromY, final int toX, final int toY) {
        return (Math.abs(fromX - toX) + Math.abs(fromY - toY)) * 10;
    }

    /** @param x pos X
     * @param y pos Y.
     * @return cell hashcode. */
    public static int hash(final int x, final int y) {
        return x + 877 * y;
    }
}
