{:provided {:env {;;enable to start the nREPL server when the application launches
                  :nrepl-port "7001"<% if database-profiles %>
                  <<database-profiles>><% endif %>}}}
