(ns experimental.core_test
  (:require [clj-stacktrace.core :as stacktrace]
            [expectations :refer :all]
            [strings.strings :refer :all]
            [errors.exceptions :as errors])
  (:import [java.io.FileInputStream]
           [java.io.ObjectInputStream]
           [java.io.FileOutputStream]
           [java.io.ObjectOutputStream]))

;;;; A space for prototypes, examples, and experimental features.
;; NEVER refer to this file in other files.

;************************************
(def ex1 (errors/run-and-catch '(1)))

(def ex2 (errors/run-and-catch '(+ 2 "pie")))

(def ex3 (errors/run-and-catch '(5)))

(def ex4 (errors/run-and-catch '(def w 4 5)))

(def ex5 (errors/run-and-catch '(defn foo (q) q)))

(def ex6 (errors/run-and-catch '(count 6)))

(def st1 (stacktrace/parse-exception ex3))

;(def fst1 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st1))))
