package com.github.czyzby.bj2016.entity.grid;

import com.github.czyzby.bj2016.service.GridService;

/** A* data container.
 *
 * @author MJ */
public class Cell implements Comparable<Cell> {
    private final Cell parent;
    private final int x, y, score;

    public Cell(final int x, final int y, final int score) {
        this.x = x;
        this.y = y;
        this.score = score;
        parent = null;
    }

    public Cell(final Cell parent, final int x, final int y, final int score) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScore() {
        return score;
    }

    public Cell getParent() {
        return parent;
    }

    @Override
    public int compareTo(final Cell o) {
        return score - o.score;
    }

    @Override
    public int hashCode() {
        return GridService.hash(x, y);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Cell && ((Cell) obj).x == x && ((Cell) obj).y == y;
    }
}
