(ns errors.stacktrace_test
  (:require [errors.dictionaries :refer :all]
            [expectations :refer :all]
            [corefns.corefns :refer :all]
            [errors.prettify_exception :refer :all]
            [errors.messageobj :refer :all]
            [errors.exceptions :refer :all]))

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


