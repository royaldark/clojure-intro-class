(ns errors.prettify_exception_test
   (:use [clojure.test]
         [errors.prettify_exception]
         [errors.dictionaries]))

;;; Stephen wrote this in August 2013

(def simple-non-match-exception (java.lang.Exception. "Test Message"))
(def get-pretty-message (ns-resolve 'errors.core 'get-pretty-message))
(def class-cast-exception (java.lang.ClassCastException. "oneType cannot be cast to anotherType"))

(deftest test-best-approximation
   (is (= "unrecognized type oneType" (best-approximation "oneType")))
   )

(deftest test-get-pretty-message
   (is (= "Test Message" (get-pretty-message simple-non-match-exception)))
   (is (= "Attempted to use unrecognized type oneType, but unrecognized type anotherType was expected." (get-pretty-message class-cast-exception)))
   )
