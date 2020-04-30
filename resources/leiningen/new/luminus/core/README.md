# <<name>>

generated using Luminus version "3.69"

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

You'll also need
`dev-config.edn` and `test-config.edn` files
for development and testing environments
similar to the sample
`dev-config.edn.sample` and `test-config.edn.sample` files included
since the main ones are ignored by git to not publicly expose secrets.

## Running

To start a web server for the application, run:
<% if not boot %>
    lein run 
<% else %>
    boot run
<% endif %>

## Testing

To start run tests for the application, run:
<% if not boot %>
    lein test
<% endif %>

## License

Copyright © <<year>> FIXME
