(ns errors.dictionaries_test
  (:require [errors.dictionaries :refer :all]
            [expectations :refer :all]
            [corefns.corefns :refer :all]
            [errors.prettify_exception :refer :all]
            [errors.messageobj :refer :all]))

(defn my-prettify-exception [e]
  (let [e-class (class e)
        m (.getMessage e)
        message (if m m "")] ; converting an empty message from nil to ""
    (get-pretty-message e-class message)))

(defn my-run-and-catch [code]
  "A function that takes quoted code and runs it, attempting to catch any
  exceptions it may throw. Returns the exeception or nil."
  (in-ns 'intro.core)
   (try (eval code)
           (catch Throwable e (my-prettify-exception e))))

;; testing for get-function-name
(expect "days" (get-function-name "happy$days"))
(expect "days" (get-function-name "happy/days"))
(expect "blahBlahBlahBlahNuthin'" (get-function-name "blahBlahBlahBlahNuthin'"))

;#######################################################################################
;### Checking if functions realize when too many or too little args are being passed ###
;#######################################################################################

(expect "Wrong number of arguments (3) passed to a function count"
        (get-all-text
         (my-run-and-catch '(count [] [] []))))

(expect "Wrong number of arguments (3) passed to a function into"
        (get-all-text
         (my-run-and-catch '(into [] '(5 10 15) [2 4 6]))))

(expect "Wrong number of arguments (0) passed to a function zero?, while compiling "
        (get-all-text (butlast (my-run-and-catch '(zero?)))))

(expect "Wrong number of arguments (3) passed to a function even?"
        (get-all-text (my-run-and-catch '(even? 3 6 1))))

(expect "Wrong number of arguments (0) passed to a function reduce"
        (get-all-text (my-run-and-catch '(reduce))))

(expect "Wrong number of arguments (0) passed to a function nth"
        (get-all-text (my-run-and-catch '(nth))))

(expect "Wrong number of arguments (0) passed to a function filter"
        (get-all-text (my-run-and-catch '(filter))))

(expect "Wrong number of arguments (3) passed to a function add-first"
        (get-all-text (my-run-and-catch '(add-first [1 2 3] 0 0))))

(expect "Wrong number of arguments (3) passed to a function add-last"
        (get-all-text (my-run-and-catch '(add-last [:a :b :c] :d :e))))

(expect "Wrong number of arguments (3) passed to a function contains-key?"
        (get-all-text (my-run-and-catch '(contains-key? {:a 1 :b 2 :c 3} 1 true))))

(expect "Wrong number of arguments (3) passed to a function contains-value?"
        (get-all-text (my-run-and-catch '(contains-value? {:a 1 :b 2 :c 3} :c true))))

(expect "Wrong number of arguments (3) passed to a function any?"
        (get-all-text (my-run-and-catch '(any? odd? [1 2 3] [1 2 3]))))
