(ns errors.dictionaries_test
  (:require [errors.dictionaries :refer :all]
            [expectations :refer :all]
            [corefns.corefns :refer :all]
            [errors.prettify_exception :refer :all]
            [errors.messageobj :refer :all]
            [errors.exceptions :refer :all]))

;#####################################
;### testing for get-function-name ###
;#####################################

(expect "days" (get-function-name "happy$days"))
(expect "days" (get-function-name "happy/days"))
(expect "blahBlahBlahBlahNuthin'" (get-function-name "blahBlahBlahBlahNuthin'"))

;#######################################################################################
;### Checking if functions realize when too many or too little args are being passed ###
;#######################################################################################

(expect "Wrong number of arguments (3) passed to a function count"
        (get-all-text
         (run-and-catch-dictionaries '(count [] [] []))))

(expect "Wrong number of arguments (3) passed to a function into"
        (get-all-text
         (run-and-catch-dictionaries '(into [] '(5 10 15) [2 4 6]))))

(expect "Wrong number of arguments (0) passed to a function zero?, while compiling "
        (get-all-text (butlast (run-and-catch-dictionaries '(zero?)))))

(expect "Wrong number of arguments (3) passed to a function even?"
        (get-all-text (run-and-catch-dictionaries '(even? 3 6 1))))

(expect "Wrong number of arguments (0) passed to a function reduce"
        (get-all-text (run-and-catch-dictionaries '(reduce))))

(expect "Wrong number of arguments (0) passed to a function nth"
        (get-all-text (run-and-catch-dictionaries '(nth))))

(expect "Wrong number of arguments (0) passed to a function filter"
        (get-all-text (run-and-catch-dictionaries '(filter))))

(expect "Wrong number of arguments (3) passed to a function add-first"
        (get-all-text (run-and-catch-dictionaries '(add-first [1 2 3] 0 0))))

(expect "Wrong number of arguments (3) passed to a function add-last"
        (get-all-text (run-and-catch-dictionaries '(add-last [:a :b :c] :d :e))))

(expect "Wrong number of arguments (3) passed to a function contains-key?"
        (get-all-text (run-and-catch-dictionaries '(contains-key? {:a 1 :b 2 :c 3} 1 true))))

(expect "Wrong number of arguments (3) passed to a function contains-value?"
        (get-all-text (run-and-catch-dictionaries '(contains-value? {:a 1 :b 2 :c 3} :c true))))

(expect "Wrong number of arguments (3) passed to a function any?"
        (get-all-text (run-and-catch-dictionaries '(any? odd? [1 2 3] [1 2 3]))))

;###############################################
;### Testing for check-if-anonymous-function ###
;###############################################

(expect "anonymous function" (check-if-anonymous-function "fn"))
(expect "anonymous function" (check-if-anonymous-function "fn_test"))
(expect "anonymous function" (check-if-anonymous-function "fn_"))
(expect "random_function" (check-if-anonymous-function "random_function"))

;######################################
;### Testing for compilation errors ###
;######################################

(expect "Wrong number of arguments (0) passed to a function odd?"
        (get-all-text (run-and-catch-dictionaries '(odd?))))

(expect "Wrong number of arguments (2) passed to a function odd?"
        (get-all-text (run-and-catch-dictionaries '(odd? 5 6))))

(expect #"Compilation error: too many arguments to def, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(def my-var 5 6))))

(expect #"Compilation error: too few arguments to def, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(def))))

;; thinks that the EOF is in our expect test instead of the test itself
;(expect #"Compilation error: end of file, starting at line (.+), while compiling (.+)"
;         "Probably a non-closing parentheses or bracket."
;        (get-all-text (run-and-catch-dictionaries '(+ 1 2 )))

;; thinks that the unmatched delimiter is part of the expect test not the test itself
;(expect #"Compilation error: there is an unmatched delimiter ), while compiling (.+)"
;        (get-all-text (run-and-catch-dictionaries 'defn my-string [x] (str x)))))

(expect #"Compilation error: name banana is undefined, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(banana 5 6))))

;;test spot for :compiler-exception-even-number-of-forms-needed

(expect #"Compilation error: this recur is supposed to take 0 arguments, but you are passing 1, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(recur (inc 1)))))

(expect #"Compilation error: def must be followed by a name. Compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(def 4 (+ 2 2)))))

(expect #"Compilation error: recur can only occur as a tail call, meaning no operations can be done after its return. Compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(defn inc-nums [x] ((recur (inc x)) (loop [x x]))))))

;;test spot for :compiler-exception-must-recur-to-function-or-loop

(expect #"Compilation error: loop is a macro, cannot be passed to a function, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(defn my-happy [x] loop [x x]))))

(expect #"Compilation error: loop requires an even number of forms in binding vector, while compiling (.+)"
        (get-all-text (run-and-catch-dictionaries '(defn s [s] (loop [s])))))

;#########################################################
;### Other testing for new exceptions and other things ###
;#########################################################

(expect #"when-let requires exactly 2 forms in binding vector in (.+)"
        (get-all-text (run-and-catch-dictionaries '(when-let [num1 1 num2 2] "hello"))))

;(expect "in function into second argument [[2] [4 5]] must be either a hashmap, or a collection of vectors or hashmaps of length 2, but is a vector"
;        (get-all-text (run-and-catch-corefns '(into {} [[2] [4 5]]))))
