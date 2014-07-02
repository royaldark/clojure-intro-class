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

;; 4clojure Problem 44
(declare prob44)

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

;; DrRacket Excercise 2 - exception in exceptions/DrRacket-Exercise2-ClassCast.ser
(defn exercise2 [str1 str2]
 (conj str1 str2))

;; DrRacket Exercise 3
(defn exercise3 [a-string index]
  (str (subs a-string 0 index) "_" (subs a-string index (+ 2 (count a-string)))))

;(expect "hello_world" (exercise3 "helloworld" 5))
