(ns experimental.core_test
  (:require [clj-stacktrace.core :as stacktrace]
            [expectations :refer :all]
            [errors.messageobj :refer :all]
            [errors.core :refer :all]
            [clojure.java.io :as io])
  (:import [java.io.FileInputStream]
           [java.io.ObjectInputStream]
           [java.io.FileOutputStream]
           [java.io.ObjectOutputStream]))

;;;; A space for prototypes, examples, and experimental features.
;; NEVER refer to this file in other files.

;*****************************
;*** comparing stacktraces ***

(defn get-fns-in-stacktrace
  "takes a parsed exception, returns a seq"
  [trace]
  (filter
   (fn [ele]
     (not (nil? ele)))
   (map :fn (:trace-elems trace))))

(defn get-keyword-in-stacktrace
  "takes a parsed exception, returns a seq"
  [a-keyword trace]
  (filter
   (fn [ele]
     (not (nil? ele)))
   (map a-keyword (:trace-elems trace))))

;(get-fns-in-stacktrace (stacktrace/parse-exception ex6))
;(get-fns-in-stacktrace (stacktrace/parse-exception ex1))
;(get-fns-in-stacktrace (stacktrace/parse-exception ex4))

;************************************
;*** try/catch function prototype ***

(defn run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

;a helper function to cleanly test the above.
(defn- exception->string
  "Converts exceptions to strings, returning a string or the original e (if it is not an exception)"
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))

(expect "java.lang.Long cannot be cast to clojure.lang.IFn"
        (exception->string (run-and-catch '(1 3))))

(expect 3
        (exception->string (run-and-catch '(+ 1 2))))

;************************************
(def ex1 (run-and-catch '(1)))

(def ex2 (run-and-catch '(+ 2 "pie")))

(def ex3 (run-and-catch '(5)))

(def ex4 (run-and-catch '(def w 4 5)))

(def ex5 (run-and-catch '(defn foo (q) q)))

(def ex6 (run-and-catch '(count 6)))

(def st1 (stacktrace/parse-exception ex3))

;(def fst1 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st1))))



