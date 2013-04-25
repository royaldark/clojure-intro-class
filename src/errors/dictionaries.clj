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
		      :clojure.lang.BigInt "a number"
		      ;; perhaps add big ints and such
		      :java.lang.Character "a character" ;; switched back from a symbol
		      :clojure.lang.Symbol "a character"
		      ;; to short-cut processing of error messages for
		      ;; "Don't know how to create a sequence from ..."
		      :clojure.lang.ISeq "a sequence"
		      :ISeq "a sequence"
		      ;; Refs come up in turtle graphics
		      :clojure.lang.Ref "a mutable object"})
		      
;; matching type interfaces to beginner-friendly names. 
;; Note: since a type may implement more than one interface, 
;; the order is essential. The lookup is done in order, so
;; the first match is returned. 
;; That's why it's a vector, not a hashmap. 
;; USE CAUTION WHEN ADDING NEW TYPES! 
(def general-types [[clojure.lang.IPersistentVector "a vector"]
		    [clojure.lang.IPersistentList "a list"]
		    [clojure.lang.IPersistentSet "a set"]
		    [clojure.lang.IPersistentMap "a map"]
		    [clojure.lang.ISeq "a sequence"]
		    ;; collections - must go before functions since some collections 
		    ;; implement the IFn interface
		    [clojure.lang.IPersistentCollection "a collection"]
		    [clojure.lang.IFn "a function"]])
		      
;; The best approximation of a type t not listed in the type-dictionary (as a string)
(defn best-approximation [t]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  (let [attempt (resolve (symbol t))
        type (if attempt attempt (resolve (symbol (str "clojure.lang." t)))) ;; may need to add clojure.lang. for some types
        matched-type (if type (first (filter #(isa? type (first %)) general-types)))]
        (if matched-type (second matched-type) (str "unrecognized type " type))))

(defn get-type [t]
  "returns a user-friendly representation of a type if it exists in the type-dictionary,
   or its default representation as an unknown type"
  ((keyword t) type-dictionary (best-approximation t)))

(defn replace-types [f]
   "returns a function that maps get-type over a list of matches"
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
		        :replace #(str  "Function " (nth % 1) " does not allow " (get-type (nth % 2)) " as an argument")}
		       {:class UnsupportedOperationException
		        :match #"(.*) not supported on this type: (.*)"
		        :replace #(str  "Function " (nth % 1) " does not allow " (get-type (nth % 2)) " as an argument")}])

