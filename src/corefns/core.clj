(ns corefns.core
  (:use [clojure.core.incubator])
  (:require [trammel.provide :as provide]
    	    [trammel.core :as tramm]
    	    [clojure.core.contracts :as contracts])
  (:refer-clojure :exclude [map]));[map filter nth concat]))

;; to-do:
;; 1. remove references to contracts, trammel (from project.clj as well)
;; 2. modify our type-checking functions to record the type (or the arg? or the message?)
;; 3. modify error--handling for asserts to check the recorded info
;; 4. rewrite messages similar to the standard ones (perhaps abstract over?)
;; 5. don't forget to clear the queue at the end (post-cond? or the end of pre-cond? or finally?)
;; 6. why didn't I think of this earlier? 

;; A few function aliases to increase the readability of
;; error messages caused by failed assertions
(def is-function? fn?)
(def is-collection? seqable?) ;; coll? does a wrong thing on strings and nil
(def is-number? number?)
;(def is-vector-or-list? #(or (vector? %) (list? %))) 

; this doesn't work, as a fully qualified name or as just pos? 
; I emailed Michael Fogus about it
(provide/contracts 
	[clojure.core/pos? "the argument is not a number"
	[n] [(number? n)]])

;; filter and map have the same checks. Should we abstract over this?
;; note that for filter the function must return a boolean, but there
;; is no way to check for it, is there? - Elena

(defn map [argument1 argument2]
  {:pre [(do (println argument2) true) (is-collection? argument2) (is-function? argument1)]}
  (clojure.core/map argument1 argument2))

;(defn filter [argument1 argument2]
;  {:pre [(is-collection? argument2) (is-function? argument1)]}
;  (clojure.core/filter argument1 argument2))

;(defn nth [argument1 argument2]
  ;{:pre [(is-collection? argument1) (is-number? argument2)]}
  ;(clojure.core/nth  argument1 argument2))

;(defn nth [argument1 argument2]
;  {:pre [(is-vector-or-list? argument1) (is-number? argument2)]}
;  (clojure.core/nth  argument1 argument2))

;(defn concat [argument1 argument2]
;  {:pre [(is-collection? argument1) (is-collection? argument2)]}
;  (clojure.core/concat  argument1 argument2))

;; need conj, into. Careful: there may be different cases. Can we have a complex pre-cond (with cases)?

(comment
(tramm/defcontract second-arg-sequence
  		"Second argument must be a sequence"
     		[f coll] ;(do (println (class coll)) true) 
     			[(seqable? coll)])

(tramm/defcontract first-arg-function
  		"First argument must be a function"
     		[f coll] [(do (println f " " coll) true) 
     			(fn? f)])

(tramm/defcontract
  doubler-defcontract
  "Test"
  [n] [(do (println (class n)) true) (number? n) => (= % (* 2 n))]
  [x y] [(do (println (class x) " " (class y)) true) (every? number? [x y]) ])

(println (= 10 ((partial doubler-defcontract #(* 2 (+ %1 %2))) 3 3)))

(def testf (partial doubler-defcontract #(* 2 (+ %1 %2))))

(println (testf 2 3))

(tramm/defcontract is-function
  		"First argument must be a function"
     		[f] [(do (println (class f)) true) 
     			(fn? f)])


;(def myf (partial is-function #(%1 %2)))

;(println ((myf dec) 2))

;; This doesn't quite do what we want since map can take a variable number of arguments.
;; Plus we don't want to overwrite map, we want to add constraints to the existing map
;; if we can. 
(def mymymap (partial second-arg-sequence #(println %1 %2)))
	;(contracts/with-constraints
	;	map 
		;second-arg-sequence
	;	first-arg-function
	;	))
		;map))
)
		
;; Functions for type-independent sequence handling
;; Need to define predicates on them as well
(defn add-first [argument1 argument2]
  ;{:pre [(is-collection? argument1)]}
  (cons argument2 argument1))

;(provide/contracts
;  [add-first "the first argument of add-first must be a collection or a sequence"
;   [coll elt] [(seqable? coll)]])

(defn add-last [argument1 argument2]
  ;{:pre [(is-collection? argument1)]}
  (doall (concat argument1 [argument2])))


;; user-friendly versions of confusing functions
(defn contains-value? [coll x]
	(let [values (if (map? coll) (vals coll) coll)]
		(not (every? #(not= x %) values))))

(def contains-key? contains?)
		
;; more content tests 
(defn any? [pred coll] (not (not-any? pred coll))) ; yes, I know :-(
(defn some? [pred coll] (not (not-any? pred coll)))

;; String functions
