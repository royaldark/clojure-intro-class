(ns corefns.core
(:refer-clojure :exclude [map filter]))

; A few function aliases to increase the readability of
; error messages caused by failed assertions
(def is-function? fn?)
(def is-collection? coll?)
;(def is-vector-or-list? (or (vector? argument2) (list? argument2)))

;; filter and map have the same checks. Should we abstract over this?
;; note that for filter the function must return a boolean, but there
;; is no way to check for it, is there? - Elena

(defn map [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/map argument1 argument2))

(defn filter [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/filter argument1 argument2))

;; List or Vector
;(defn add-first [argument1 argument2]	 	
;   {:pre [(or (vector? argument2) (list? argument2)) ]}
;   (clojure.core/reverse argument2 (clojure.core/conj (clojure.core/reverse argument2) argument1))) 
;; need conj, into. Careful: there may be different cases. Can we have a complex pre-cond (with cases)?
