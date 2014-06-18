(ns corefns.assert_handling
  (:use [clojure.core.incubator])
  (:require [corefns.failed_asserts_info :refer :all]))

;; Functions to check pre-conditions and record the offenders for
;; error processing
(defn check-if-seqable? [fname x & [n]]
  "returns true if x is seqable and false otherwise, sets data
  in seen-failed-asserts. If the second argument is present, it's added
  to the seen-failed-asserts as the number of the argument"
  (if (seqable? x) true
    (do (add-to-seen {:check "sequence"
                      :class (class x)
                      :value x
                      :fname fname})
      (if n (add-to-seen {:arg-num n}))
      false)))

(defn check-if-function? [fname x]
  (if (fn? x) true
    (do (add-to-seen {:check "function"
                      :class (class x)
                      :value x
                      :fname fname})
      false)))

(defn check-if-number? [fname x]
  (if (number? x) true
    (do (add-to-seen {:check "number"
                      :class (class x)
                      :value x
                      :fname fname})
      false)))

(defn check-if-string? [fname x]
  (if (string? x) true
    (do (add-to-seen {:check "string"
                      :class (class x)
                      :value x
                      :fname fname})
      false)))

(defn check-if-character? [fname x]
  (if (char? x) true
    (do (add-to-seen {:check "character"
                      :class (class x)
                      :value x
                      :fname fname})
      false)))

;; should pass the starting arg number: it's different for different functions
(defn check-if-seqables? [fname arguments start]
  (loop [args arguments n start]
    (if (empty? args) true
      (if (not (check-if-seqable? fname (first args)))
      	(do (add-to-seen {:arg-num n})
          false)
        (recur (rest args) (inc n))))))

;; should pass the starting arg number: it's different for different functions
(defn check-if-numbers? [fname arguments start]
  (loop [args arguments n start]
    (if (empty? args) true
      (if (not (check-if-number? fname (first args)))
      	(do (add-to-seen {:arg-num n})
          false)
        (recur (rest args) (inc n))))))

;; should pass the starting arg number: it's different for different functions
(defn check-if-strings? [fname arguments start]
  (loop [args arguments n start]
    (if (empty? args) true
      (if (not (check-if-string? fname (first args)))
        (do (add-to-seen {:arg-num n})
          false)
        (recur (rest args) (inc n))))))
