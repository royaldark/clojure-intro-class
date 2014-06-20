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

(defn my-run-and-catch-corefns [code]
  "A function that takes quoted code and runs it, attempting to catch any
  exceptions it may throw. Returns the exeception or nil."
  (in-ns 'intro.core)
   (try (eval code)
           (catch Throwable e (my-prettify-exception e))))

;#################################################
;### Testing if the corefns preconditions work ###
;#################################################

;;; testing for the first precondition of map
(expect "in function map first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(doall (map :not-a-function [1 2 3])))))

;;; testing for the second precondition of map
(expect "in function map second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(doall (map + :not-a-collection)))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of count
(expect "in function count first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(count :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of conj
(expect "in function conj first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(conj :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the first precondition of into
(expect "in function into first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(into :not-a-collection [1 2 3]))))

;;; testing for the second precondition of into
(expect "in function into second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(into #{} :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the first precondition of reduce
(expect "in function reduce first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(reduce :not-a-function [1 2 3]))))

;;; testing for the second precondition of reduce
(expect "in function reduce second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(reduce + :not-a-collection))))

;;; testing for the third precondition of reduce
(expect "in function reduce third argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(reduce + 2 :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the first precondition of nth, with two args
(expect "in function nth first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(nth :not-a-collection 10))))

;;; testing for the second precondition of nth, with two args
(expect "in function nth second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(nth [0 1 2 3 4] :not-a-number))))

;;; testing for the second precondition of nth, with three args
(expect "in function nth first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(nth :not-a-collection 8 "nothing found"))))

;;; testing for the second precondition of nth, with three args
(expect "in function nth second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(nth [0 1 2 3 4] :not-a-number ""))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the first precondition of filter
(expect "in function filter first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(filter :not-a-function [1 2 3]))))

;;; testing for the second precondition of filter
(expect "in function filter second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(filter odd? :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the first precondition of mapcat
(expect "in function mapcat first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(mapcat :not-a-function [1 2 3] [8 9 10]))))

;;; testing for the second precondition of mapcat
(expect "in function mapcat second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(mapcat reverse :not-a-collection [8 9 10]))))

;;; testing for the second precondition of mapcat with multiple collections
(expect "in function mapcat third argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(mapcat reverse [1 2 3] :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of concat with one arg
(expect "in function concat first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(concat :not-a-collection))))

;;; testing for the precondition of concat with multiple args
(expect "in function concat fourth argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(concat [1 2] [3 4] [18 22] :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of < breaks on first arg
(expect "in function < first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(< :not-a-number 31))))

;;; testing for the precondition of < breaks on second arg
(expect "in function < second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(< 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of > breaks on first arg
(expect "in function > first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(> :not-a-number 31))))

;;; testing for the precondition of > breaks on second arg
(expect "in function > second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(> 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of >= breaks on first arg
(expect "in function >= first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(>= :not-a-number 31))))

;;; testing for the precondition of >= breaks on second arg
(expect "in function >= second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(>= 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of <= breaks on first arg
(expect "in function <= first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(<= :not-a-number 31))))

;;; testing for the precondition of <= breaks on second arg
(expect "in function <= second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(<= 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of add-first
(expect "in function add-first first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(add-first :not-a-collection [1 2 3]))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for the precondition of add-last
(expect "in function add-last first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(add-last :not-a-collection [1 2 3]))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for contains-value?
(expect "in function contains-value? first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(contains-value? :not-a-collection 2))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for contains-key?
(expect "in function contains-key? first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(contains-key? :not-a-collection 2))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;;; testing for any?
(expect "in function any? first argument :not-a-predicate must be a function but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(any? :not-a-predicate [1 2 3]))))

;;; testing for any?
(expect "in function any? second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (my-run-and-catch-corefns '(any? odd? :not-a-collection))))


;################################################################################
;### A variety of other tests to see if they give us the correct msg-info-obj ###
;################################################################################

(expect [{:msg "in function ", :stylekey :reg, :length 12}
         {:msg "concat", :stylekey :arg, :length 6}
         {:msg " ", :stylekey :reg, :length 1}
         {:msg "second argument", :stylekey :reg, :length 15}
         {:msg " ", :stylekey :reg, :length 1}
         {:msg ":not-a-collection", :stylekey :arg, :length 17}
         {:msg " must be a ", :stylekey :reg, :length 11}
         {:msg "sequence", :stylekey :type, :length 8}
         {:msg " but is ", :stylekey :reg, :length 8}
         {:msg "a keyword", :stylekey :type, :length 9}]
        (my-run-and-catch-corefns '(doall (concat [:foo] :not-a-collection))))
