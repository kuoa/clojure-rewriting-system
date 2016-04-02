(ns clojure-rewriting-system.rules_test
  (:use midje.sweet)
  (:require [clojure-rewriting-system.rules :refer :all]))

(fact "Rules"
      (rule? '[(?X + 1) ?X]) => true)

(fact "Not valid rules"
      (rule? '[(?X + 1)]) => false
      (rule? '[?X nil]) => false
      (rule? '[(?X * 12) (?X + ?Y)]) => false
      (rule? '[{:x ?X :y {:z ?Z}} ?Y]) => false)

(fact "Right / left rule access."
     (l '[(?X + 1) ?X]) => '(?X + 1)
     (r '[(?X + 1) ?X]) => '?X)


(fact "Variables"
      (variables '?X) => '#{?X}
      (variables '(?X ?Y ?Y)) => '#{?X ?Y}
      (variables '(?X (?Y (?X ?Y ?Z hey)))) => '#{?X ?Y ?Z}
      (variables 'hey) => '#{}
      (variables '[?X [?Y]]) => '#{?X ?Y}
      (variables '{:x ?X :y ?Y :b ?Z}) => '#{?X ?Y ?Z}
      (variables '({:x ?X :y {:z ?Z}} [(?Y {:a ?A})])) => '#{?X ?Z ?Y ?A})
