(ns errors.dictionaries)

;; A dictionary of known types and their user-friendly representations
;; potentially we can have multiple dictionaries, depending on the level
(def type-dictionary {:java.lang.String "a string"
                      :java.lang.Number "a number"
		      :clojure.lang.Keyword "a keyword"
		      :java.lang.Boolean "a boolean"
		      ;; I think this is better for new students to lump all numbers together
		      :java.lang.Long "a number"
		      :java.lang.Integer "a number"
		      :java.lang.Double "a number"
		      :java.lang.Float "a number"
		      :java.lang.Short  "a number"
		      ;; perhaps add big ints and such
		      :java.lang.Character "a symbol" ;; simplifying things for a new student
		      :clojure.lang.Symbol "a symbol"
		      ;; to short-cut processing of error messages for
		      ;; "Don't know how to create a sequence from ..."
		      :clojure.lang.ISeq "a sequence"
		      :ISeq "a sequence"
		      ;; Refs come up in turtle graphics
		      :clojure.lang.Ref "a mutable object"})

;; A string representation of a type t not listed in the type-dictionary
(defn best-approximation [type]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  ;; collections - must go before functions since some seqs implement the IFn interface
  (let [t (resolve (symbol type))]
    (if (isa? t clojure.lang.ISeq) "a sequence"
	(if (isa? t clojure.lang.IPersistentCollection) "a collection" ;; the same test as in coll?
	  ;; functions
	  (if (isa? t clojure.lang.IFn) "a function"
	      ;; if all else fails: 
	      (str "unrecognized type " t))))))

;; The best approximation of a type we can get if it's not listed in the type-dictionary
(defn other-type [t]
  "returns the best approximation of a type we can get if it's not listed in the type-dictionary"
  (best-approximation t))

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
			:replace (replace-types #(str "Don't know how to create " (nth %1 0) " from " (nth %1 1)))}
		       {:class IndexOutOfBoundsException 
			:match #"(\d+)"
			:replace "An index in a sequence is out of bounds. The index is: $1"}
		       {:class IndexOutOfBoundsException
		        :match #"" ; the message may be a nil
		        :replace "An index in a sequence is out of bounds"}
		       {:class NullPointerException  
			:match #"(.+)" ; for some reason (.*) matches twice. Since we know there is at least one symbol, + is fine
			:replace "An attempt to access a non-existing object: $1 \n (NullPointerException)"}
		       {:class NullPointerException
		        :match  #""
		        :replace "An attempt to access a non-existing object \n (NullPointerException)"}
		       {:class IllegalArgumentException
		        :match #"(.*) not supported on type: (.*)"
		        :replace (replace-types #(str "Function " (nth %1 0) " does not allow " (nth %1 1) " as an argument"))}])

