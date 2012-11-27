(ns corefns.core
(:refer-clojure :exclude [map]))

; A few function aliases to increase the readability of
; error messages caused by failed assertions
(def is-function? fn?)
(def is-collection? coll?)

(defn map [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/map argument1 argument2))
