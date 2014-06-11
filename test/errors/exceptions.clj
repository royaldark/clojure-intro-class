(ns errors.exceptions
  (:require [expectations :refer :all]
            [clj-stacktrace.core :as stacktrace])
  (:import [java.io.FileInputStream]
           [java.io.ObjectInputStream]
           [java.io.FileOutputStream]
           [java.io.ObjectOutputStream]
           [java.util.ArrayList]))

;;; INDEX ;;;

;1. Writing/Reading to file
;|-1.1 functions
;|-1.2 tests
;2 Generating exceptions
;|-2.1 functions
;|-2.2 tests
;3. Comparing Stacktraces
;|-2.1 functions
;|-2.2 tests

;################################
;## 1. Writing/Reading to file ##
;################################

;## global vars ##
(def path "exceptions/")

; 1.1 functions

;## NOTE ##
;;The second part of let is a series of expressions that evaluate in /order/, returning the last expression.
;;This is Important:
;;The following two functions are dependent on events occurring in chronological order.

(defn export-to-file
  "Uses Java's Serializable to write a (java) object to a file"
  [obj filepath]
  (let [file-stream (java.io.FileOutputStream. filepath)
        obj-stream (java.io.ObjectOutputStream. file-stream)
        ]
    (.writeObject obj-stream obj)
    (.close obj-stream)
    (.close file-stream)
    (println (str "data saved in project folder or: " filepath))
  ))

(defn import-from-file
  "Uses Java's Serializable to read a (java) object from a file"
  [filepath]
  (let [file-stream (java.io.FileInputStream. filepath)
        obj-stream (java.io.ObjectInputStream. file-stream)
        e (.readObject obj-stream)]
    (.close obj-stream)
    (.close file-stream)
    e))

(defn- write-objects-local
  "writes a java object to a file, creating it if it does not exist, in path (see errors.exceptions)"
  [object filename]
  (export-to-file object (str path filename)))

(defn- read-objects-local
  "reads a file in path (see errors.exceptions) as a java object"
  [filename]
  (import-from-file (str path filename)))

; 1.2 testing reading/writing to file

(def java-arraylist (new java.util.ArrayList 5))

(expect (.equals java-arraylist
           (let [filename "testfile.silly"
                 object java-arraylist]
             (write-objects-local object filename)
             (read-objects-local filename))))

;##############################
;## 2. Generating Exceptions ##
;##############################

; 2.1 functions

(defn run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

(defn exception->string
  "Converts exceptions to strings, returning a string or the original e if it is not an exception"
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))

; 2.2 tests

(expect "java.lang.Long cannot be cast to clojure.lang.IFn"
        (exception->string (run-and-catch '(1 3))))

(expect 3
        (exception->string (run-and-catch '(+ 1 2))))

;##############################
;## 3. Comparing Stacktraces ##
;##############################

; 3.1 functions

(defn get-keyword-in-stacktrace
  "Gets all of the keywords mentioned in a parsed stacktrace"
  [a-keyword trace]
  (filter
   (fn [ele]
     (not (nil? ele)))
   (map a-keyword (:trace-elems trace))))

(defn- get-keyword-but-not-x-in-stacktrace
  "Don't actually use this - wrap it in a helper function. Gets all the values of a keyword mentioned in a parsed stacktrace, except those that the predicate returns true for"
  [a-keyword pred trace]
  (filter
   (fn [ele]
     (not (or (nil? ele)
              (pred ele))))
   (map a-keyword (:trace-elems trace))))

;; an example of what get-keyword-but-not-x-in-stacktrace
(defn get-fns-in-stacktrace
  "Gets all of the functions mentioned in a parsed stacktrace"
  [trace]
  (get-keyword-but-not-x-in-stacktrace
   :fn
   (fn [ele] (not (clojure.string/blank? (re-matches #"eval\d.*" ele))))
   trace))


(defn get-eval-nums
  "Gets all evaulation numbers - a random number (confirm?) that is attached to all evals in the stacktrace. Used to confirm that two stacktraces came from the same exception."
  [trace]
  (map (fn [ele] ele)
       (get-keyword-in-stacktrace :fn trace)))

; 3.2 tests

(def ex1 (run-and-catch '(+ 2 "pie")))

(expect "eval" (first (get-fns-in-stacktrace (stacktrace/parse-exception ex1))))
