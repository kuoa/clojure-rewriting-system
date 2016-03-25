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
  "Applies in a consequtive order the strategies `s & others`."
  ([] strat-identity)
  ([s & others]
   (if (seq others)
     (strat-and-then-2 s (apply strat-and-then others))
     s)))


(defn strat-or-else-2 [s1 s2]
  (fn [t] (if-let [u (s1 t)] u (s2 t))))

(defn strat-or-else
  "Applies the first strategy that works, from `s & others`."
  ([] strat-failure)
  ([s & others]
   (if (seq others)
     (strat-or-else-2 s (apply strat-and-then others))
     s)))


(defn some-success
  "Applies the strategy `s` to the terms `terms`. 
  We return a list with new terms for each strategy that succeeds."
  [s term]
  (loop [ts term, success? false, res []]
    (if (seq ts)
      (if-let [u (s (first ts))]
        (recur (rest ts) true (conj res u))
        (recur (rest ts) success? (conj res (first ts))))
      res)))

(defn some-success-reduce
  "Idem as some-succes but using reduce and returns a array containing
  [result,sucess?] which allows us to avoid comparing the terms to know if
  the strategy has failed or not."
  [s term]
  (reduce (fn [[res, success?], t]
            (if-let [u (s t)]
              [(conj res u), true]
              [(conj res t), success?])) [[], false] term))


(defn strat-sub
  "Strategy based on some-success-reduce without term comparing
  P.S HERE COME THE BONUS POINTS :D"
  [s]
  (fn [t]
    (let [res (some-success-reduce s t)]
      (when (second res)
        (first res)))))

(defn strat-bottom-up
  "Bottom up recursive strategy"
  [s]
  (fn [t] (strat-and-then (strat-sub (strat-buttom-up s)) s) t))
    
      
        
    
              
            




