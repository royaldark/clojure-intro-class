(ns errors.dictionaries)

;; A dictionary of known types and their user-friendly representations
;; potentially we can have multiple dictionaries, depending on the level
(def type-dictionary {:java.lang.String "string"
                      :java.lang.Number "number"
		      :clojure.lang.Keyword "keyword"
		      :java.lang.Boolean "boolean"
		      ;; I think this is better for new students to lump all numbers together
		      :java.lang.Long "number"
		      :java.lang.Integer "number"
		      :java.lang.Double "number"
		      :java.lang.Float "number"
		      :java.lang.Short  "number"
		      ;; perhaps add big ints and such
		      :java.lang.Character "symbol" ;; simplifying things for a new student
		      :clojure.lang.Symbol "symbol"
		      ;; to short-cut processing of error messages for
		      ;; "Don't know how to create a sequence from ..."
		      :clojure.lang.ISeq "sequence"
		      :ISeq "sequence"})

;; A string representation of a type t not listed in the type-dictionary
(defn unknown-type-string [t]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  ;; collections - must go before functions since some seqs implement the IFn interface
  (if (isa? (resolve (symbol t)) clojure.lang.ISeq) "sequence"
      (if (isa? (resolve (symbol t)) clojure.lang.IPersistentCollection) "collection" ;; the same test as in coll?
	  ;; functions
	  (if (isa? (resolve (symbol t)) clojure.lang.IFn) "function"
	      ;; if all else fails: 
	      (str "unrecognized type " t)))))

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
			:replace (replace-types #(str "Attempted to use " (nth %1 0) ", but " (nth %1 1) " was expected."))}
		       {:class IllegalArgumentException
			:match #"Don't know how to create (.*) from: (.*)"
			:replace (replace-types #(str "Don't know how to create " (nth %1 0) " from " (nth %1 1)))}])


