(ns intro.core 
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
  (test-all-and-continue '( ;(nth 0 [1 2 3]) ;; attempted to use collection but number was expected 
			    ;(nth '(1 2 3) 7) ;; An index in a vector or a list is out of bounds
			    (into 6 [1 2]) ;; attempted to use number but collection was expected
			    (into {} [1 2 3]) ;; don't know how to create sequence from number. into on a hashmap requires a collection of pairs.
			    (into {} [1 2 3 4]) ;; same as above. A correct one would be (into {} [[1 2] [3 4]])
			    (conj "a" 2) ;; Attempted to use string, but collection was expected.
			    )))

(defn test-nth []
  (test-all-and-continue '( (nth 3 [1 2 3]) ;; ERROR: Attempted to use a collection, but a number was expected.
			(nth [1 2 3] 3)
			(nth [1 2 3] [8 9])
			(nth #{1 2 3} 1))))

(defn test-exceptions []
  (test-all-and-continue '((throw (new IndexOutOfBoundsException))
			   (throw (new IndexOutOfBoundsException "10"))
			   (throw (new NullPointerException))
			   (throw (new NullPointerException "some message")))))

(defn test-turtle []
  (test-all-and-continue '((in-ns 'turtle.core) ; needed because otherwise eval operates in its own namespace and can't access turtle stuff
			   (pen-up turtle) ; using an unitialized turtle
					; strangely the exception is that a function cannot be converted into a ref
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
    ;(draw-polygon [100 100  100 -100  -100 -100  -100 100])
    
    ;(draw-spiral 16)
    ;(draw-spiral2 6 [100 100] 10)
    ;(draw-spiral3 50)
    (test-turtle)
					;(test-exceptions)
    ;(test-nth)
    (catch Throwable e (println (errors/prettify-exception e)))))
