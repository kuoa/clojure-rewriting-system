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


(declare pmatch)

(defn pmatch-seq-vec
  "Matches over `seq-terms` or `vec-terms`."
  [pseq tseq s]
  (if (seq pseq)
    (when (seq tseq)
      (when-let [s1 (pmatch (first pseq) (first tseq) s)]
        (recur (rest pseq) (rest tseq) s1)))
    (when (empty? tseq)
      s)))

(defn pmatch-map
  "Matches over `map-terms`."
  [pmap tmap s]
  (if (seq pmap)
    (when (seq tmap)
      (when-let [s1 (pmatch (second (first pmap)) (second (first tmap)) s)]
        (recur (rest pmap) (rest tmap) s1)))
    (when (empty? pmap)
      s)))

(defn pmatch
  "Matches the term `t` (with no variables)
  with the term `p` (with variables) in the substitution context `s`. "
  [p t s]
  (cond
    (variable? p) (if-let [u (get s p)]
                    (when (= u t)
                      s)
                    (assoc s p t))

    (seq-term? p) (when (seq-term? t)
                    (pmatch-seq-vec p t s))

    (vec-term? p) (when (vec-term? t)
                    (pmatch-seq-vec p t s))

    (map-term? p) (when (map-term? t)
                    (pmatch-map p t s))

    :else (when (= p t) s)))

