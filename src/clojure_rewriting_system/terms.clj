(ns clojure-rewriting-system.terms)

(defn variable?
  "A variable is a `symbol` or a `keyword` starting with `?`."
  [t]
  (and (or (symbol? t) (keyword? t))
       (= (first (name t)) \?)))

(defn seq-term?
  "A seq-term is a `sequence`."
  [t] (sequential? t))

(defn vec-term?
  "A vec-term is a `vector`."
  [t] (vector? t))

(defn map-term?
  "A map-term is a `map`."
  [t] (map? t))

(defn constant?
  "A constant is every other combination except `nil`"
  [t]
  (and (not (variable? t))
       (not (seq-term? t))
       (not (vec-term? t))
       (not (map-term? t))
       (not (nil? t))))

(defn term?
  "Is this a term? is it? :))"
  [t]
  (or (variable? t)
      (seq-term? t)
      (vec-term? t)
      (map-term? t)
      (constant? t)))
