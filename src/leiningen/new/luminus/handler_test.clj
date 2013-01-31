(ns {{name}}.test.handler
  (:use clojure.test
        ring.mock.request
        {{name}}.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response)
             "<!DOCTYPE html>\n<html><head><title>Welcome to guestbook</title><link href=\"/css/screen.css\" rel=\"stylesheet\" type=\"text/css\"></head><body><h1>Hello World!</h1></body></html>"))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))
