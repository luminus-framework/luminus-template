<div class="bs-callout bs-callout-danger">

#### MongoDB configuration is required

If you haven't already, then please follow the steps below to configure your MongoDB connection.

* Ensure that MongoDB is up and running.
* Set the connection parameters in the `profiles.clj` file.
* Let `mount` know to start the database connection by `require`-ing `<<project-ns>>.core` in some other namespace.
* Restart the application.

</div>
