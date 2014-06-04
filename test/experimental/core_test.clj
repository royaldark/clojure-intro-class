(ns experimental.core_test
  (:require [clj-stacktrace.core :as stacktrace]
            [expectations :refer :all]
            [strings.core :refer :all]
            [errors.core-test :as errors])
  (:import [java.io.FileInputStream]
           [java.io.ObjectInputStream]
           [java.io.FileOutputStream]
           [java.io.ObjectOutputStream]))

;;;; A space for prototypes, examples, and experimental features.
;; NEVER refer to this file in other files.

;*****************************
;*** comparing stacktraces ***

(defn get-fns-in-stacktrace
  "gets all of the functions mentioned in a parsed stacktrace"
  [trace]
  (get-keyword-but-not-x-in-stacktrace
   :fn
   (fn [ele] (not (strings.core/string-blank? (re-matches #"eval.*" ele))))
   trace))

(defn get-keyword-in-stacktrace
  "gets all of the functions mentioned in a parsed stacktrace"
  [a-keyword trace]
  (filter
   (fn [ele]
     (not (nil? ele)))
   (map a-keyword (:trace-elems trace))))

(defn get-keyword-but-not-x-in-stacktrace
  "Don't actually use this - wrap it in a helper function. Gets all the values of a keyword mentioned in a parsed stacktrace, except those that the predicate returns true fo  "
  [a-keyword pred trace]
  (filter
   (fn [ele]
     (not (or (nil? ele)
              (pred ele))))
   (map (a-keyword (:trace-elems trace)))))

;; expect a k/v pair in a map.
;expect {:foo 1} (in {:foo 1 :cat 4}))

(get-fns-in-stacktrace (stacktrace/parse-exception ex1))
;(get-fns-in-stacktrace (stacktrace/parse-exception ex1))
;(get-fns-in-stacktrace (stacktrace/parse-exception ex4))

;************************************
(def ex1 (error/run-and-catch '(1)))

(def ex2 (run-and-catch '(+ 2 "pie")))

(def ex3 (run-and-catch '(5)))

(def ex4 (run-and-catch '(def w 4 5)))

(def ex5 (run-and-catch '(defn foo (q) q)))

(def ex6 (run-and-catch '(count 6)))

(def st1 (stacktrace/parse-exception ex3))

;(def fst1 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st1))))



