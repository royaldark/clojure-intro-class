(ns errors.dictionaries)

;; A dictionary of known types and their user-friendly representations
(def type-dictionary {:java.lang.String "string"
                      :java.lang.Number "number"
		      :clojure.lang.Symbol "symbol"})

;; A string representation of a type t not listed in the type-dictionary
(defn unknown-type-string [t]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  ;; functions
  ;; a code pattern: (instance? (resolve (symbol t)) 5)
  ;; (if (instance? clojure.lang.IFn (resolve (symbol t))) "function"
  (if (ifn? (resolve (symbol t))) "function" ;; fails for some reason; shouldn't
  ;; collections - might want to move to earlier since some seqs implement the IFn interface

  ;; if all else fails: 
  (str "unrecognized type " t)))

;; The best approximation of a type we can get if it's not listed in the type-dictionary
(defn other-type [t]
  "returns the best approximation of a type we can get if it's not listed in the type-dictionary"
  (unknown-type-string t))

(defn get-type [t]
  "returns a user-friendly representation of a type if it exists in the type-dictionary,
   or its default representation as an unknown type"
  ((keyword t) type-dictionary (other-type t)))

(defn replace-types [f]
  (fn [matches] (f (map get-type (rest matches)))))

(def error-dictionary [{:class ClassCastException
                   :match #"(.*) cannot be cast to (.*)"
		   :replace (replace-types #(str "Attempted to use " (nth %1 0) ", but " (nth %1 1) " was expected."))}])


