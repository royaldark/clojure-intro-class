(ns errors.stacktrace_test
  (:require [errors.dictionaries :refer :all]
            [expectations :refer :all]
            [corefns.corefns :refer :all]
            [errors.prettify_exception :refer :all]
            [errors.messageobj :refer :all]
            [errors.exceptions :refer :all]))

;;***************************************************
;;***** Testing individual stacktrace elements ******
;;****************************************************

;; a helper function to test for either nil or false
(defn- not-true? [x] (not (true? x)))

(expect not-true? (keep-stack-trace-elem {:method "add",
				     :class "clojure.lang.Numbers",
				     :java true, :file "Numbers.java", :line 126}))

(expect not-true? (keep-stack-trace-elem {:method "eval", :class "clojure.lang.Compiler",
				     :java true, :file "Compiler.java", :line 6619}))

(expect true? (keep-stack-trace-elem {:anon-fn true, :fn "load",
				      :ns "clojure.core", :clojure true,
				      :file "core.clj", :line 5530}))

(expect not-true? (keep-stack-trace-elem {:anon-fn false, :fn "track-reload",
				     :ns "clojure.tools.namespace.reload", :clojure true,
				     :file "reload.clj", :line 52}))

(expect not-true? (keep-stack-trace-elem {:method "main", :class "clojure.main",
					  :java true, :file "main.java", :line 37}))

;; Artificial example (in reality :java true) to test filtering
;; Note: this returns true since "clojure.main" is a class, not a namespace
(expect true? (keep-stack-trace-elem {:method "main", :class "clojure.main",
				      :clojure true, :file "main.java", :line 37}))

(expect not-true? (keep-stack-trace-elem {:method "main", :ns "clojure.main",
					  :clojure true, :file "main.java", :line 37}))

(expect not-true?  (keep-stack-trace-elem
                    {:anon-fn false, :fn "main", :ns "clojure.main", :clojure true,
                     :file "main.clj", :line 420}))

(expect not-true?  (keep-stack-trace-elem
                    {:anon-fn false, :fn "main", :ns "clojure.lang", :clojure true,
                     :file "main.clj", :line 420}))



;;****************************************************
;;********** Testing filtering stacktrace ************
;;****************************************************

;; a helper function to test the size of a stacktrace
(defn- helper-stack-count? [n stacktrace]
  (= n (count stacktrace)))

(defn- check-stack-count? [n]
  (partial helper-stack-count? n))

;; testing a count-checking function itself:
(expect (check-stack-count? 3) [3 6 7])

(defn- helper-trace-elem-has-function? [fun trace-elem]
  (= fun (:fn trace-elem)))

(defn- trace-elem-has-function? [fun]
  (partial helper-trace-elem-has-function? fun))

(defn- helper-trace-elem-has-pair? [k v trace-elem]
  (= v (k trace-elem)))

(defn- trace-elem-has-pair? [k v]
  (partial helper-trace-elem-has-pair? k v))

(defn- helper-trace-elem-has-all-pairs? [kv-pairs trace-elem]
  "checks that every binding in kv-pairs also appears in trace-elem"
  (every? true? (map #(helper-trace-elem-has-pair? (first %) (second %) trace-elem) kv-pairs)))

;; testing the helper function:
(expect true (helper-trace-elem-has-all-pairs? {:fn "map" :ns "corefns.corefns"}
					       {:fn "map" :junk :dontcare :ns "corefns.corefns"}))

(expect false (helper-trace-elem-has-all-pairs? {:fn "map" :ns "corefns.corefns"}
					       {:fn :dontcare :junk "map" :ns "corefns.corefns"}))

(defn- trace-elem-has-all-pairs? [kv-pairs]
  (partial helper-trace-elem-has-all-pairs? kv-pairs))

;; a stacktrace object copied from a run:
(def complete-stack
  [{:anon-fn false, :fn "map", :ns "corefns.corefns", :clojure true, :file "corefns.clj", :line 34}
   {:method "invoke", :class clojure.lang.RestFn, :java true, :file "RestFn.java", :line 423}
   {:anon-fn false, :fn "eval6415", :ns "intro.core", :clojure true, :file "NO_SOURCE_FILE", :line 302}
   {:method "eval", :class clojure.lang.Compiler, :java true, :file "Compiler.java", :line 6703}
   {:method "eval", :class clojure.lang.Compiler, :java true, :file "Compiler.java", :line 6666}
   {:anon-fn false, :fn "eval", :ns "clojure.core", :clojure true, :file "core.clj", :line 2927}
   {:anon-fn false, :fn "test-and-continue", :ns "intro.core", :clojure true, :file "core.clj", :line 22}
   {:anon-fn true, :fn "map", :ns "clojure.core", :clojure true, :file "core.clj", :line 2559}
   {:method "sval", :class clojure.lang.LazySeq, :java true, :file "LazySeq.java", :line 40}
   {:method "seq", :class clojure.lang.LazySeq, :java true, :file "LazySeq.java", :line 49}
   {:method "seq", :class clojure.lang.RT, :java true, :file "RT.java", :line 484}
   {:anon-fn false, :fn "seq", :ns "clojure.core", :clojure true, :file "core.clj", :line 133}
   {:anon-fn false, :fn "dorun", :ns "clojure.core", :clojure true, :file "core.clj", :line 2855}
   {:anon-fn false, :fn "doall", :ns "clojure.core", :clojure true, :file "core.clj", :line 2871}
   {:anon-fn false, :fn "test-all-and-continue", :ns "intro.core", :clojure true, :file "core.clj", :line 28}
   {:anon-fn false, :fn "test-asserts", :ns "intro.core", :clojure true, :file "core.clj", :line 301}
   {:anon-fn false, :fn "-main", :ns "intro.core", :clojure true, :file "core.clj", :line 599}
   {:method "invoke", :class clojure.lang.RestFn, :java true, :file "RestFn.java", :line 397}
   {:method "invoke", :class clojure.lang.Var, :java true, :file "Var.java", :line 375}
   {:anon-fn false, :fn "eval6408", :ns "user", :clojure true, :file "NO_SOURCE_FILE", :line 1}
   {:method "eval", :class clojure.lang.Compiler, :java true, :file "Compiler.java", :line 6703}
   {:method "eval", :class clojure.lang.Compiler, :java true, :file "Compiler.java", :line 6693}
   {:method "eval", :class clojure.lang.Compiler, :java true, :file "Compiler.java", :line 6666}
   {:anon-fn false, :fn "eval", :ns "clojure.core", :clojure true, :file "core.clj", :line 2927}
   {:anon-fn false, :fn "eval-opt", :ns "clojure.main", :clojure true, :file "main.clj", :line 288}
   {:anon-fn false, :fn "initialize", :ns "clojure.main", :clojure true, :file "main.clj", :line 307}
   {:anon-fn false, :fn "null-opt", :ns "clojure.main", :clojure true, :file "main.clj", :line 342}
   {:anon-fn false, :fn "main", :ns "clojure.main", :clojure true, :file "main.clj", :line 420}
   {:method "invoke", :class clojure.lang.RestFn, :java true, :file "RestFn.java", :line 421}
   {:method "invoke", :class clojure.lang.Var, :java true, :file "Var.java", :line 383}
   {:method "applyToHelper", :class clojure.lang.AFn, :java true, :file "AFn.java", :line 156}
   {:method "applyTo", :class clojure.lang.Var, :java true, :file "Var.java", :line 700}
   {:method "main", :class clojure.main, :java true, :file "main.java", :line 37}])

(def filtered-stack
  [{:anon-fn false, :fn "map", :ns "corefns.corefns", :clojure true, :file "corefns.clj", :line 34}
   {:anon-fn false, :fn "eval6415", :ns "intro.core", :clojure true, :file "NO_SOURCE_FILE", :line 302}
   {:anon-fn false, :fn "eval", :ns "clojure.core", :clojure true, :file "core.clj", :line 2927}
   {:anon-fn false, :fn "test-and-continue", :ns "intro.core", :clojure true, :file "core.clj", :line 22}
   {:anon-fn true, :fn "map", :ns "clojure.core", :clojure true, :file "core.clj", :line 2559}
   {:anon-fn false, :fn "seq", :ns "clojure.core", :clojure true, :file "core.clj", :line 133}
   {:anon-fn false, :fn "dorun", :ns "clojure.core", :clojure true, :file "core.clj", :line 2855}
   {:anon-fn false, :fn "doall", :ns "clojure.core", :clojure true, :file "core.clj", :line 2871}
   {:anon-fn false, :fn "test-all-and-continue", :ns "intro.core", :clojure true, :file "core.clj", :line 28}
   {:anon-fn false, :fn "test-asserts", :ns "intro.core", :clojure true, :file "core.clj", :line 301}
   {:anon-fn false, :fn "-main", :ns "intro.core", :clojure true, :file "core.clj", :line 599}
   {:anon-fn false, :fn "eval6408", :ns "user", :clojure true, :file "NO_SOURCE_FILE", :line 1}
   {:anon-fn false, :fn "eval", :ns "clojure.core", :clojure true, :file "core.clj", :line 2927}])

(expect (check-stack-count? 13) (filter-stacktrace complete-stack))

(expect {:anon-fn true, :fn "map", :ns "clojure.core", :clojure true, :file "core.clj", :line 2559}
	(in (filter-stacktrace complete-stack)))

(expect (trace-elem-has-function? "map") (in (filter-stacktrace complete-stack)))

(expect (trace-elem-has-pair? :fn "-main") (in (filter-stacktrace complete-stack)))

(expect (trace-elem-has-all-pairs? {:fn "test-and-continue", :ns "intro.core"})
				   (in (filter-stacktrace complete-stack)))

(expect filtered-stack (filter-stacktrace complete-stack))

;; we can combine conditions if we want to, do we want to?
;(expect (more filtered-stack (check-stack-count? 13))
;	(filter-stacktrace complete-stack))
