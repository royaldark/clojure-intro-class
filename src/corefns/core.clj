(ns corefns.core
(:refer-clojure :exclude [map filter]))

; A few function aliases to increase the readability of
; error messages caused by failed assertions
(def is-function? fn?)
(def is-collection? coll?)

;; filter and map have the same checks. Should we abstract over this?
;; note that for filter the function must return a boolean, but there
;; is no way to check for it, is there? - Elena

(defn map [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/map argument1 argument2))

(defn filter [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/filter argument1 argument2))