(ns clojure-rewriting-system.match
  (:require [clojure-rewriting-system.terms :refer :all]))

(defn subst
  "Applies the substitution `s` to the terme `t`."
  [t s]
  (cond
    (variable? t) (if-let [r (get s t)] r t)
    
    (or (seq-term? t)
        (vec-term? t)) (map #(subst % s) t)

    (map-term? t) (reduce
                   (fn [m [key value]]
                     (assoc m key (subst value s))) {} t)
    :else t))
