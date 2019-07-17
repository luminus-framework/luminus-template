(ns test-templates
  (:require [clojure.test :refer :all]
            [clojure.java.shell :only [sh] :as shell]))

(deftest test-noop
  (print (shell/sh "lein" "new" "luminus" "tmp"))
  (is (= 1 1)))

