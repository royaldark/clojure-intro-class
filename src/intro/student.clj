(ns intro.student
	;(:use [corefns.corefns]
   ;     [seesaw.core])
  (:require [expectations :refer :all]
            [corefns.corefns :refer :all]
            [strings.strings :refer :all]))

;##################################
;### Testing compilation errors ###
;##################################

;(def f [x] (+ x 2))
;(+ (* 2 3) 7

;(+ (* 2 3) 7)]

;(defn f[x] (+ x y))

;(defn f[x y]
;	(fn [x] (+ x y z)))

;(loop [x 1 y 2]
;	(if (= x 0) 1
;		(recur (dec x) 5)))


;#############################################################
;### 4clojure and beginner problems like students would do ###
;#############################################################

(defn prob44-helper-positive [n coll]
  (let [result (add-last (rest coll) (first coll))]
    (prob44 (- n 1) result)))

(defn prob44-helper-negative [n coll]
  (let [result (add-first (butlast coll) (last coll))]
    (prob44 (+ n 1) result)))

(defn prob44 [n coll]
  (cond
   (= n 0) coll ;n equal to zero
   (> n 0) (prob44-helper-positive n coll) ;n is positive
   (< n 0) (prob44-helper-negative n coll))) ;n is negative

