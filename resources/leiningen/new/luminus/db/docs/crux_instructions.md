#### Crux configuration

* Crux can be configured in the `dev-config.edn` and `test-config.edn` files. By default you will have a local Crux node in the
  dev env and an in memory node in the test env.
* Let `mount` know to start the Crux node by `require`-ing `<<project-ns>>.db.core` in some other namespace.
* Restart the application.