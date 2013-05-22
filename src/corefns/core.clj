(ns corefns.core
  (:use [clojure.core.incubator])
  ;(:require ;[trammel.provide :as provide]
    	    ;[trammel.core :as tramm]
    	    ;[clojure.core.contracts :as contracts])
  (:refer-clojure :exclude [map]));[map filter nth concat]))

;; to-do:
;; 1. remove references to contracts, trammel (from project.clj as well) - Done
;; 2. modify our type-checking functions to record the type (or the arg? or the message?)
;; 3. modify error--handling for asserts to check the recorded info
;; 4. rewrite messages similar to the standard ones (perhaps abstract over?)
;; 5. don't forget to clear the queue at the end (post-cond? or the end of pre-cond? or finally?)
;; 6. why didn't I think of this earlier? 

;; a global hashmap of recorded types/messages
(def seen-objects (atom {}))

(defn add-to-seen [binds] 
  "adds bindings to seen objects"
  (swap! seen-objects merge binds))

(defn empty-seen [] 
  "removes all bindings from seen objects"
  (swap! seen-objects {}))

;; A few function aliases to increase the readability of
;; error messages caused by failed assertions
(defn check-if-function? [x]
  (if (fn? x) true
  	      (do (add-to-seen {:check "function" 
  	      		        :class (class x)
  	      		        :value x})
  	      	   false)))   


(def is-collection? seqable?) ;; coll? does a wrong thing on strings and nil
(def is-number? number?)
;(def is-vector-or-list? #(or (vector? %) (list? %))) 


;; filter and map have the same checks. Should we abstract over this?
;; note that for filter the function must return a boolean, but there
;; is no way to check for it, is there? - Elena

(defn map [argument1 argument2]
  {:pre [(is-collection? argument2) (check-if-function? argument1)]}
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
		
;;;;; Functions for type-independent sequence handling ;;;;;;

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
