
# Changelog


## next

* list server stores

**migration** :

If you already have configured servers with a previous version of this plugin you need to edit the `openfga-servers.xml` configuration file.

This file is located in the `options` folder of your JetBrains IDE application data folder.

* on windows it should be `%AppData%\JetBrains\<YourIDE>\options\openfga-servers.xml`
* on linux it could be `~/.var/app/<your ide related subfolders>/options/openfga-servers.xml`

Open the `openfga-servers.xml` file and rename all the `Server` elements to `ServerState`.

## v0.2.2

* validate openfga cli configuration
* format generated authorization model json file


## v0.2.1

* only display OpenFGA actions group on OpenFGA files


## v0.2.0

* add server oidc configuration support
* add "test connection" in server dialog to check the configuration (checking a list stores api call doesn't fail)
* improve error handling for authorization model json generation (also the action is disabled if required cli is not configured)


## v0.1.0

* DSL syntax support (associated with `.fga` and `.openfga` file extensions)
* Authorization model dsl file template
* Authorization model dsl live templates
* Generate json file from DSL (requires [OpenFGA CLI](https://github.com/openfga/cli) to be installed)
* Configure servers in OpenFGA tool window