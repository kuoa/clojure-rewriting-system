(ns clojure-rewriting-system.system
  (:require [clojure-rewriting-system.match :refer :all]))

(def simple-rules {:l-times-1 '[(?X * 1) ?X]
                   :r-times-1 '[(1 * ?X) ?X]
                   :l-plus-0  '[(?X + 0) ?X]
                   :r-plus-0  '[(0 + ?X) ?X]})

(defn rewrite
  "Rewrite the term `t` using the rule `[l r]`."
  [t [l r]]
  (when-let [s (pmatch l t {})]
    (subst r s)))


(defn apply-rule
  [t rule sys]
  "Applies the rule `rule` from the system `sys` on term `t`."
  (if-let [rule (sys rule)]
    (rewrite t rule)
    (throw (ex-info "Unknown rule."
                    {:name rule, :sys sys}))))

(defn strat-identity [t] t)

(defn strat-failure [t] nil)

(defn strat-rule
  "Construct a strategy that consists of apllying the rule `rule` 
  from the system `sys` to the term `t`."
  [rule sys]
  (fn [t] (apply-rule t rule sys)))


(defn strat-and-then-2 [s1 s2]
  (fn [t] (when-let [u (s1 t)] (s2 u))))

(defn strat-and-then
  "Applies in a consequtive order the strategies `strats`."
  ([] (strat-identity))
  ([s & others]
   (if (seq others)
     (strat-and-then-2 s (apply strat-and-then others))
     s)))
