;; WARNING
;; The profiles.clj file is used for local environment variables, such as database credentials.
;; This file is listed in .gitignore and will be excluded from version control by Git.

{:profiles/dev  {:env {<% if database-profile-dev %><<database-profile-dev>><% endif %>}}
 :profiles/test {:env {<% if database-profile-dev %><<database-profile-test>><% endif %>}}}
