(ns clojure-rewriting-system.test_terms
  (:use midje.sweet)
  (:require [clojure-rewriting-system.terms :refer :all]))


(fact "Some variables"
     (variable? '?X) => true
     (variable? '?toto) => true
     (variable? :?Y) => true
     (variable? :?titi) => true)

(fact "Not a variable"
     (variable? 'toto) => false
     (variable? 2) => false
     (variable? true) => false
     (variable? '(2 ?X :toto)) => false)

(fact "Some seq-terms"
     (seq-term? '(1 2 3 ?X)) => true
     (seq-term? '()) => true)

(fact "Not a seq-term"
     (seq-term? '?X) => false
     (seq-term? "toto") => false
     (seq-term? nil) => false)

(fact "Some vec-terms"
     (vec-term? '[1 2 3 ?X]) => true
     (vec-term? '[]) => true)

(fact "Not a vec-term"
     (vec-term? '?X) => false
     (vec-term? "toto") => false
     (vec-term? nil) => false)

(fact "Some map-terms"
     (map-term? '{:one 1 :two 2 :three 3 :X ?X}) => true
     (map-term? '{}) => true)

(fact "Not a vec-term"
     (map-term? '?X) => false
     (map-term? "toto") => false
     (map-term? '[:hohoho]) => false
     (map-term? nil) => false)

(fact "Constants"
     (constant? 2) => true
     (constant? "toto") => true
     (constant? true) => true
     (constant? false) => true
     (constant? 'toto) => true
     (constant? :titi) => true)

(fact "Not a constant"
     (constant? '?X) => false
     (constant? '(1 2 3)) => false
     (constant? nil) => false)


(fact "Nil is not a term"
      (term? nil) => false
      (term? 'X) => true
      (term? ['X 'Y 3]) => true
      (term? {:h 'X}) => true)
