This page will help guide you through the first steps of building your site.

#### Why are you seeing this page?

The `home-routes` handler in the `<<project-ns>>.routes.home` namespace
defines the route that invokes the `home-page` function whenever an HTTP
request is made to the `/` URI using the `GET` method.
<% if cljs %>
```
(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))
```

The `home-page` function will in turn call the `<<project-ns>>.layout/render` function
to render the HTML content:

```
(defn home-page []
  (layout/render "home.html"))
```

The page contains a link to the compiled ClojureScript found in the `target/cljsbuild/public` folder:

```
{% script "/js/app.js" %}
```

The rest of this page is rendered by ClojureScript found in the `src/cljs/<<sanitized>>/core.cljs` file.

<% else %>
```
(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
```

The `home-page` function will in turn call the `<<project-ns>>.layout/render` function
to render the HTML content:

```
(defn home-page []
  (layout/render
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

The `home-page` function 
<% endif %>

#### Managing your middleware

Request middleware functions are located under the `<<name>>.middleware` namespace.

This namespace is reserved for any custom middleware for the application. Some default middleware is
already defined here. The middleware is assembled in the `wrap-base` function.

Middleware used for development is placed in the `<<project-ns>>.dev-middleware` namespace found in
the `env/dev/clj/` source path.

<<db-docs>>
<<sassc-docs>>

#### Need some help?

Visit the [official documentation](http://www.luminusweb.net/docs) for examples
on how to accomplish common tasks with Luminus. The `#luminus` channel on the [Clojurians Slack](http://clojurians.net/) and [Google Group](https://groups.google.com/forum/#!forum/luminusweb) are both great places to seek help and discuss projects with other users.
