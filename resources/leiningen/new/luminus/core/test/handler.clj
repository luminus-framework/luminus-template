(ns <<project-ns>>.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [<<project-ns>>.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (<% if war %>app<% else %>(app)<% endif %> (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (<% if war %>app<% else %>(app)<% endif %> (request :get "/invalid"))]
      (is (= 404 (:status response))))))
