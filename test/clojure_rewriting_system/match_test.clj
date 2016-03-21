(ns clojure-rewriting-system.match_test
  (:use midje.sweet)
  (:require [clojure-rewriting-system.match :refer :all]))


(fact "Variable application"
     (subst '?X '{ ?X (3 * 1) }) => '(3 * 1)
     (subst '?Y '{ ?X (3 * 1) }) => '?Y)

(fact "Application example"
     (subst '(?X + 0) '{ ?X (3 * 1) })
     => '((3 * 1) + 0)

     (subst '(?X + ?Y) '{ ?X (3 * 1)
                          ?Y 8 })
     => '((3 * 1) + 8)

     (subst '(?X + ?Y) '{ ?X 8 })
     => '(8 + ?Y)

     (subst '(2 + 3) '{ ?X (3 * 1) })
     => '(2 + 3))

(fact "Substitution avec vecteurs"
     (subst '[?X + 0] '{ ?X [3 * 1] })
     => '[[3 * 1] + 0])

(fact "Substitution ave une map"
     (subst '{:un (?X + 1)
              :deux (?Y + 2) } '{ ?X (3 * 1)
                                 ?Y 8 })
     => '{:un ((3 * 1) + 1)
          :deux (8 + 2) })

;;; Remarque: les clés dans les maps sont des constantes

(fact "Matching de variable."
     (pmatch '?X '(3 + 0) {}) => '{?X (3 + 0)}

     (pmatch '?X '(3 + 0) '{ ?X 8 }) => nil

     (pmatch '?X '(3 + 0) '{ ?X (3 + 0) }) => '{ ?X (3 + 0) })

(fact "Exemples de matching"

     (pmatch '(?X + 0) '(3 + 0) {})
     => '{ ?X 3 }

     (pmatch '(?X + 0) '(3 * 1) {})
     => nil
     
     (pmatch '(?X + 0) '((3 * 1) + 0) {})
     => '{ ?X (3 * 1) }

     (pmatch '(?X + 0) '3 {})
     => nil

     (pmatch '(?X + 0) '((3 + 0) + 0) {})
     => '{ ?X (3 + 0) }

     (pmatch '((?X + ?Y) + 0) '((3 + 0) + 0) {})
     => '{?X 3
          ?Y 0}

     (pmatch '(?X + ?Y) '(3 + 2 + 1) {}) => nil

     (pmatch '(?X + ?Y) '(3 + (2 + 1)) {})
     => {?X 3
         ?Y (2 + 1)}

     (pmatch '(?X + ?Y + ?Z) '(3 + 2) {}) => nil

     (pmatch '(3 + 0)  '(3 + 1) {}) => nil

     (pmatch '0  '1 {}) => nil
     
     (pmatch '1  '1 {}) => {}

     (pmatch '(3 + 0) '(3 + 0) {})
     => {})
