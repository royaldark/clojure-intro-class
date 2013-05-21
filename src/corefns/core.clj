(ns corefns.core
  (:use [clojure.core.incubator])
  (:require [trammel.provide :as provide]
    	    [trammel.core :as tramm]
    	    [clojure.core.contracts :as contracts]))
  ;(:refer-clojure :exclude [map filter nth]));[map filter nth concat]))


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

;(defn map [argument1 argument2]
;  {:pre [(is-collection? argument2) (is-function? argument1)]}
;  (clojure.core/map argument1 argument2))

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

(tramm/defcontract second-arg-sequence
  		"Second argument must be a sequence"
     		[f coll] [(do (println (class coll)) true) (seqable? coll)])

(tramm/defcontract first-arg-function
  		"First argument must be a function"
     		[f _] [(is-function? f)])

;; testing contracts
(def map 
	(contracts/with-constraints 
		map 
		second-arg-sequence
		first-arg-function))
		;map))

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
