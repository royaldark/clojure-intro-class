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

;###############################
;### Checks a single element ###
;###############################

;### Does have ###

(defn- helper-trace-elem-has-function? [fun trace-elem]
  (= fun (:fn trace-elem)))

(defn- trace-elem-has-function? [fun]
  (partial helper-trace-elem-has-function? fun))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-elem-has-pair? [k v trace-elem]
  (= v (k trace-elem)))

(defn- trace-elem-has-pair? [k v]
  (partial helper-trace-elem-has-pair? k v))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-elem-has-all-pairs? [kv-pairs trace-elem]
  "checks that every binding in kv-pairs also appears in trace-elem"
  (every? true? (map #(helper-trace-elem-has-pair? (first %) (second %) trace-elem) kv-pairs)))

(defn- trace-elem-has-all-pairs? [kv-pairs]
  (partial helper-trace-elem-has-all-pairs? kv-pairs))

;### Doesn't have ###

(defn- helper-trace-elem-doesnt-have-function? [fun trace-elem]
  (not (helper-trace-elem-has-function? fun trace-elem)))

(defn- trace-elem-doesnt-have-function? [fun]
  (partial helper-trace-elem-doesnt-have-function? fun))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-elem-doesnt-have-pair? [k v trace-elem]
  (not (helper-trace-elem-has-pair? k v trace-elem)))

(defn- trace-elem-doesnt-have-pair? [k v]
  (partial helper-trace-elem-doesnt-have-pair? k v))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
(defn- helper-trace-elem-doesnt-have-all-pairs? [kv-pairs trace-elem]
  "checks that every binding in kv-pairs also appears in trace-elem"
  (not (helper-trace-elem-has-all-pairs? kv-pairs trace-elem)))

(defn- trace-elem-doesnt-have-all-pairs? [kv-pairs]
  (partial helper-trace-elem-doesnt-have-all-pairs? kv-pairs))

;#################################
;### Checks a whole stacktrace ###
;#################################

;; a helper function to test the size of a stacktrace
(defn- helper-stack-count? [n stacktrace]
  (= n (count stacktrace)))

(defn- check-stack-count? [n]
  (partial helper-stack-count? n))

;### Does have ###

(defn- helper-trace-has-function? [fun trace]
  (any? (trace-elem-has-function? fun) trace))

(defn- trace-has-function? [fun]
  (partial helper-trace-has-function? fun))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-has-pair? [k v trace]
  (any? (trace-elem-has-pair? k v) trace))

(defn- trace-has-pair? [k v]
  (partial helper-trace-has-pair? k v))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-has-all-pairs? [kv-pairs trace]
  (any? (trace-elem-has-all-pairs? kv-pairs) trace))

(defn- trace-has-all-pairs? [kv-pairs]
  (partial helper-trace-has-all-pairs? kv-pairs))

;### Doesn't have ###

(defn- helper-trace-doesnt-have-function? [fun trace]
  (not (helper-trace-has-function? fun trace)))

(defn- trace-doesnt-have-function? [fun]
  (partial helper-trace-doesnt-have-function? fun))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-doesnt-have-pair? [k v trace]
  (not (helper-trace-has-pair? k v trace)))

(defn- trace-doesnt-have-pair? [k v]
  (partial helper-trace-doesnt-have-pair? k v))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(defn- helper-trace-doesnt-have-all-pairs? [kv-pairs trace]
  (not (helper-trace-has-all-pairs? kv-pairs trace)))

(defn- trace-doesnt-have-all-pairs? [kv-pairs]
  (partial helper-trace-doesnt-have-all-pairs? kv-pairs))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



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

;#############
;### Tests ###
;#############

;; testing for filter-stacktrace
(expect filtered-stack (filter-stacktrace complete-stack))
(expect {:anon-fn true, :fn "map", :ns "clojure.core", :clojure true, :file "core.clj", :line 2559}
	(in (filter-stacktrace complete-stack)))

;; testing for helper-trace-elem-has-function?
(expect true (helper-trace-elem-has-function? "map" {:fn "map" :ns "corefns.corefns"}))
(expect false (helper-trace-elem-has-function? "soup" {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-has-function?
(expect (trace-elem-has-function? "map") (in filtered-stack))
(expect true ((trace-elem-has-function? "map") {:fn "map" :ns "corefns.corefns"}))
(expect false ((trace-elem-has-function? "donkey") {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-trace-elem-has-pair?
(expect true (helper-trace-elem-has-pair? :ns "corefns.corefns" {:fn "map" :ns "corefns.corefns"}))
(expect false (helper-trace-elem-has-pair? :emma "lemmon" {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-has-pair?
(expect (trace-elem-has-pair? :fn "-main") (in filtered-stack))
(expect true ((trace-elem-has-pair? :ns "corefns.corefns") {:fn "map" :ns "corefns.corefns"}))
(expect false ((trace-elem-has-pair? :emma "lemmon") {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-trace-elem-has-all-pairs?
(expect true (helper-trace-elem-has-all-pairs? {:ns "corefns.corefns", :fn "map"} {:fn "map" :ns "corefns.corefns"}))
(expect false (helper-trace-elem-has-all-pairs? {:emma "lemmon", :not "pass"} {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-has-all-pairs?
(expect (trace-elem-has-all-pairs? {:fn "test-and-continue", :ns "intro.core"}) (in filtered-stack))
(expect true ((trace-elem-has-all-pairs? {:ns "corefns.corefns" :fn "map"}) {:fn "map" :ns "corefns.corefns"}))
(expect false ((trace-elem-has-all-pairs? {:emma "lemmon" :not "pass"}) {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-trace-elem-doesnt-have-function?
(expect false (helper-trace-elem-doesnt-have-function? "map" {:fn "map" :ns "corefns.corefns"}))
(expect true (helper-trace-elem-doesnt-have-function? "soup" {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-doesnt-have-function?
(expect (trace-elem-doesnt-have-function? "smooth") (in filtered-stack))
(expect false ((trace-elem-doesnt-have-function? "map") {:fn "map" :ns "corefns.corefns"}))
(expect true ((trace-elem-doesnt-have-function? "donkey") {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-trace-elem-doesnt-have-pair?
(expect false (helper-trace-elem-doesnt-have-pair? :ns "corefns.corefns" {:fn "map" :ns "corefns.corefns"}))
(expect true (helper-trace-elem-doesnt-have-pair? :emma "lemmon" {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-doesnt-have-pair?
(expect (trace-elem-doesnt-have-pair? :fn "hippo") (in filtered-stack))
(expect false ((trace-elem-doesnt-have-pair? :ns "corefns.corefns") {:fn "map" :ns "corefns.corefns"}))
(expect true ((trace-elem-doesnt-have-pair? :emma "lemmon") {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-trace-elem-doesnt-have-all-pairs?
(expect false (helper-trace-elem-doesnt-have-all-pairs? {:ns "corefns.corefns", :fn "map"} {:fn "map" :ns "corefns.corefns"}))
(expect true (helper-trace-elem-doesnt-have-all-pairs? {:emma "lemmon", :not "pass"} {:fn "map" :ns "corefns.corefns"}))

;; testing for trace-elem-doesnt-have-all-pairs?
(expect (trace-elem-doesnt-have-all-pairs? {:fn "test-and-continue", :ns "rhino.squirrel"}) (in filtered-stack))
(expect false ((trace-elem-doesnt-have-all-pairs? {:ns "corefns.corefns" :fn "map"}) {:fn "map" :ns "corefns.corefns"}))
(expect true ((trace-elem-doesnt-have-all-pairs? {:emma "lemmon" :not "pass"}) {:fn "map" :ns "corefns.corefns"}))

;; testing for helper-stack-count?
(expect (helper-stack-count? 2 [:z :q]))
(expect (helper-stack-count? 0 []))

;; testing for check-stack-count?
(expect (check-stack-count? 3) [3 6 7])
(expect (check-stack-count? 0) [])
(expect (check-stack-count? 13) filtered-stack)

;; testing for helper-trace-has-function?
(expect true (helper-trace-has-function? "map" filtered-stack))
(expect true (helper-trace-has-function? "doall" filtered-stack))
(expect false (helper-trace-has-function? "soup" filtered-stack))
(expect false (helper-trace-has-function? "map" []))

;; testing for trace-has-function?
(expect (trace-has-function? "map") filtered-stack)
(expect true ((trace-has-function? "map") [{:fn "map" :ns "corefns.corefns"}
                                           {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-function? "donkey") [{:fn "map" :ns "corefns.corefns"}
                                               {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-has-pair?
(expect true (helper-trace-has-pair? :fn "map" filtered-stack))
(expect true (helper-trace-has-pair? :fn "doall" filtered-stack))
(expect false (helper-trace-has-pair? :fn "soup" filtered-stack))
(expect false (helper-trace-has-pair? :fn "map" []))

;; testing for trace-has-pair?
(expect (trace-has-pair? :fn "map") filtered-stack)
(expect true ((trace-has-pair? :something "else") [{:fn "map" :ns "corefns.corefns"}
                                                   {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-pair? :shark "donkey") [{:fn "map" :ns "corefns.corefns"}
                                                  {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-has-pair?
(expect true (helper-trace-has-pair? :fn "map" filtered-stack))
(expect true (helper-trace-has-pair? :fn "doall" filtered-stack))
(expect false (helper-trace-has-pair? :fn "soup" filtered-stack))
(expect false (helper-trace-has-pair? :fn "map" []))

;; testing for trace-has-pair?
(expect (trace-has-pair? :fn "map") filtered-stack)
(expect true ((trace-has-pair? :something "else") [{:fn "map" :ns "corefns.corefns"}
                                                   {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-pair? :shark "donkey") [{:fn "map" :ns "corefns.corefns"}
                                                  {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-has-all-pairs?
(expect true (helper-trace-has-all-pairs? {:anon-fn false, :fn "seq"} filtered-stack))
(expect false (helper-trace-has-all-pairs? {:fn "doall" :foo "bar"} filtered-stack))
(expect false (helper-trace-has-all-pairs? {:fn "doall" :anon-fn true} filtered-stack))
(expect false (helper-trace-has-all-pairs? {:fn "soup" :despicable-me "minions"} filtered-stack))
(expect false (helper-trace-has-all-pairs? {:fn "map" :loser "winner"} []))

;; testing for trace-has-all-pairs?
(expect (trace-has-all-pairs? {:fn "map" :clojure true}) filtered-stack)
(expect true ((trace-has-all-pairs? {:something "else" :emma "lemmon"}) [{:fn "map" :ns "corefns.corefns"}
                                                                         {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-all-pairs? {:shark "donkey" :porpoise "laughter"}) [{:fn "map" :ns "corefns.corefns"}
                                                                              {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-all-pairs? {:shark "donkey" :ns "corefns.corefns"}) [{:fn "map" :ns "corefns.corefns"}
                                                                               {:something "else" :emma "lemmon"}]))
(expect false ((trace-has-all-pairs? {:something "else" :ns "corefns.corefns"}) [{:fn "map" :ns "corefns.corefns"}
                                                                                 {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-doesnt-have-function?
(expect false (helper-trace-doesnt-have-function? "map" filtered-stack))
(expect false (helper-trace-doesnt-have-function? "doall" filtered-stack))
(expect true (helper-trace-doesnt-have-function? "soup" filtered-stack))
(expect true (helper-trace-doesnt-have-function? "map" []))

;; testing for trace-doesnt-have-function?
(expect (trace-doesnt-have-function? "singapore") filtered-stack)
(expect false ((trace-doesnt-have-function? "map") [{:fn "map" :ns "corefns.corefns"}
                                                    {:something "else" :emma "lemmon"}]))
(expect true ((trace-doesnt-have-function? "donkey") [{:fn "map" :ns "corefns.corefns"}
                                                      {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-doesnt-have-pair?
(expect false (helper-trace-doesnt-have-pair? :fn "map" filtered-stack))
(expect false (helper-trace-doesnt-have-pair? :fn "doall" filtered-stack))
(expect true (helper-trace-doesnt-have-pair? :fn "soup" filtered-stack))
(expect true (helper-trace-doesnt-have-pair? :fn "map" []))

;; testing for trace-doesnt-have-pair?
(expect (trace-doesnt-have-pair? :fn "nachos") filtered-stack)
(expect false ((trace-doesnt-have-pair? :something "else") [{:fn "map" :ns "corefns.corefns"}
                                                            {:something "else" :emma "lemmon"}]))
(expect true ((trace-doesnt-have-pair? :shark "donkey") [{:fn "map" :ns "corefns.corefns"}
                                                         {:something "else" :emma "lemmon"}]))

;; testing for helper-trace-doesnt-have-all-pairs?
(expect false (helper-trace-doesnt-have-all-pairs? {:anon-fn false, :fn "seq"} filtered-stack))
(expect true (helper-trace-doesnt-have-all-pairs? {:fn "doall" :foo "bar"} filtered-stack))
(expect true (helper-trace-doesnt-have-all-pairs? {:fn "doall" :anon-fn true} filtered-stack))
(expect true (helper-trace-doesnt-have-all-pairs? {:fn "soup" :despicable-me "minions"} filtered-stack))
(expect true (helper-trace-doesnt-have-all-pairs? {:fn "map" :loser "winner"} []))

;; testing for trace-doesnt-have-all-pairs?
(expect (trace-doesnt-have-all-pairs? {:fn "map" :clojure "ruby"}) filtered-stack)
(expect false ((trace-doesnt-have-all-pairs? {:something "else" :emma "lemmon"}) [{:fn "map" :ns "corefns.corefns"}
                                                                                  {:something "else" :emma "lemmon"}]))
(expect true ((trace-doesnt-have-all-pairs? {:shark "donkey" :porpoise "laughter"}) [{:fn "map" :ns "corefns.corefns"}
                                                                                     {:something "else" :emma "lemmon"}]))
(expect true ((trace-doesnt-have-all-pairs? {:shark "donkey" :ns "corefns.corefns"}) [{:fn "map" :ns "corefns.corefns"}
                                                                                      {:something "else" :emma "lemmon"}]))
(expect true ((trace-doesnt-have-all-pairs? {:something "else" :ns "corefns.corefns"}) [{:fn "map" :ns "corefns.corefns"}
                                                                                        {:something "else" :emma "lemmon"}]))
;; we can combine conditions if we want to, do we want to?

;(expect (more filtered-stack (check-stack-count? 13))
;	(filter-stacktrace complete-stack))
