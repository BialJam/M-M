# BJ2016

A [LibGDX](http://libgdx.badlogicgames.com/) project generated with [gdx-setup](https://github.com/czyzby/gdx-setup).

Project template included launchers with [Autumn](https://github.com/czyzby/gdx-lml/tree/master/autumn) class scanners and an [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) application showing usage of Box2D and Controllers LibGDX extensions. A simple GUI consisting of several screens and dialogs was provided, including a settings view that allows the players to choose their controls.

## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies. Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands. Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `desktop:jar`: builds application's runnable jar, which can be found at `desktop/build/libs`.
- `desktop:run`: starts the application.
- `eclipse`: generates Eclipse project data.
- `gwt:dist`: compiles GWT sources. The compiled application can be found at `gwt/build/dist`: you can use any HTTP server to deploy it.
- `gwt:superDev`: compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/gwt](http://localhost:8080/gwt). Use only during development.
- `idea`: generates IntelliJ project data.
- `pack`: packs GUI assets from `raw/ui`. Saves the atlas file at `assets/ui`.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.