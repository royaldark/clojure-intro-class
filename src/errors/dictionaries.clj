(ns errors.dictionaries
  (:use [corefns.corefns]
        [errors.messageobj]
        [corefns.failed_asserts_info]))

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
		                  ;; This is here because of shuffle. It's not ideal, too similar to Sequential
		                  :java.util.Collection " a traversable collection (such as a vector, list, or set)" ; not sure if this makes sense in relation to the previous one
		                  ;; got this in a seesaw error message. Not sure what other types are "Named"
		                  ;; source: https://groups.google.com/forum/?fromgroups#!topic/clojure/rd-MDXvn3q8
		                  :clojure.lang.Named "a keyword or a symbol"
		                  :clojure.lang.nil "nil"})


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
;;; best-approximation: type -> string
(defn best-approximation [t]
  "returns a string representation of a type t not listed in the type-dictionary for user-friendly error messages"
  (let [attempt (resolve (symbol t))
        type (if attempt attempt (clojure.lang.RT/loadClassForName (str "clojure.lang." t))) ;; may need to add clojure.lang. for some types.
        matched-type (if type (first (filter #(isa? type (first %)) general-types)))]
    (if matched-type (second matched-type) (str "unrecognized type " t))))

;;; get-type: type -> string
(defn get-type [t]
  "returns a user-friendly representation of a type if it exists in the type-dictionary,
	or its default representation as an unknown type"
  ((keyword t) type-dictionary (best-approximation t)))

;; hashmap of internal function names and their user-friendly versions
(def predefined-names {:_PLUS_ "+"  :_ "-" :_SLASH_ "/" })

;;; lookup-funct-name: predefined function name -> string
(defn lookup-funct-name [fname]
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

;;; check-if-anonymous-function: string -> string
(defn check-if-anonymous-function [fname]
  (if (or (= fname "fn") (re-matches #"fn_(.*)" fname))
      "anonymous function" fname))

;;; get-match-name: string -> string
(defn get-match-name [fname]
  "extract a function name from a qualified name"
  (let [m (nth (re-matches #"(.*)\$(.*)" fname) 2)
        matched (if m m (nth (re-matches #"(.*)/(.*)" fname) 2))]
    (if matched
      (check-if-anonymous-function (lookup-funct-name matched))
      fname)))

;;; remove-inliner: string -> string
(defn- remove-inliner [fname]
  "If fname ends with inliner this will return everything before it"
  (let [match (nth (re-matches #"(.*)--inliner" fname) 1)]
    (if match match fname)))

;;; get-function-name: string -> string
(defn get-function-name [fname]
  (remove-inliner (get-match-name fname)))

;;; get-macro-name: string -> string
(defn get-macro-name [mname]
  "extract a macro name from a qualified name"
    (nth (re-matches #"(.*)/(.*)" mname) 2))

;;; pretty-print-value: anything, string, string -> string
(defn pretty-print-value [value fname type]
  "returns a pretty-printed value based on its class, handles various messy cases"
    ; strings are printed in double quotes:
  (if (string? value) (str "\"" value "\"")
      (if (nil? value) "nil"
          (if (= type "a function")
            ; extract a function from the class fname (easier than from value):
            (get-function-name fname)
            (str value)))))

;;; arg-str: number -> string
(defn arg-str [n]
  (case n
    1 "first argument"
    2 "second argument"
    3 "third argument"
    4 "fourth argument"
    5 "fifth argument"
    (str n "th argument")))

;;; process-asserts-obj: string or nil -> string
(defn process-asserts-obj [n]
  "Returns a msg-info-obj generated for an assert failure based on the
	global seen-failed-asserts hashmap, clears the hashmap"
  ;; and perhaps need manual error handling, in case the seen-object is empty
  (let [t (:check @seen-failed-asserts)
        cl (:class @seen-failed-asserts)
        c (if cl (.getName cl) nil)
        fname (:fname @seen-failed-asserts)
        c-type (if c (get-type c) "nil") ; perhaps want to rewrite this
        v (:value @seen-failed-asserts)
        v-print (pretty-print-value v c c-type)
        arg (arg-str (if n (Integer. n) (:arg-num @seen-failed-asserts)))]
    (empty-seen) ; empty the seen-failed-asserts hashmap
    (make-msg-info-hashes
     "in function " fname :arg " " arg " " v-print :arg
     " must be a " t :type " but is " c-type :type)))

(def error-dictionary [{:key :assertion-error-with-argument
                        :class AssertionError
                        :match #"Assert failed: \((.*) argument(.*)\)"
                        :make-msg-info-obj (fn [matches] (process-asserts-obj (nth matches 2)))}
                       {:key :assertion-error-without-argument
                        :class AssertionError
                        :match #"Assert failed: \((.*)\)"
                        :make-msg-info-obj (fn [matches] (process-asserts-obj nil))}
                       {:key :class-cast-exception
                        :class ClassCastException
                        :match #"(.*) cannot be cast to (.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Attempted to use "
                                                                               (get-type (nth matches 1)) :type ", but "
                                                                               (get-type (nth matches 2)) :type " was expected."))}
                       {:key :illegal-argument-cannot-convert-type
                        :class IllegalArgumentException
                        :match #"Don't know how to create (.*) from: (.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Don't know how to create "
                                                                               (get-type (nth matches 1)) :type
                                                                               " from "(get-type (nth matches 2)) :type))}
                       {:key :illegal-argument-even-number-of-forms
                        :class IllegalArgumentException
                        :match #"(.*) requires an even number of forms"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "There is an unmatched parameter in declaration of "
                                                                               (nth matches 1) :arg))}
                       {:key :illegal-argument-even-number-of-forms-in-binding-vector
                        :class IllegalArgumentException
                        :match #"(.*) requires an even number of forms in binding vector in (.*):(.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "A parameter for a " (nth matches 1)
                                                                               " is missing a binding on line "
                                                                               (nth matches 3) " in the file " (nth matches 2)))}
                       {:key :illegal-argument-needs-vector-when-binding
                        :class IllegalArgumentException
                        :match #"(.*) requires a vector for its binding in (.*):(.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "When declaring a " (nth matches 1)
                                                                               ", you need to pass it a vector of arguments. Line "
                                                                               (nth matches 3) " in the file " (nth matches 2)))}
                       {:key :illegal-argument-type-not-supported
                        :class IllegalArgumentException
                        :match #"(.*) not supported on type: (.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Function " (nth matches 1) :arg
                                                                               " does not allow " (get-type (nth matches 2)) :type " as an argument"))}
                       {:key :illegal-argument-parameters-must-be-in-vector
                        :class IllegalArgumentException
                        :match #"Parameter declaration (.*) should be a vector"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Parameters in " "defn" :arg
                                                                               " should be a vector, but is " (nth matches 1) :arg))}
                       {:key :illegal-argument-exactly-2-forms
                        :class IllegalArgumentException
                        :match #"(.+): (.*) requires exactly 2 forms in binding vector (.+)"
                        :make-msg-info-obj make-mock-preobj}




                       {:key :index-out-of-bounds-index-provided
                        :class IndexOutOfBoundsException
                        :match #"(\d+)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "An index in a sequence is out of bounds."
                                                                               "The index is:" (nth matches 0) :arg))}
                       {:key :index-out-of-bounds-index-not-provided
                        :class IndexOutOfBoundsException
                        :match #"" ; an empty message
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "An index in a sequence is out of bounds"))}
                       {:key :arity-exception-wrong-number-of-arguments
                        :class clojure.lang.ArityException
                        :match #"Wrong number of args \((.*)\) passed to: (.*)"
                        :make-msg-info-obj (fn [matches]
                                             (let [fstr (get-function-name (nth matches 2))
                                                   funstr (if (= fstr "anonymous function")
                                                            "an "
                                                            (str "a function "))]
                                               (make-msg-info-hashes "Wrong number of arguments ("
                                                                     (nth matches 1) ") passed to " funstr fstr :arg)))}
                       {:key :null-pointer-non-existing-object-provided
                        :class NullPointerException
                        :match #"(.+)" ; for some reason (.*) matches twice. Since we know there is at least one symbol, + is fine
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "An attempt to access a non-existing object: "
                                                                               (nth matches 1) :arg "\n(NullPointerException)"))}
                       {:key :null-pointer-non-existing-object-not-provided
                        :class NullPointerException
                        :match  #""
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "An attempt to access a non-existing object. \n(NullPointerException)"))}
                       {:key :unsupported-operation-wrong-type-of-argument
                        :class UnsupportedOperationException
                        :match #"(.*) not supported on this type: (.*)"
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Function " (nth matches 1) :arg
                                                                               " does not allow " (get-type (nth matches 2)) :type " as an argument"))}
                       {:key :java.lang.Exception-improper-identifier
                        :class java.lang.Exception
                        :match #"Unsupported binding form: (.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "You cannot use " (nth matches 1) :arg
                                                                               " as a variable."))}
                       ;; Compilation errors
                       {:key :compiler-exception-even-numbers-in-binding-vector
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.IllegalArgumentException
                        :match #"(.+): (.+) requires an even number of forms in binding vector in (.+):(.+), compiling:(.+)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: " (nth matches 2)
                                                                               " requires an even number of forms in binding vector, while compiling "
                                                                               (nth matches 3)))}
                       {:key :compiler-exception-wrong-number-of-arguments-to-recur
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.IllegalArgumentException
                        :match #"(.*) Mismatched argument count to recur, expected: (.*) args, got: (.*), compiling:(.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: this recur is supposed to take "
                                                                               (nth matches 2) " arguments, but you are passing " (nth matches 3)
                                                                               ", while compiling " (nth matches 4)))
                                                                    ;;TODO: handle singular/plural arguments
                        :hints "1. You are passing a wrong number of arguments to recur. Check its function or loop.
		        		                2. recur might be outside of the scope of its function or loop"}
                       {:key :compiler-exception-wrong-number-of-arguments
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception clojure.lang.ArityException
                        :match #"(.+): Wrong number of args \((.*)\) passed to: (.*), compiling:(.*)"
                        :make-msg-info-obj (fn [matches]
                                             (let [fstr (get-function-name (nth matches 3))
                                                   funstr (if (= fstr "anonymous function")
                                                            "an "
                                                            (str "a function "))]
                                               (make-msg-info-hashes "Wrong number of arguments ("
                                                                     (nth matches 2) ") passed to " funstr fstr :arg
                                                                     ", while compiling "
                                                                     (nth matches 4) :arg)))}
                       {:key :compiler-exception-must-recur-from-tail-position
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.UnsupportedOperationException
                        :match #"(.*) Can only recur from tail position, compiling:(.*)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: recur can only occur "
                                                                               "as a tail call, meaning no operations can"
                                                                               " be done after its return. Compiling "
                                                                               (nth matches 2)))}
                       {:key :compiler-exception-cannot-resolve-symbol
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.RuntimeException
                        :match #"(.+): Unable to resolve symbol: (.+) in this context, compiling:\((.+)\)"
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: " "name "
                                                                               (nth matches 2) :arg " is undefined, while compiling "
                                                                               (nth matches 3) :arg))}
                       {:key :compiler-exception-first-argument-must-be-symbol
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.RuntimeException
                        :match #"(.*) First argument to (.*) must be a Symbol, compiling:\((.+)\)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: "
                                                                               (nth matches 2) :arg " must be followed by a name. Compiling "
                                                                               (nth matches 3)))}
                       ;; This is probably somewhat fragile: it occurs in an unbounded recur, but
                       ;; may occur elsewhere. We need to be careful to not catch a wider rnage of exceptions:
                      ; {:key :compiler-exception-must-recur-to-function-or-loop
                      ;  :class clojure.lang.Compiler$CompilerException
                      ;  :true-exception make-mock-preobj
                      ;  :match #"(.*): clojure.lang.Var\$Unbound cannot be cast to clojure.lang.IPersistentVector, compiling:(.*)"
                      ;  :make-msg-info-obj (fn [matches] (make-msg-info-hashes "recur" :arg
                      ;                                                         " does not refer to any function or loop."
                      ;                                                         " Compiling " (nth matches 2)))}
                       {:key :compiler-exception-cannot-take-value-of-macro
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.RuntimeException
                        :match #"(.+): Can't take value of a macro: (.+), compiling:\((.+)\)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: "
                                                                               (get-macro-name (nth matches 2)) :arg
                                                                               " is a macro, cannot be passed to a function, while compiling "
                                                                               (nth matches 3)))}
                       {:key :compiler-exception-unmatched-delimiter
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.Exception
                        :match #"(.+): Unmatched delimiter: (.+), compiling:(.+)"
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: there is an unmatched deliminter " (nth matches 2) :arg
                                                                               ", while compiling " (nth matches 3) :arg))}
                       {:key :compiler-exception-too-many-arguments
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.Exception
                        :match #"(.+): Too many arguments to (.+), compiling:(.+)"
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: too many arguments to "
                                                                               (nth matches 2) :arg ", while compiling "
                                                                               (nth matches 3) :arg))}
                       {:key :compiler-exception-too-few-arguments
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception java.lang.Exception
                        :match #"(.+): Too few arguments to (.+), compiling:(.+)"
		                    :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: too few arguments to "
                                                                               (nth matches 2) :arg  ", while compiling "
                                                                               (nth matches 3) :arg))}
                       {:key :compiler-exception-end-of-file
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception make-mock-preobj
                        :match #"(.+): EOF while reading, starting at line (.+), compiling:(.+)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "Compilation error: end of file, starting at line " (nth matches 2) :arg
                                                                               ", while compiling " (nth matches 3) :arg \n ". Probably a non-closing
                                                                               parentheses or bracket."))}
                       {:key :compiler-exception-even-number-of-forms-needed
                        :class clojure.lang.Compiler$CompilerException
                        :true-exception make-mock-preobj
                        :match #"(.*): (.*) requires an even number of forms, compiling:\((.+)\)"
                        :make-msg-info-obj (fn [matches] (make-msg-info-hashes "There is an unmatched parameter in declaration of "
                                                                               (nth matches 2) :arg ". Compiling: " (nth matches 3)))}])
