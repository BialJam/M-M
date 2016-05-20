package com.github.czyzby.bj2016.controller;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.bj2016.util.Box2DUtil;
import com.github.czyzby.noise4j.map.Grid;

/** Manages underlying Noise4J map.
 *
 * @author MJ */
@Component
public class GridController {
    private final Grid grid = new Grid((int) (Box2DUtil.WIDTH * 2f), (int) (Box2DUtil.HEIGHT * 2f));

    public void createGrid() {
        grid.set(0f);
        // TODO
    }
}
