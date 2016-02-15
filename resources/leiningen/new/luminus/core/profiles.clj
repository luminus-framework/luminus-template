{:profiles/dev  {:env {<% if database-profile-dev %><<database-profile-dev>><% endif %>}}
 :profiles/test {:env {<% if database-profile-dev %><<database-profile-test>><% endif %>}}}