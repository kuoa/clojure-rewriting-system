(ns clojure-rewriting-system.rules
  (:require [clojure-rewriting-system.terms :refer :all]))


;;; Exercice : implémenter la contrainte principale
;;; des règles de réécriture:
;;;  dans une règle   lhs -> rhs
;;;  la contrainte est: les variables du membre de droite
;;;  sont un sous-ensemble des variables du membre de gauche
;;   (fonctions :   (variables t) retourne l'ensemble
;;   des variables du terme ...) puis modification de regle?) -> DONE

(defn variables
  "Returns a set of all the variables in `t`.
  Terminal using a dummy stack."
  [t]
  (loop [t t, stack '() rez #{}]
    (cond
      (variable? t) (if (seq stack)
                      (recur (first stack) (rest stack) (conj rez t))
                      (conj rez t))
      (or
       (seq-term? t)
       (vec-term? t)) (if (seq t) ;; '() | [] -> true
                        (recur (first t) (conj stack (rest t)) rez)
                        (if (seq stack)                        
                          (recur (first stack) (rest stack) rez)
                          rez))
      
      (map-term? t) (if (seq t) ;; {} -> true                      
                      (recur (second (first t))
                             (conj stack (dissoc t (first (first t)))) rez)
                      (if (seq stack)
                        (recur (first stack) (rest stack) rez)
                        rez))

    :else (if (seq stack)
              (recur (first stack) (rest stack) rez)
              rez))))

;;; Exercice -> DONE

(defn variables-subset?
  "Check if the variables are a subset"
  [[l r]]
  (clojure.set/subset? (variables r) (variables l)))

(defn rule?
  "Check if the rule `[left right]` has the correct form"
  [t]
  (and (vector? t)
       (= (count t) 2)
       (term? (first t))
       (term? (second t))
       (variables-subset? t)))

(defn l
  "Left side rule access"
  [t] (first t))

(defn r
  "Right side rule access"
  [t] (second t))
