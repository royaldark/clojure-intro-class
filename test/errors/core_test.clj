(ns errors.core-test
  (:require [expectations :refer :all]
	    [errors.messageobj :refer :all]))

(expect 4 (+ 2 2))