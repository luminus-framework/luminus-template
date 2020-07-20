<h2 class="alert alert-success">Congratulations, your <a class="alert-link" href="http://luminusweb.net">Luminus</a> site is ready!</h2>

This page will help guide you through the first steps of building your site.

#### Why are you seeing this page?

The `home-routes` handler in the `<<project-ns>>.routes.home` namespace
defines the route that invokes the `home-page` function whenever an HTTP
request is made to the `/` URI using the `GET` method.
<% if cljs %>
```
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
```

The `home-page` function will in turn call the `<<project-ns>>.layout/render` function
to render the HTML content:

```
(defn home-page [request]
  (layout/render request "home.html"))
```

The page contains a link to the compiled ClojureScript found in the `target/cljsbuild/public` folder:

```
{% script "/js/app.js" %}
```

The rest of this page is rendered by ClojureScript found in the `src/cljs/<<sanitized>>/core.cljs` file.

<% else %>
```
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])
```

The `home-routes` are wrapped with two middleware functions. The first enables CSRF protection.
The second takes care of serializing and deserializing various encoding formats, such as JSON.

The `home-page` function will in turn call the `<<project-ns>>.layout/render` function
to render the HTML content:

```
(defn home-page [request]
  (layout/render
    request
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))
```

The `render` function will render the `home.html` template found in the `resources/templates`
folder using a parameter map containing the `:docs` key. This key points to the
contents of the `resources/docs/docs.md` file containing these instructions.


The HTML templates are written using [Selmer](https://github.com/yogthos/Selmer) templating engine.


```
<div class="row">
  <div class="col-sm-12">
    {{docs|markdown}}
  </div>
</div>
```

<a class="btn btn-primary" href="http://www.luminusweb.net/docs/html_templating.md">learn more about HTML templating »</a>

<% endif %>

#### Organizing the routes

The routes are aggregated and wrapped with middleware in the `<<project-ns>>.handler` namespace:

```
(mount/defstate app
  :start
  (middleware/wrap-base
    (ring/ring-handler
      (ring/router
        [(home-routes)])
      (ring/routes
        (ring/create-resource-handler
          {:path "/"})
        (wrap-content-type
          (wrap-webjars (constantly nil)))
        (ring/create-default-handler
          {:not-found
           (constantly (error-page {:status 404, :title "404 - Page not found"}))
           :method-not-allowed
           (constantly (error-page {:status 405, :title "405 - Not allowed"}))
           :not-acceptable
           (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))))
```

The `app` definition groups all the routes in the application into a single handler.
A default route group is added to handle the `404`, `405`, and `406` errors.

<a class="btn btn-primary" href="https://metosin.github.io/reitit/basics">learn more about routing »</a>

#### Managing your middleware

Request middleware functions are located under the `<<name>>.middleware` namespace.

This namespace is reserved for any custom middleware for the application. Some default middleware is
already defined here. The middleware is assembled in the `wrap-base` function.

Middleware used for development is placed in the `<<project-ns>>.dev-middleware` namespace found in
the `env/dev/clj/` source path.

<a class="btn btn-primary" href="http://www.luminusweb.net/docs/middleware.md">learn more about middleware »</a>

<<db-docs>>
<<sassc-docs>>

#### Need some help?

Visit the [official documentation](http://www.luminusweb.net/docs) for examples
on how to accomplish common tasks with Luminus. The `#luminus` channel on the [Clojurians Slack](http://clojurians.net/) and [Google Group](https://groups.google.com/forum/#!forum/luminusweb) are both great places to seek help and discuss projects with other users.
