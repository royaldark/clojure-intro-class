(ns corefns.core-test
  (:require [expectations :refer :all]))

(expect 4 (+ 2 2))

(expect true (check-if-function? "plus" +))
(expect true (check-if-function? "minus" -))
