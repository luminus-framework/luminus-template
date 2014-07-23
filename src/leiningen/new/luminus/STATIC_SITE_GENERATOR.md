* Luminous as a static site generator

The Luminus framework can be used as a powerful static site generator
by simulating browser requests to the compojure routes you've defined
in your luminous webapp and then saving the results as html files.

The static generator reads the `static-site` datastructure defined
inside static.cljs in order to know which html files to create.

     (def static-site
       {:routes
        [["index.html" :get "/index.html"]
         ["about.html" :get "/about.html"]]
        :css {:exclude #"bootstrap"}})

Define your static site by adding and removing entries. For example,
the first entry in the example above tells the static site generator
to simulat a HTTP GET request to the root path of your site ("/") and
save the results as index.html.

* Usage

Use the `+static` lein profile hint to create a new luminus project.
This provides the extra code needed to generate static sites. For
example:

    lein new luminus myapp +static

To generate a static site, use `lein run` which will execute the
`-main` method inside static.clj and create a static site based on the
`static-site` config found in static.clj. For example, to generate a
static site under the "resources/public/static" directory in a new
luminus project, run the following:

    lein run -- --servlet-context "/site" --dest-path "resources/public/site"

This will produce a complete static representation of the luminus site
under "resources/public/site" with context path set to "/site". You
can then check that all the links work by visiting:

    http://localhost:3000/site/index.html
    
* Defaults and configuration

By default the static site will be created under a directory named
"static" under the root directory of your luminus project. This can be
controlled via the `--dest-path` cmd line option.

By default, all links found inside css and html will be left
untouched. But, if needed, the `servlet-context` path can be
controlled via the `--servlet-context` cmd line option. See tips below
for more info.

* Url and Routing Best Practices

It's often required to deploy a static html site to a different
"context path" on a web server. For example, say you'd like to host
your site at a url such as:

    http://mydomain.com/docs

The static generator can help to transform urls inside html and css so
that the site will "just work" at that address. But in order for it to
work, there are a few best practcies you need to follow.

In html templates, make sure to prefix urls for css files and js files
with `{{servlet-context}}` like this:

    <link rel="stylesheet" href="{{servlet-context}}/css/screen.css" type="text/css">
    <script src="{{servlet-context}}/js/myapp.js" type="text/javascript"></script>

Note that the "servlet-context" will be replaced by the value passed
via the `--servlet-context` command line option

The static generator will also fix urls found inside css like this
one:

    background: url("/img/918913.png");

The `:css :exclude` section in static-site can ignore large css files
for faster performance when generating static stie.

You also need to be careful about defining routes so that they work in
static site. For example, routes like this won't work in a static site environment:

    (GET "/about" [] (about-page))

Instead, use routes definitions like so:

    (GET "/about.html" [] (about-page))

This way, anchor tag links will work in both dynamic and static environments.

