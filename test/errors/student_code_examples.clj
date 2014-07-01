(ns errors.student_code_examples
      (:require [expectations :refer :all]
                [errors.messageobj :refer :all]
                [errors.exceptions :refer :all]
                [errors.prettify_exception :refer :all]
                [corefns.corefns :refer :all]))

;;the function without declaring prob44 before helper functions

;(expect  (get-all-text (run-and-catch-student-code-examples
;                                        '(defn prob44-main [n coll]
;                                           '(defn prob44-helper-positive [n coll]
;                                              (let [result (add-last (rest coll) (first coll))]
;                                                (prob44 (- n 1) result)))
;                                           '(defn prob44-helper-negative [n coll]
;                                              (let [result (add-first (butlast coll) (last coll))]
;                                                (prob44 (+ n 1) result)))
;                                           '(defn prob44 [n coll]
;                                              (cond
;                                               (= n 0) coll ;n equal to zero
;                                               (> n 0) (prob44-helper-positive n coll) ;n is positive
;                                               (< n 0) (prob44-helper-negative n coll) ;n is negative
;                                               ))))))

;; now, the function working perfectly when declared beforehand

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
   (< n 0) (prob44-helper-negative n coll) ;n is negative
   ))

(expect '(3 4 5 1 2) (prob44 2 [1 2 3 4 5])) ; normally, function does work


;(expect "sassafras" (get-all-text (run-and-catch-student-code-examples
;                                        '(fn nil-key? [k hashmap]
;                                           (any? nil? hashmap)))))
