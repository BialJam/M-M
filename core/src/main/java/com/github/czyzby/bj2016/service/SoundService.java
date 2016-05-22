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
    @Asset(value = { "sounds/boom0.ogg", "sounds/boom1.ogg", "sounds/boom2.ogg", "sounds/boom3.ogg" },
            type = Sound.class) private Array<Sound> crushSounds;
    @Asset(value = { "sounds/bonus0.ogg", "sounds/bonus1.ogg" }, type = Sound.class) private Array<Sound> bonusSounds;
    @Asset(value = { "sounds/jump0.ogg" }, type = Sound.class) private Array<Sound> jumpSounds;
    @Asset(value = { "sounds/female0.ogg", "sounds/female1.ogg", "sounds/female2.ogg" },
            type = Sound.class) private Array<Sound> femaleScreams;
    @Asset(value = { "sounds/male0.ogg", "sounds/male1.ogg", "sounds/male2.ogg", "sounds/male3.ogg",
            "sounds/male4.ogg" }, type = Sound.class) private Array<Sound> maleScreams;

    /** Starts a random punch sound. */
    public void playRandomPunchSound() {
        musicService.play(punchSounds.random());
    }

    /** Starts a random bonus sound. */
    public void playRandomBonusSound() {
        musicService.play(bonusSounds.random());
    }

    /** Starts a random female death sound. */
    public void playRandomFemaleScream() {
        musicService.play(femaleScreams.random());
    }

    /** Starts a random male death sound. */
    public void playRandomMaleScream() {
        musicService.play(maleScreams.random());
    }

    /** Plays a random jump sound. */
    public void playRandomJumpSound() {
        musicService.play(jumpSounds.random());
    }

    /** Starts a random explosion sound. */
    public void playRandomCrushSound() {
        musicService.play(crushSounds.random());
    }
}
