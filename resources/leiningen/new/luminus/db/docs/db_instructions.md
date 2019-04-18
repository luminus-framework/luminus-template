<div class="bs-callout bs-callout-danger">

#### Database configuration is required

If you haven't already, then please follow the steps below to configure your database connection and run the necessary migrations.

* Create the database for your application.
* Update the connection URL in the `dev-config.edn` and `test-config.edn` files with your database name and login credentials.
* Run `lein run migrate` in the root of the project to create the tables.
* Let `mount` know to start the database connection by `require`-ing `<<project-ns>>.db.core` in some other namespace.
* Restart the application.

<a class="btn btn-primary" href="http://www.luminusweb.net/docs/database.md">learn more about database access »</a>

</div>
