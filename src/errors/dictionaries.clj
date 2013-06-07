(ns errors.dictionaries
  (:use [corefns.core]
  	[errors.messageobj]))

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
		      ;; to short-cut processing of error messages for
		      ;; "Don't know how to create a sequence from ..."
		      :clojure.lang.ISeq "a sequence"
		      :ISeq "a sequence"
		      ;; Refs come up in turtle graphics
		      :clojure.lang.Ref "a mutable object"
		      ;; regular expressions wouldn't make sense to beginners,
		      ;; but it's better to recognize their types for easier
		      ;; help with diagnostics
		      :java.util.regex.Pattern "a regular expression pattern"
		      :java.util.regex.Matcher "a regular expression matcher"
		      ;; also not something beginners would know,
		      ;; but useful for understanding errors
		      :clojure.lang.Symbol "a symbol"
		      :clojure.lang.IPersistentStack "an object that behaves as a stack (such as a vector or a list)"
		      ;; assoc works on maps and vectors:
		      :clojure.lang.Associative "a map or a vector"
		      :clojure.lang.Reversible "a vector or a sorted-map"
		      :clojure.lang.Sorted "a collection stored in a sorted manner (such as sorted-map or sorted-set)"
		      :clojure.lang.Sequential "a sequential collection (such as a vector or a list)"
		      ;; got this in a seesaw error message. Not sure what other types are "Named"
		      ;; source: https://groups.google.com/forum/?fromgroups#!topic/clojure/rd-MDXvn3q8
		      :clojure.lang.Named "a keyword or a symbol"})
		      
		      
;; matching type interfaces to beginner-friendly names. 
;; Note: since a type may implement more than one interface, 
;; the order is essential. The lookup is done in order, so
;; the first match is returned. 
;; That's why it's a vector, not a hashmap. 
;; USE CAUTION WHEN ADDING NEW TYPES! 
(def general-types [[Number "a number"]
		    [clojure.lang.IPersistentVector "a vector"]
		    [clojure.lang.IPersistentList "a list"]
		    [clojure.lang.IPersistentSet "a set"]
		    [clojure.lang.IPersistentMap "a map"]
		    [clojure.lang.ISeq "a sequence"]
		    ;; collections - must go before functions since some collections 
		    ;; implement the IFn interface
		    [clojure.lang.IPersistentCollection "a collection"]
		    [clojure.lang.IFn "a function"]])
		      
;; The best approximation of a type t not listed in the type-dictionary (as a string)
(defn- best-approximation [t]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  (let [attempt (resolve (symbol t))
        type (if attempt attempt (resolve (symbol (str "clojure.lang." t)))) ;; may need to add clojure.lang. for some types
        matched-type (if type (first (filter #(isa? type (first %)) general-types)))]
        (if matched-type (second matched-type) (str "unrecognized type " type))))

(defn- get-type [t]
  "returns a user-friendly representation of a type if it exists in the type-dictionary,
   or its default representation as an unknown type"
  ((keyword t) type-dictionary (best-approximation t)))

(defn- replace-types [f]
   "returns a function that maps get-type over a list of matches"
  (fn [matches] (f (map get-type (rest matches)))))

;; hashmap of internal function names and their user-friendly versions
(def predefined-names {:_PLUS_ "+"  :_ "-" :_SLASH_ "/" }) 

(defn- lookup-funct-name [fname]
  "looks up pre-defined function names, such as _PLUS_. If not found,
  returns the original"
  (let [lookup ((keyword fname) predefined-names)]
    (if lookup lookup (-> fname
    		    	  (clojure.string/replace #"_QMARK_" "?")
    		    	  (clojure.string/replace #"_BANG_" "!")
    		    	  (clojure.string/replace #"_EQ_" "=")
    		    	  (clojure.string/replace #"_LT_" "<")
    		    	  (clojure.string/replace #"_GT_" ">")
    		    	  (clojure.string/replace #"_STAR_" "*")))))

(defn- get-function-name [fname]
  "extract a function name from a qualified name"
  (if-let [matching-name (lookup-funct-name (nth (re-matches #"(.*)\$(.*)" fname) 2))]
  	  (if (or (= matching-name "fn") (re-matches #"fn_(.*)" matching-name)) 
               "anonymous function" matching-name)
  	  fname))

(defn- get-macro-name [mname]
  "extract a macro name from a qualified name"
  (nth (re-matches #"(.*)/(.*)" mname) 2))

(defn- pretty-print-value [v c type]
   "returns a pretty-printed value v based on its class, handles various messy cases"
   ; strings are printed in double quotes:
   (if (string? v) (str "\"" v "\"")
     (if (= type "a function") 
       ; extract a function from the class c (easier than from v):
       (get-function-name c)
       v)))

(defn arg-str [n]
  (case n 
    1 "First argument" 
    2 "Second argument" 
    3 "Third argument" 
    4 "Fourth argument" 
    5 "Fifth argument" 
    (str n "th argument ")))  	  

(defn process-asserts-obj [n] 
  "Returns a message object generated for an assert failure based on the 
  global seen-objects hashmap, clears the hashmap"
  ;; and perhaps need manual error handling, in case the seen-object is empty
   (let [t (:check @seen-objects)
         c (.getName (:class @seen-objects))
         c-type (get-type c)
         v (:value @seen-objects)
         v-print (pretty-print-value v c c-type)
         arg (arg-str (if n (Integer. n) (:arg-num @seen-objects)))]
         (println t " " c " " v)
   (empty-seen) ; empty the seen-objects hashmap 
   (make-preobj-hashes 
   	   [[arg] [" "] [v-print :arg] 
   	   [" must be a "] [t :type] [" but is "] [c-type :type]])))	

(def error-dictionary [{:class AssertionError
		        :match #"Assert failed: \((.*) argument(.*)\)"  
			:make-preobj (fn [matches] (process-asserts-obj (nth matches 2)))}
		       {:class AssertionError
		        :match #"Assert failed: \((.*)\)"  
			:make-preobj (fn [matches] (process-asserts-obj nil))}
		       {:class ClassCastException
			:match #"(.*) cannot be cast to (.*)"
			;:replace (replace-types #(str "Attempted to use " (nth %1 0) ", but " (nth %1 1) " was expected."))
			:make-preobj (fn [matches] (make-preobj-hashes [["Attempted to use "] 
                                                   [(get-type (nth matches 1)) :type] [", but "] 
                                                   [(get-type (nth matches 2)) :type] [" was expected."]]))} 
		       {:class IllegalArgumentException
			:match #"Don't know how to create (.*) from: (.*)"
			;:replace (replace-types #(str "Don't know how to create " (nth %1 0) " from " (nth %1 1)))
			:make-preobj (fn [matches] (make-preobj-hashes [["Don't know how to create "] 
                                                   [(get-type (nth matches 1)) :type] 
                                                   [" from "] [(get-type (nth matches 2)) :type]]))}
                       {:class IllegalArgumentException
			:match #"loop requires an even number of forms in binding vector in (.*):(.*)"
			:make-preobj (fn [matches] (make-preobj-hashes [["You need an even number of elements for a loop on line "]
					[(nth matches 2)] [" in the file "]  [(nth matches 1)]]))}
		       {:class IllegalArgumentException
		        :match #"(.*) requires a vector for its binding in (.*):(.*)"
		        :make-preobj (fn [matches] (make-preobj-hashes [["When declaring a "] [(nth matches 1)] 
		        		[", you need to pass it a vector of arguments. Line "]
		        		[(nth matches 3)] [" in the file "]  [(nth matches 2)]]))}
		       {:class IllegalArgumentException
		        :match #"(.*) not supported on type: (.*)"
			:make-preobj (fn [matches] (make-preobj-hashes [["Function "] [(nth matches 1) :arg] 
					[" does not allow "] [(get-type (nth matches 2)) :type] [" as an argument"]]))}
		       {:class IndexOutOfBoundsException 
			:match #"(\d+)"
			;:replace "An index in a sequence is out of bounds. The index is: $1"
			:make-preobj (fn [matches] (make-preobj-hashes 
					[["An index in a sequence is out of bounds."] 			
					["The index is:"] 
					[(nth matches 0) :arg]]))}
		       {:class IndexOutOfBoundsException
		        :match #"" ; an empty message
		        ;:replace "An index in a sequence is out of bounds"
			:make-preobj (fn [matches] (make-preobj-hashes [["An index in a sequence is out of bounds"]]))}
		       {:class clojure.lang.ArityException
		        :match #"Wrong number of args \((.*)\) passed to: (.*)"
		        :make-preobj (fn [matches] 
		        		(let [fstr (get-function-name (nth matches 2))
		        		      funstr (if (= fstr "anonymous function")
		        		     	     	 "an "
		        		     	     	 (str "a function "))]
		        		(make-preobj-hashes [["Wrong number of arguments ("] 
		        		[(nth matches 1)] [")  passed to "] [funstr]
		        		[fstr :arg]])))}
		       {:class NullPointerException  
			:match #"(.+)" ; for some reason (.*) matches twice. Since we know there is at least one symbol, + is fine
			;:replace "An attempt to access a non-existing object: $1 \n(NullPointerException)"
			:make-preobj (fn [matches] (make-preobj-hashes [["An attempt to access a non-existing object: "]
					[(nth matches 1) :arg] ["\n(NullPointerException)"]]))}
		       {:class NullPointerException
		        :match  #""
		        ;:replace "An attempt to access a non-existing object \n(NullPointerException)"
			:make-preobj (fn [matches] (make-preobj-hashes [["An attempt to access a non-existing object. \n(NullPointerException)"]]))}
		       {:class UnsupportedOperationException
		        :match #"(.*) not supported on this type: (.*)"
		        ;:replace #(str  "Function " (nth % 1) " does not allow " (get-type (nth % 2)) " as an argument")
			:make-preobj (fn [matches] (make-preobj-hashes [["Function "] [(nth matches 1) :arg] 
					[" does not allow "] [(get-type (nth matches 2)) :type] [" as an argument"]]))}
		       {:class java.lang.Exception
		        :match #"Unsupported binding form: (.*)"
		        :make-preobj (fn [matches] (make-preobj-hashes [["You cannot use "] [(nth matches 1) :arg]
		        		[" as a variable."]]))}
		        ;; Compilation errors 
		       {:class clojure.lang.Compiler$CompilerException
		        :match #"(.+): Too many arguments to (.+), compiling:(.+)"
		        ;:replace "Compilation error: too many arguments to $2 while compiling $3"
			:make-preobj (fn [matches] (make-preobj-hashes [["Compilation error: Too many arguments to"]
					[(nth matches 2) :arg] [", while compiling "]
					[(nth matches 3) :arg]]))}
		       {:class clojure.lang.Compiler$CompilerException
		        :match #"(.+): EOF while reading, starting at line (.+), compiling:(.+)"
		        :replace "Compilation error: end of file, starting at line $2, while compiling $3.\nProbabbly a non-closing parentheses or bracket."
			:make-preobj make-mock-preobj}
		        {:class clojure.lang.Compiler$CompilerException
		        :match #"(.+): Unmatched delimiter: (.+), compiling:(.+)"
		        :replace "Compilation error: a closing $2 without a matching opening one while compiling $3."
			:make-preobj make-mock-preobj}
		        {:class clojure.lang.Compiler$CompilerException
		        :match #"(.+): Unable to resolve symbol: (.+) in this context, compiling:\((.+)\)"
		        ;:replace "Compilation error: name $2 is undefined in this context, while compiling $3."
			:make-preobj (fn [matches] (make-preobj-hashes [["Compilation error: "] ["name "] 
					 [(nth matches 2) :arg] [" is undefined, while compiling "] 
					 [(nth matches 3) :arg]]))}
			{:class clojure.lang.Compiler$CompilerException
			 :match #"(.*) Mismatched argument count to recur, expected: (.*) args, got: (.*), compiling:(.*)"
			 :make-preobj (fn [matches] (make-preobj-hashes [["Compilation error: this loop is supposed to take "]
			 		 [(nth matches 3)] [" arguments, but you are passing "] [(nth matches 2)]
			 		 [", while compiling "]  [(nth matches 4)]]))}
			{:class clojure.lang.Compiler$CompilerException
			:match #"(.+): Can't take value of a macro: (.+), compiling:\((.+)\)"
			:make-preobj (fn [matches] (make-preobj-hashes [["Compilation error: "] 
					[(get-macro-name (nth matches 2)) :arg] 
					[" is a macro, cannot be passed to a function, while compiling "]
					[(nth matches 3)]]))}])

