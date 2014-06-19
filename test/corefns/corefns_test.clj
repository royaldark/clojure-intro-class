(ns corefns.corefns_test
  (:require [expectations :refer :all]
            [corefns.corefns :refer :all]
            [strings.strings :refer :all]
            [errors.messageobj :refer :all]
            [errors.prettify_exception :refer :all]))

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

;#################################################
;### Testing if the corefns preconditions work ###
;#################################################

;;; testing the first precondition of map
(expect "in function map first argument o must be a function but is a character"
        (get-all-text
           (my-run-and-catch '(doall (map \o [1 2 3])))))

;;; testing the second precondition of map
(expect "in function map second argument :hello must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch '(doall (map + :hello)))))

;;; testing the precondition of count
(expect "in function count first argument 3 must be a sequence but is a number"
        (get-all-text
            (my-run-and-catch '(count 3))))

;;; testing the precondition of conj
(expect "in function conj first argument :hi must be a sequence but is a keyword"
        (get-all-text
            (my-run-and-catch '(conj :hi))))

;;; testing the first precondition of into
(expect "in function into first argument 42 must be a sequence but is a number"
        (get-all-text
            (my-run-and-catch '(into 42 [1 2 3]))))

;;; testing the second precondition of into
(expect "in function into second argument 90 must be a sequence but is a number"
        (get-all-text
            (my-run-and-catch '(into #{} 90))))

