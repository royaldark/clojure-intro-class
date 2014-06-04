(ns errors.core-test
  (:require [expectations :refer :all]
	    [errors.messageobj :refer :all]
	    [errors.exceptionobj :refer :all]
	    [errors.exceptions :refer :all]
	    [clj-stacktrace.core :as stacktrace]))

;;; INDEX ;;;

;;1. Functions
;;2. Prebuilt Exceptions
;;3. errors.messageobj
;;4. errors.exceptionobj
;;5. errors.core, errorgui tests

;####################
;### 1. Functions ###
;####################

(defn ignore-stacktrace [trace1 trace2] true)

(defn run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

(defn exception->string
  "Converts exceptions to strings, returning a string or the original e if it is not an exception"
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))
;; Testing the above

(expect "java.lang.Long cannot be cast to clojure.lang.IFn"
        (exception->string (run-and-catch '(1 3))))

(expect 3
        (exception->string (run-and-catch '(+ 1 2))))

;###############################
;### 2. Prebuilt Exceptions ####
;###############################



(def function-call-on-value (run-and-catch '(4 + "pie")))

(def class-cast-exception (run-and-catch '(+ 4 "pie")))



;##################################
;### 3. errors.messageobj tests ###
;##################################

;***Testing make-preobj-hashes***

(expect (make-preobj-hashes) [])

;***Testing make-msg-preobj-hash***

(expect (make-msg-preobj-hash "Hi there")
	{:msg "Hi there" :stylekey :reg :length 8})

(expect (make-msg-preobj-hash "Hi there" :arg)
	{:msg "Hi there" :stylekey :arg :length 8})

(expect (make-msg-preobj-hash "Blue Jeans")
 {:msg "Blue Jeans", :stylekey :reg, :length 10})

(expect (make-msg-preobj-hash "Bootcut" :hippy)
 {:msg "Bootcut", :stylekey :hippy, :length 7})

;***Testing make-preobj-hashes***

(expect (make-preobj-hashes "Hi there")
	[{:msg "Hi there" :stylekey :reg :length 8}])

(expect (make-preobj-hashes "Hi there" :arg)
	[{:msg "Hi there" :stylekey :arg :length 8}])

(expect (make-preobj-hashes "Hi there" "Hello")
	[{:msg "Hi there" :stylekey :reg :length 8}
	 {:msg "Hello" :stylekey :reg :length 5}])

(expect (make-preobj-hashes "Hi there" :arg "Hello" :blah)
	[{:msg "Hi there" :stylekey :arg :length 8}
	 {:msg "Hello" :stylekey :blah :length 5}])

(expect (make-preobj-hashes "Blue Jeans"
                             "Khakis" :business-casual
                             "Bootcut" :hippy
                             "Jeggings" :casual)

 [{:msg "Blue Jeans", :stylekey :reg, :length 10}
  {:msg "Khakis", :stylekey :business-casual, :length 6}
  {:msg "Bootcut", :stylekey :hippy, :length 7}
  {:msg "Jeggings", :stylekey :casual, :length 8}])

;***Testing make-obj***

(expect (make-obj (make-preobj-hashes "Hi there" :arg "Hello"))
	[{:msg "Hi there" :stylekey :arg :length 8 :start 0}
	 {:msg "Hello" :stylekey :reg :length 5 :start 8}])

(expect (make-obj [{:msg "Blue Jeans", :stylekey :reg, :length 10}
                   {:msg "Khakis", :stylekey :business-casual, :length 6}
                   {:msg "Bootcut", :stylekey :hippy, :length 7}
                   {:msg "Jeggings", :stylekey :casual, :length 8}])

 [{:start 0, :msg "Blue Jeans", :stylekey :reg, :length 10}
  {:start 10, :msg "Khakis", :stylekey :business-casual, :length 6}
  {:start 16, :msg "Bootcut", :stylekey :hippy, :length 7}
  {:start 23, :msg "Jeggings", :stylekey :casual, :length 8}])

;***Testing get-all-text***

(expect (get-all-text (make-obj (make-preobj-hashes "Hi there! " :arg "Hello")))
	"Hi there! Hello")

;***Testing make-mock-preobj***

(expect (make-mock-preobj ["anything" "will be ignored"])
	[{:msg "This is a" :stylekey :reg :length 9}
	 {:msg "test" :stylekey :arg :length 4}])

;####################################
;### 4. errors.exceptionobj tests ###
;####################################


;######################################
;### 5. errors.core, errorgui tests ###
;######################################

;; get a pre-stored exception
(def classcast-exc (import-from-file (str path "classcast1.ser")))
(def classcast-exc-parsed (stacktrace/parse-exception classcast-exc))

(expect java.lang.ClassCastException (class classcast-exc))

(expect java.lang.ClassCastException (:class classcast-exc-parsed))

(expect "java.lang.String cannot be cast to java.lang.Number" (:message classcast-exc-parsed))

(expect 61 (count (:trace-elems classcast-exc-parsed)))

(expect {:method "add", :class "clojure.lang.Numbers", :java true, :file "Numbers.java", :line 126}
	(first (:trace-elems classcast-exc-parsed)))

(def the-trace
(list {:method "add", :class "clojure.lang.Numbers", :java true, :file "Numbers.java", :line 126}
{:method "add", :class "clojure.lang.Numbers", :java true, :file "Numbers.java", :line 3523}
{:anon-fn false, :fn "eval9481", :ns "experimental.core-test", :clojure true,
:file "core_test.clj", :line 57}
{:method "eval", :class "clojure.lang.Compiler"
, :java true, :file "Compiler.java", :line 6619}
{:method "eval", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 6582}
{:anon-fn false, :fn "eval", :ns "clojure.core", :clojure true, :file "core.clj", :line 2852}
{:anon-fn false, :fn "run-and-catch", :ns "experimental.core-test", :clojure true, :file "core_test.clj", :line 38}
{:method "applyToHelper", :class "clojure.lang.AFn", :java true, :file "AFn.java", :line 161}
{:method "applyTo", :class "clojure.lang.AFn", :java true, :file "AFn.java", :line 151}
{:method "eval", :class
 "clojure.lang.Compiler$InvokeExpr", :java true, :file "Compiler.java", :line 3458}
{:method "eval", :class "clojure.lang.Compiler$DefExpr", :java true, :file "Compiler.java", :line 408}
{:method "eval", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 6624}
{:method "load", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 7064}
{:method "loadResourceScript", :class "clojure.lang.RT", :java true, :file "RT.java", :line 370}
{:method "loadResourceScript", :class "clojure.lang.RT", :java true, :file "RT.java", :line 361}
{:method "load", :class "clojure.lang.RT", :java true, :file "RT.java", :line 440}
{:method "load", :class "clojure.lang.RT", :java true, :file "RT.java", :line 411}
{:anon-fn true, :fn "load", :ns "clojure.core", :clojure true, :file "core.clj", :line 5530}
{:anon-fn false, :fn "load", :ns "clojure.core",
 :clojure true, :file "core.clj", :line 5529}
{:method "invoke", :class "clojure.lang.RestFn", :java true, :file "RestFn.java", :line 408}
{:anon-fn false, :fn "load-one", :ns "clojure.core", :clojure true, :file "core.clj", :line 5336}
{:anon-fn true, :fn "load-lib", :ns "clojure.core", :clojure true, :file "core.clj"
, :line 5375}
{:anon-fn false, :fn "load-lib", :ns "clojure.core", :clojure true, :file "core.clj", :line 5374}
{:method "applyTo", :class "clojure.lang.RestFn"
, :java true, :file "RestFn.java", :line 142}
{:anon-fn false, :fn "apply", :ns "clojure.core", :clojure true, :file "core.clj", :line 619}
{:anon-fn false, :fn  "load-libs", :ns "clojure.core", :clojure true, :file "core.clj", :line 5413}
{:method "applyTo", :class "clojure.lang.RestFn", :java true, :file "RestFn.java"
, :line 137}
{:anon-fn false, :fn "apply", :ns "clojure.core", :clojure true, :file "core.clj", :line 619}
{:anon-fn false, :fn "require", :ns "clojure.core", :clojure true, :file "core.clj", :line 5496}
{:method "invoke", :class "clojure.lang.RestFn", :java true, :file "RestFn.java", :line 421}
{:anon-fn false, :fn "track-reload-one", :ns "clojure.tools.namespace.reload", :clojure true, :file "reload.clj", :line 35}
{:anon-fn false, :fn "track-reload", :ns "clojure.tools.namespace.reload", :clojure true, :file "reload.clj", :line 52}
{:method "applyToHelper", :class "clojure.lang.AFn", :java true, :file "AFn.java", :line 161}
{:method "applyTo", :class "clojure.lang.AFn", :java true, :file "AFn.java", :line 151}
{:method "alterRoot", :class "clojure.lang.Var", :java true, :file "Var.java"
, :line 336}
{:anon-fn false, :fn "alter-var-root", :ns "clojure.core", :clojure
 true, :file "core.clj", :line 4946}
{:method "invoke", :class "clojure.lang.RestFn", :java true, :file "RestFn.java", :line 425}
{:anon-fn false, :fn "do-refresh", :ns "clojure.tools.namespace.repl", :clojure true, :file "repl.clj", :line 94}
{:anon-fn false, :fn "refresh", :ns "clojure.tools.namespace.repl", :clojure
 true, :file "repl.clj", :line 142}
{:method "invoke", :class "clojure.lang.RestFn", :java true, :file "RestFn.java", :line 397}
{:anon-fn false, :fn "refresh-environment", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 23}
{:anon-fn true, :fn "run-tests", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 50}
{:anon-fn false, :fn "run-tests", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 50}
{:anon-fn true, :fn "monitor-project", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 69}
{:anon-fn true, :fn "monitor-project", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 68}
{:anon-fn false, :fn "monitor-project", :ns "autoexpect.runner", :clojure true, :file "runner.clj", :line 66}
{:anon-fn false, :fn "eval1187", :ns "user", :clojure true, :file "form-init6834699387848419871.clj",
:line 1}
{:method "eval", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 6619}
{:method "eval", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 6609}
{:method "load", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 7064}
{:method "loadFile", :class "clojure.lang.Compiler", :java true, :file "Compiler.java", :line 7020}
{:anon-fn false, :fn "load-script", :ns "clojure.main", :clojure true, :file "main.clj", :line 294}
{:anon-fn false, :fn "init-opt", :ns "clojure.main", :clojure true, :file "main.clj", :line 299}
{:anon-fn false, :fn "initialize", :ns "clojure.main", :clojure true, :file "main.clj", :line 327}
{:anon-fn false, :fn "null-opt", :ns "clojure.main", :clojure true, :file "main.clj", :line 362}
{:anon-fn false, :fn "main", :ns "clojure.main", :clojure true, :file "main.clj", :line 440}
{:method "invoke", :class "clojure.lang.RestFn", :java true, :file "RestFn.java", :line 421}
{:method "invoke", :class "clojure.lang.Var", :java true, :file "Var.java", :line 419}
{:method "applyToHelper", :class "clojure.lang.AFn", :java true, :file "AFn.java", :line 163}
{:method "applyTo", :class "clojure.lang.Var", :java true, :file "Var.java", :line 532}
{:method "main", :class "clojure.main", :java true, :file "main.java", :line 37}))

(expect the-trace (:trace-elems classcast-exc-parsed))
