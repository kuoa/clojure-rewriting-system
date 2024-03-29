(ns clojure-rewriting-system.system_test
  (:use midje.sweet)
  (:require [clojure-rewriting-system.system :refer :all]))

(fact "Rewriting."
      
    (rewrite '(3 + 0) '[(?X + 0) ?X]) => 3
    
    (rewrite '((3 * 1) + 0) '[(?X + 0) ?X])

     => '(3 * 1)

     (rewrite '(3 + 0) '[(?X + 0) (?X * ?X)])
     => '(3 * 3)

     (rewrite '(3 + 0) '[(?X + ?Y) (?X * ?Y)])
     => '(3 * 0)
   
     (rewrite '((3 + 0) * 1) '[(?X + 0) ?X])

     => nil)

(fact "Rewriting with multiple rules"
     (apply-rule '((3 + 0) * 1) :l-times-1 simple-rules)
     => '(3 + 0)

     (apply-rule '((3 + 0) * 1) :r-times-1 simple-rules)
     => nil

     (:name
      (try
        (apply-rule '((3 + 0) * 1) :blabla simple-rules)
        (catch Exception e (ex-data e))))
     => :blabla)
   

(fact "Rewriting with multiple rules."
     ((strat-rule :l-times-1 simple-rules)
      '((3 + 0) * 1)) => '(3 + 0)

     (let [strat (strat-rule :l-times-1 simple-rules)]
       (strat '((3 + 0) * 1))) => '(3 + 0)

     ((strat-rule :r-times-1 simple-rules)
      '((3 + 0) * 1)) => nil)

(fact "Strat and then."
     (let [s (strat-and-then
              (strat-rule :r-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))

     => '(42 + 2)

     (let [s (strat-and-then
              (strat-rule :l-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))

     => nil
   
     (let [s (strat-and-then
              (strat-rule :r-plus-0 simple-rules)
              (strat-rule :r-plus-0 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))
   
     => nil)



(fact "Strat or-else-2."
     (let [s (strat-or-else-2
              (strat-rule :r-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))

     => '((42 + 2) * 1)

     (let [k (strat-or-else-2
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
       (k '(0 + ((42 + 2) * 1))))

     => '((42 + 2) * 1)

     (let [s (strat-or-else-2
              (strat-rule :l-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(?X)))

     => nil)

(fact "Strat or-else."
      (let [s (strat-or-else
              (strat-rule :r-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))

      => '((42 + 2) * 1)

       (let [k (strat-or-else
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
         (k '(0 + ((42 + 2) * 1))))
       => '((42 + 2) * 1)

     (let [k (strat-or-else
              (strat-rule :l-times-1 simple-rules)
              (strat-rule :l-times-1 simple-rules)
              (strat-rule :r-plus-0 simple-rules))]
       (k '(0 + ((42 + 2) * 1))))

     => '((42 + 2) * 1)

     (let [k (strat-or-else
              (strat-rule :l-times-1 simple-rules)
              (strat-rule :l-times-1 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (k '(0 + ((42 + 2) * 1))))
     => nil
     
     (let [s (strat-or-else
              (strat-rule :l-plus-0 simple-rules)
              (strat-rule :l-times-1 simple-rules))]
       (s '(?X)))

     => nil)


(fact "Some-success."

     (some-success (strat-rule :l-plus-0 simple-rules)
                   '((3 + 0) (4 + 0) (0 + 4) (5 + 0)))

     => '[3 4 (0 + 4) 5]

     (some-success (strat-rule :l-plus-0 simple-rules)
                   '((0 + 3) (0 + 4) (0 + 5)))

     => '[(0 + 3) (0 + 4) (0 + 5)])


(fact "Some-success-reduce."

     (first (some-success-reduce (strat-rule :l-plus-0 simple-rules)
                   '((3 + 0) (4 + 0) (0 + 4) (5 + 0))))

     => '[3 4 (0 + 4) 5]

     (second (some-success-reduce (strat-rule :l-plus-0 simple-rules)
                   '((3 + 0) (4 + 0) (0 + 4) (5 + 0))))

     => true

     (first (some-success-reduce (strat-rule :l-plus-0 simple-rules)
                   '((0 + 3) (0 + 4) (0 + 5))))

     => '[(0 + 3) (0 + 4) (0 + 5)]

     (second (some-success-reduce (strat-rule :l-plus-0 simple-rules)
                   '((0 + 3) (0 + 4) (0 + 5))))

     => false)
    


(fact "Exemples avec strat-sub"

     (let [strat (strat-sub (strat-rule :l-plus-0 simple-rules))]
       (strat '((3 + 0) (4 + 0) (0 + 4) (5 + 0))))
     => [3 4 '(0 + 4) 5]

     (let [strat (strat-sub (strat-rule :l-plus-0 simple-rules))]
       (strat '((0 + 4) (0 + 3) (0 + 2))))
     => nil

     (let [strat (strat-sub (strat-rule :l-plus-0 simple-rules))]
       (strat '[]))
     => nil)

(fact "Exemple de bottom-up de niveau 1."

     ;; ((4 + 0) + 0) --> (4 + 0)
     (let [s-lplus-zero (strat-rule :l-plus-0 simple-rules)]
       ((strat-sub s-lplus-zero)
        '((4 + 0) + 0))) => '[4 + 0]
     
     ;; ((4 + 0) + 0) --> (4 + 0) --> 4
     (let [s-lplus-zero (strat-rule :l-plus-0 simple-rules)]
       ((strat-and-then s-lplus-zero s-lplus-zero)
        '((4 + 0) + 0))) => 4)


;;; 

(fact "Stratégie bottom up"

     (let [strat (strat-rule :l-plus-0 simple-rules)]
       ((strat-bottom-up strat)
        '(3 * (4 + (2 + 0)))))
       => '(3 * (4 + 2))

     (let [strat (strat-rule :l-plus-0 simple-rules)]
       ((strat-bottom-up strat)
        '(3 * (4 + (0 + 2)))))
     => nil

     (let [strat (strat-rule :l-plus-0 simple-rules)]
       ((strat-bottom-up strat)
        '(3 * ((2 + 0) + 0))))
     => '(3 * 2))


(fact "Problème avec strat-bottom-up."

     (let [strat (strat-rule :l-plus-0 simple-rules)]
       ((strat-bottom-up strat)
        '((0 + 4) * 5)))
     => nil)



(fact "ArithRules"
      (let [s (strat-or-else
               (strat-rule :same-term-c arith-rules)
               (strat-rule :diff-l arith-rules)
               (strat-rule :diff-r arith-rules)
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-times-1 simple-rules)
               (strat-rule :l-plus-0 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
       (s '(0 + ((42 + 2) * 1))))

      => '((42 + 2) * 1)

      (let [s (strat-or-else
               (strat-rule :same-term-c arith-rules)
               (strat-rule :diff-l arith-rules)
               (strat-rule :diff-r arith-rules)
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-times-1 simple-rules)
               (strat-rule :l-plus-0 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
       (s '(34 - 34)))

      =>  0

      (let [s (strat-or-else
               (strat-rule :same-term-c arith-rules)
               (strat-rule :diff-l arith-rules)
               (strat-rule :diff-r arith-rules)
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-times-1 simple-rules)
               (strat-rule :l-plus-0 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
       (s '((34 + (?X - (2 + 3))) - (?X - (2 + 3)))))

      =>  34

         (let [s (strat-or-else
               (strat-rule :same-term-c arith-rules)
               (strat-rule :diff-l arith-rules)
               (strat-rule :diff-r arith-rules)
               (strat-rule :l-times-1 simple-rules)
               (strat-rule :r-times-1 simple-rules)
               (strat-rule :l-plus-0 simple-rules)
               (strat-rule :r-plus-0 simple-rules))]
       (s '(((?X + ?Y + ?Z) + 55) - (?X + ?Y + ?Z))))

         =>  55

         (let [s (strat-or-else
                  (strat-rule :same-term-c arith-rules)
                  (strat-rule :diff-l arith-rules)
                  (strat-rule :diff-r arith-rules)
                  (strat-rule :sqrt arith-rules)
                  (strat-rule :sqr arith-rules)
                  (strat-rule :l-times-1 simple-rules)
                  (strat-rule :r-times-1 simple-rules)
                  (strat-rule :l-plus-0 simple-rules)
                  (strat-rule :r-plus-0 simple-rules))]
           (s '(sqrt (?X * ?X))))

         =>  '(|?X|)

            (let [s (strat-or-else
                  (strat-rule :same-term-c arith-rules)
                  (strat-rule :diff-l arith-rules)
                  (strat-rule :diff-r arith-rules)
                  (strat-rule :sqrt arith-rules)
                  (strat-rule :sqr arith-rules)
                  (strat-rule :l-times-1 simple-rules)
                  (strat-rule :r-times-1 simple-rules)
                  (strat-rule :l-plus-0 simple-rules)
                  (strat-rule :r-plus-0 simple-rules))]
              (s '((sqrt ?X) * (sqrt ?X))))

      => '?X)
