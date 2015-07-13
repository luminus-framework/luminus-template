{:provided {:env {;;when set the application start the nREPL server on load
                  :nrepl-port "7001"<% if database-profiles %>
                  <<database-profiles>><% endif %>}}}
