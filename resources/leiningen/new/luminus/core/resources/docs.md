<<db-docs>>
<<sassc-docs>>
### Managing Your Middleware

Request middleware functions are located under the `<<name>>.middleware` namespace.

This namespace is reserved for any custom middleware for the application. Some default middleware is
already defined here. The middleware is assembled in the `wrap-base` function.

Middleware used for development is placed in the `<<project-ns>>.dev-middleware` namespace found in
the `env/dev/clj/` source path.

### Here are some links to get started

1. [HTML templating](http://www.luminusweb.net/docs/html_templating.md)
2. [Accessing the database](http://www.luminusweb.net/docs/database.md)
3. [Setting response types](http://www.luminusweb.net/docs/responses.md)
4. [Defining routes](http://www.luminusweb.net/docs/routes.md)
5. [Adding middleware](http://www.luminusweb.net/docs/middleware.md)
6. [Sessions and cookies](http://www.luminusweb.net/docs/sessions_cookies.md)
7. [Security](http://www.luminusweb.net/docs/security.md)
8. [Deploying the application](http://www.luminusweb.net/docs/deployment.md)
