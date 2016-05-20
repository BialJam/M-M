package com.github.czyzby.bj2016.configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;

/** Thanks to the Component annotation, this class will be automatically found and processed.
 *
 * This is a utility class that configures application settings. */
@Component
public class Configuration {
    /** Name of the application's preferences file. */
    public static final String PREFERENCES = "BJ2016";
    /** Max players amount. */
    public static final int PLAYERS_AMOUNT = 4;
    /** Path to global macro file. */
    @LmlMacro private final String globalMacro = "ui/templates/macros/global.lml";
    /** Path to the internationalization bundle. */
    @I18nBundle private final String bundlePath = "i18n/bundle";
    /** Enabling VisUI usage. */
    @LmlParserSyntax private final LmlSyntax syntax = new VisLmlSyntax();

    /** These sound-related fields allow MusicService to store settings in preferences file. Sound preferences will be
     * automatically saved when the application closes and restored the next time it's turned on. Sound-related methods
     * methods will be automatically added to LML templates - see settings.lml template. */
    @SoundVolume(preferences = PREFERENCES) private final String soundVolume = "soundVolume";
    @SoundEnabled(preferences = PREFERENCES) private final String soundEnabled = "soundOn";
    @MusicVolume(preferences = PREFERENCES) private final String musicVolume = "musicVolume";
    @MusicEnabled(preferences = PREFERENCES) private final String musicEnabledPreference = "musicOn";

    /** These i18n-related fields will allow LocaleService to save game's locale in preferences file. Locale changing
     * actions will be automatically added to LML templates - see settings.lml template. */
    @I18nLocale(propertiesPath = PREFERENCES, defaultLocale = "en") private final String localePreference = "locale";
    @AvailableLocales private final String[] availableLocales = new String[] { "en", "pl" };

    /** Setting the default Preferences object path. */
    @Preference private final String preferencesPath = PREFERENCES;

    /** Thanks to the Initiate annotation, this method will be automatically invoked during context building. All
     * method's parameters will be injected with values from the context.
     *
     * @param skinService contains GUI skin. */
    @Initiate
    public void initiateConfiguration(final SkinService skinService, final InterfaceService interfaceService) {
        // Loading default VisUI skin with the selected scale:
        VisUI.load("ui/skin.json");
        // Registering VisUI skin with "default" name - this skin will be the default one for all LML widgets:
        skinService.addSkin("default", VisUI.getSkin());
        // Methods not annotated with @LmlAction will not be available in LML views.
        Lml.EXTRACT_UNANNOTATED_METHODS = false;
        // Changing title on bundles (re)load.
        interfaceService.setActionOnBundlesReload(new Runnable() {
            @Override
            public void run() {
                Gdx.graphics.setTitle(interfaceService.getParser().getData().getDefaultI18nBundle().get("title")
                        + " - BialJam, M&M 2016");
            }
        });
        // Changing the default resizer - centering actors on resize.
        InterfaceService.DEFAULT_VIEW_RESIZER = new ViewResizer() {
            @Override
            public void resize(final Stage stage, final int width, final int height) {
                stage.getViewport().update(width, height, true);
                for (final Actor actor : stage.getActors()) {
                    Actors.centerActor(actor);
                }
            }
        };
    }
}