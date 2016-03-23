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
     => :blabla
     )
   

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
   
     => nil
     )

;;; 2) le combinateur :  or-else (ou alors)

;;; Exercice (inspiré du and-then) :

;;; (strat-or-else-2 s1 s2)
;;; tente d'appliquer la stratégie s1
;;;   - en cas de succès : c'est le résultat de l'application de s1
;;;   - en cas d'échec : c'est la statégie s2

;;; (strat-or-else s & others)
;;; généralisation à n stratégies
;;; cas particulier:  (strat-or-else) retourne strat-echec

;;; => donner les facts qui vont bien

;;; Exercice (subsidiaire): implémenter some-success avec reduce

(fact "Exemples avec some-success."

     (some-success (strat-regle :lplus-zero simpl-regles)
                   '((3 + 0) (4 + 0) (0 + 4) (5 + 0)))

     => [3 4 (0 + 4) 5]

     (some-success (strat-regle :lplus-zero simpl-regles)
                   '((0 + 3) (0 + 4) (0 + 5)))

     => nil)


;;; Stratégie sub : réécriture des sous-termes

(fact "Exemples avec strat-sub"

     (let [strat (strat-sub (strat-regle :lplus-zero simpl-regles))]
       (strat '((3 + 0) (4 + 0) (0 + 4) (5 + 0))))
     => [3 4 '(0 + 4) 5]

     (let [strat (strat-sub (strat-regle :lplus-zero simpl-regles))]
       (strat '((0 + 4) (0 + 3) (0 + 2))))
     => nil

     (let [strat (strat-sub (strat-regle :lplus-zero simpl-regles))]
       (strat :toto))
     => nil
     
     )

(fact "Exemple de bottom-up de niveau 1."

     ;; ((4 + 0) + 0) --> (4 + 0)
     (let [s-lplus-zero (strat-regle :lplus-zero simpl-regles)]
       ((strat-sub s-lplus-zero)
        '((4 + 0) + 0))) => '(4 + 0)
     
     ;; ((4 + 0) + 0) --> (4 + 0) --> 4
     (let [s-lplus-zero (strat-regle :lplus-zero simpl-regles)]
       ((strat-and-then (strat-sub s-lplus-zero) s-lplus-zero)
        '((4 + 0) + 0))) => 4

     )


;;; Stratégie bottom-up

(fact "Stratégie bottom up"

     (let [strat (strat-regle :lplus-zero simpl-regles)]
       ((strat-bottom-up strat)
        '(3 * (4 + (2 + 0)))))
       => '(3 * (4 + 2))

     (let [strat (strat-regle :lplus-zero simpl-regles)]
       ((strat-bottom-up strat)
        '(3 * (4 + (0 + 2)))))
     => nil

     (let [strat (strat-regle :lplus-zero simpl-regles)]
       ((strat-bottom-up strat)
        '(3 * ((2 + 0) + 0))))
     => '(3 * 2)

     )
     
;;; Pour finir le projet réécriture

;;; 1) redéfinir   strat-bottom-up   pour corriger le problème suivant:

(fact "Problème avec strat-bottom-up."

     (let [strat (strat-regle :lplus-zero simpl-regles)]
       ((strat-bottom-up strat)
        '((0 + 4) * 5)))  ;; => ((0 + 4) * 5)   << problème
     => nil)

;;; question subsidiaire : solution *sans* comparaison de terme

;;; 2) définir un système de simplifications (arithmétiques)
;;;    un peu plus intéressant (en évitant commutativité)

;;;  exemple de règles :   ?X * 1 -> ?X
;;;                        ((?X + ?Y) - ?Y) -> ?X
;;;                        (?X - ?X) -> 0
;;;  (etc....)

;;; la stratégie strat-or-else joue ici un rôle important
