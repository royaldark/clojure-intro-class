(ns corefns.core
  (:use [clojure.core.incubator]))
  ;(:refer-clojure :exclude [map filter nth]));[map filter nth concat]))


;; A few function aliases to increase the readability of
;; error messages caused by failed assertions
(def is-function? fn?)
(def is-collection? seqable?) ;; coll? does a wrong thing on strings and nil
(def is-number? number?)
;(def is-vector-or-list? #(or (vector? %) (list? %))) 


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
;  {:pre [(is-vector-or-list? argument1) (is-number? argument2)]}
;  (clojure.core/nth  argument1 argument2))

;(defn concat [argument1 argument2]
;  {:pre [(is-collection? argument1) (is-collection? argument2)]}
;  (clojure.core/concat  argument1 argument2))

;; need conj, into. Careful: there may be different cases. Can we have a complex pre-cond (with cases)?

;; Functions for type-independent sequence handling
;; Need to define predicates on them as well
(defn add-first [argument1 argument2]
  ;{:pre [(is-collection? argument1)]}
  (cons argument2 argument1))

(defn add-last [argument1 argument2]
  ;{:pre [(is-collection? argument1)]}
  (concat argument1 [argument2]))

;; user-friendly versions of confusing functions
; works on strings, unlike contains?
(defn contains-value? [coll x]
	(let [values (if (map? coll) (vals coll) coll)]
		(not (every? #(not= x %) values))))

(def contains-key? contains?)
		
;; more content tests 
(defn any? [pred coll] (not (not-any? pred coll))) ; yes, I know :-(
(defn some? [pred coll] (not (not-any? pred coll)))

;; String functions

