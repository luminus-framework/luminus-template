(require '[clj-webdriver.taxi :as taxi]
         '[<<project-ns>>.browser :refer [browser-up browser-down]]
         '[clojure.test :refer :all])

(Given #"^I am at the \"homepage\"$" []
       (browser-up)
       (taxi/to "http://localhost:3000/"))

(Then #"^I should see \"([^\"]*)\"$" [title]
      (is (= (taxi/text "div.jumbotron > h1") title))
      (browser-down))
