(ns intro.core ;; Doesn't know the source file for this?
  (:require [errors.core :as errors])
  (:use [corefns.core]
        [seesaw.core]
        [turtle.core]))

(defn basic-seesaw-frame []
  (invoke-later
    (native!)
    (def f (frame :title "Hello",
                  :content (button :text "Seesaw Rocks!"),
                  :on-close :exit))
    (-> f pack! show!))) 



(defn draw-polygon [coll]
	(let [turtle (turtle 400 400)]
	(pen-up turtle)
	(conj coll (first coll))
	(conj coll (second coll))
	(pen-down turtle)
	(loop [s coll] 
	(if (= (count s) 2) (go turtle (first s) (second s))
		(go turtle (first s) (second s))) 
		(recur  (drop 2 s) ))
	(show turtle)))

(defn fib [turtle depth]
  (forward turtle 30)
  (if (> depth 2)
    (do 
      (left turtle 15)
      (fib turtle (- depth 1))
      (right turtle 30)
      (fib turtle (- depth 2))
      (left turtle 15)))
  (back turtle 30))


(defn turtle-demo []
  (let [turtle (turtle 400 400)]
  (pen-up turtle)
  ;(go turtle 0 -100)
    ;Oops! Forgot an argument.
    ;Good thing we have prettify-exception
  ;(go turtle 0) Does not like the turtle to do this, not sure why
  (pen-down turtle)
  (fib turtle 10)
  (show turtle)))

(defn test-and-continue [quoted-exp]
  (try
    (eval quoted-exp)
    (catch Throwable e (println (errors/prettify-exception e)))))

(defn test-all-and-continue [quoted-exps]
  ;; doall is needed because map is lazy 
  (doall (map test-and-continue quoted-exps))) 

(defn test-arithmetic-expressions []
  ;; 3 exceptions thrown:
  (test-all-and-continue '((+ \a 2) (< 'a 8) (/ 5 0)))) 

;; Some of the error messages below would change once we create enough preconditions
(defn test-sequences []
  (test-all-and-continue '( (nth 0 [1 2 3]) ;; attempted to use collection but number was expected 
			    (nth '(1 2 3) 7) ;; Throws an empty error. I am confused. Should be IndexOutOfBoundsException.
			    (into 6 [1 2]) ;; attempted to use number but collection was expected
			    (into {} [1 2 3]) ;; don't know how to create sequence from number. into on a hashmap requires a collection of pairs.
			    (into {} [1 2 3 4]) ;; same as above. A correct one would be (into {} [[1 2] [3 4]])
			    (conj "a" 2) ;; Attempted to use string, but collection was expected.
			    )))


(defn -main [& args]
  (try
    ;(basic-seesaw-frame)
    ;(turtle-demo)
    ;(map 2 [1 2 3]) ; Doesn't realize this is a function from the error message.
    ; (filter is-odd? [1 2 3 4]) ; throws a gigantic exception, lots of compiler errors or lines that aren't there.
    ;(filter 1 [1 2 3 4]) ; does something somewhat reasonable, also doesn't realize this is a function from the error message
    ;(filter #(+ % 2) [1 2 3 4]) ; apparently it's not an error, but perhaps should be for new students? How do we deal with that?
					;(< 'a 8) ;now gives a reasonable message
    ;(defn myfunc [x] (+ x 2))
					;(< myfunc +)
    ;(test-arithmetic-expressions)
    ;(test-sequences)
    ;(reduce + 7)
    (draw-polygon [100 75])
    (catch Throwable e (println (errors/prettify-exception e)))))
