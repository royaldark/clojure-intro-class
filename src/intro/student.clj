(ns intro.student
	(:use [corefns.core]
        [seesaw.core]
	[turtle.core]))

;; Testing compilation errors

;(def f [x] (+ x 2))
;(+ (* 2 3) 7

;(+ (* 2 3) 7)]

;(defn f[x] (+ x y))

;(defn f[x y] 
;	(fn [x] (+ x y z)))

(loop [x 1 y]
	(if (= x 0) 1
		(recur (dec x) 5)))
