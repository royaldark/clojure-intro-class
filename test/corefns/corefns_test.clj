(ns corefns.corefns_test
  (:require [expectations :refer :all]
            [corefns.failed_asserts_info :refer :all]))

;; testing for the expectations framework
(expect 4 (+ 2 2))
(expect ArithmeticException (/ 5 0))

;; Nothing here might be necessary at all, but we've kept it in for now.
;; testing for add-to-seen and empty-seen
;; doesn't work yet
(comment
  (do
    (empty-seen)
    (expect nil (deref seen-failed-asserts))
    (add-to-seen {:a 1})
    (expect {:a 1} (deref seen-failed-asserts))
    (empty-seen)))


;; testing for add-to-seen and empty-seen
;; doesn't work yet
(comment
  (do
    (empty-seen)
    (add-to-seen {:a 1})
    (expect {:a 1} @seen-failed-asserts)
    ;(add-to-seen {:b 2 :c 3})
    ;(expect {:a 1, :b 2, :c 3} @seen-failed-asserts)
    (empty-seen)
    (expect nil @seen-failed-asserts)))
