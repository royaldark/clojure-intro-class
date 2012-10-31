(ns corefns.core
(:refer-clojure :exclude [map]))

(defn is-function? [x]
(fn? x))

(defn is-collection? [x]
(coll? x))

(defn map [argument1 argument2]
  {:pre [(is-collection? argument2) (is-function? argument1)]}
  (clojure.core/map argument1 argument2))
