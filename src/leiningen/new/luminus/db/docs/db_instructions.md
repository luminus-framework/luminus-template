<div class="bs-callout bs-callout-danger">

### Database Configuration is Required

Before continuing please follow the steps below to configure your database connection and run the necessary migrations.

* Create the database for your application.
* Update the connection parameters for the database in the `<<name>>.db.core` namespace of your application.
* Update the connection URL in the `project.clj` under the `:ragtime` key with your database name and login.
* Run `lein ragtime migrate` in the root of the project to create the tables.
* Restart the application.

</div>