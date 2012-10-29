(ns corefns.core)

(defn map [f coll]
  {:pre [(coll? coll) (fn? f)]}
  (clojure.core/map f coll))
