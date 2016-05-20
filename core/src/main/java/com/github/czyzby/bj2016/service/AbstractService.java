package com.github.czyzby.bj2016.service;

import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;

/** Utility base for services.
 *
 * @author MJ */
public class AbstractService {
    private final Logger logger = LoggerService.forClass(getClass());

    protected void log(final String message, final Object... args) {
        logger.info(message, args);
    }
}
