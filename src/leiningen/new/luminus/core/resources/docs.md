<<db-docs>>
<<sassc-docs>>
### Managing Your Middleware

Request middleware functions are located under the `<<name>>.middleware` namespace.

This namespace is reserved for any custom middleware for the application. Some default middleware is
already defined here.

The two function for organizing the middleware are called `wrap-dev` and `wrap-base`.

Any middleware that you only wish to run in development mode should be added inside the `wrap-dev` function.
This middleware will only be invoked when the environment contains the `:dev` key with a truthy value.

### Here are some links to get started

1. [HTML templating](http://www.luminusweb.net/docs/html_templating.md)
2. [Accessing the database](http://www.luminusweb.net/docs/database.md)
3. [Serving static resources](http://www.luminusweb.net/docs/static_resources.md)
4. [Setting response types](http://www.luminusweb.net/docs/responses.md)
5. [Defining routes](http://www.luminusweb.net/docs/routes.md)
6. [Adding middleware](http://www.luminusweb.net/docs/middleware.md)
7. [Sessions and cookies](http://www.luminusweb.net/docs/sessions_cookies.md)
8. [Security](http://www.luminusweb.net/docs/security.md)
9. [Deploying the application](http://www.luminusweb.net/docs/deployment.md)
