(ns test-templates
  (:require [clojure.test :refer :all]))

(deftest test-noop
    (is (= 2 (+ 1 1))))
