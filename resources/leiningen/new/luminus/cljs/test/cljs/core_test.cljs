(ns <<project-ns>>.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]<% if reagent %>
            [reagent.core :as reagent :refer [atom]]<% endif %>
            [<<project-ns>>.core :as rc]))

(deftest test-home
  (is (= true true)))

