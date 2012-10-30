(ns corefns.core
(:refer-clojure :exclude [map]))

(defn map [f coll]
  {:pre [(coll? coll) (fn? f)]}
  (clojure.core/map f coll))
