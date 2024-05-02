# openfga4intellij


This is an unofficial IntelliJ plugin for [OpenFGA](https://openfga.dev/) support.


## Features

* DSL syntax support (associated with `.fga` and `.openfga` file extensions)
* DSL syntax injection for YAML store files (associated with `.fga.yaml` and `.openfga.yaml` file extensions)
* Authorization model dsl file template
* Authorization model dsl live templates
* Generate json file from DSL (requires [OpenFGA CLI](https://github.com/openfga/cli) to be installed)
* Configure servers in OpenFGA tool window

## Development

### Build & test

```./gradlew build```

### Create a local plugin artifact

```./gradlew buildPlugin```

[Instructions for installation from disk](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk)

### Publish

TODO: https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html#deploying-a-plugin-with-gradle