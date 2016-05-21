package com.github.czyzby.bj2016.service;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.stereotype.Asset;

/** Manages application's sounds.
 *
 * @author MJ */
@Component
public class SoundService {
    @Inject private MusicService musicService;
    @Asset(value = { "sounds/punch0.ogg", "sounds/punch1.ogg", "sounds/punch2.ogg", "sounds/punch3.ogg",
            "sounds/punch4.ogg" }, type = Sound.class) private Array<Sound> punchSounds;

    /** Starts a random punch sound. */
    public void playRandomPunchSound() {
        musicService.play(punchSounds.random());
    }
}
