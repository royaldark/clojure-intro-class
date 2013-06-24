(ns corefns.core
  (:use [clojure.core.incubator])
  (:refer-clojure :exclude [map nth]))

;; to-do:
;; 1. remove references to contracts, trammel (from project.clj as well) - Done
;; 2. modify our type-checking functions to record the type (or the arg? or the message?) - Done
;; 3. modify error-handling for asserts to check the recorded info - Done
;; 4. rewrite messages similar to the standard ones (perhaps abstract over?) - maybe? 
;; 5. don't forget to clear the queue at the end (post-cond? or the end of pre-cond? or finally?) --
;;    not in post-cond since if we got to post-cond, there were no errors. Perhaps after we
;;    process the queue? We aren't going to handle nested errors. finally may be the place - Done
;; 6. Handle multiple-args functions - done the prototype
;; 7. Process function names - Done
;; 8. Change the messsage for only one arg
;; 9. Handle anonymous functions
;; 10. Add a function name to the error message !!!!!
;; inf. why didn't I think of this earlier? 

;; a global hashmap of recorded types/messages
(def seen-objects (atom {}))

(defn add-to-seen [binds] 
  "adds bindings to seen objects"
  (swap! seen-objects merge binds)) ; merge overwrites the same fields but adds new ones 

(defn empty-seen [] 
  "removes all bindings from seen objects"
  (swap! seen-objects {}))

;; Functions to check pre-conditions and record the offenders for 
;; error processing
(defn check-if-function? [fname x]
  (if (fn? x) true
  	      (do (add-to-seen {:check "function" 
  	      		        :class (class x)
  	      		        :value x
  	      		        :fname fname})
  	      	   false)))   


(defn check-if-seqable? [fname x & [n]]
  "returns true if x is seqable and false otherwise, sets data
  in seen-objects. If the second argument is present, it's added 
  to the seen-objects as the number of the argument"
  (if (seqable? x) true
  	      (do (add-to-seen {:check "sequence" 
  	      		        :class (class x)
  	      		        :value x
  	      		        :fname fname})
  	      	   (if n (add-to-seen {:arg-num n}))
  	      	   false)))  

(defn check-if-number? [fname x]
  (if (number? x) true
  	      (do (add-to-seen {:check "number" 
  	      		        :class (class x)
  	      		        :value x
  	      		        :fname fname})
  	      	   false)))  

;; should pass the strating arg number: it's different for different functions
(defn check-if-seqables? [fname arguments start]
  (loop [args arguments n start]
    (if (empty? args) true
      (if (not (check-if-seqable? fname (first args)))
      	(do (add-to-seen {:arg-num n})
            false)
        (recur (rest args) (inc n))))))

;; Including the standard Clojure documentation to make sure that asserts 
;; and cases are consistent with the standard Clojure. 

;; (map f coll)
;; (map f c1 c2)
;; (map f c1 c2 c3)
;; (map f c1 c2 c3 & colls)
;; Returns a lazy sequence consisting of the result of applying f to the
;; set of first items of each coll, followed by applying f to the set
;; of second items in each coll, until any one of the colls is
;; exhausted. Any remaining items in other colls are ignored. Function
;; f should accept number-of-colls arguments.
(defn map [argument1 & args]
 {:pre [(check-if-function? "map" argument1) (check-if-seqables? "map" args 2)]}
  (apply clojure.core/map argument1 args))

;; count, into, conj, nth, drop, take, concat, filter, reduce
;; Maps and the like: key, val, keys, vals - careful with pre-conds for key!
;; odd?, even?, etc - check for numbers!

;; (count coll)
;; Returns the number of items in the collection. (count nil) returns
;; 0. Also works on strings, arrays, and Java Collections and Maps
(defn count [argument1]
  {:pre [(check-if-seqable? "count" argument1)]}
  (clojure.core/count argument1))

;; (conj coll x)
;; (conj coll x & xs)
;; conj[oin]. Returns a new collection with the xs
;;'added'. (conj nil item) returns (item). The 'addition' may
;; happen at different 'places' depending on the concrete type.
(defn conj [argument1 & args]
  {:pre [(check-if-seqable? "conj" argument1)]}
  (apply clojure.core/conj argument1 args))

;; 
(defn into [argument1 argument2]
   {:pre [(check-if-seqable? "into" argument1) (check-if-seqable? "into" argument2)]}
   (clojure.core/into argument1 argument2))

;; (reduce f coll)
;; (reduce f val coll)
;; f should be a function of 2 arguments. If val is not supplied,
;; returns the result of applying f to the first 2 items in coll, then
;; applying f to that result and the 3rd item, etc. If coll contains no
;; items, f must accept no arguments as well, and reduce returns the
;; result of calling f with no arguments. If coll has only 1 item, it
;; is returned and f is not called. If val is supplied, returns the
;; result of applying f to val and the first item in coll, then
;; applying f to that result and the 2nd item, etc. If coll contains no
;; items, returns val and f is not called.
(defn reduce [argument1 & args]
   {:pre [(check-if-function? "reduce" argument1) (if (= (count args) 1)
   		   			     (check-if-seqable? "reduce" (first args) 2)
   		   			     (check-if-seqable? "reduce" (second args) 3))]}
   (apply clojure.core/reduce argument1 args))  
                                               


;; (nth coll index)
;; (nth coll index not-found)
;; Returns the value at the index. get returns nil if index out of
;; bounds, nth throws an exception unless not-found is supplied. nth
;; also works for strings, Java arrays, regex Matchers and Lists, and,
;; in O(n) time, for sequences.
(defn nth [argument1 argument2 & args] ;; there may be an optional 3rd arg, no restrictions 
   {:pre [(check-if-seqable? "nth" argument1) (check-if-number? "nth" argument2)]}
   (apply clojure.core/nth argument1 argument2 args))


;; (filter pred coll)
;; Returns a lazy sequence of the items in coll for which
;; (pred item) returns true. pred must be free of side-effects.
(defn filter [argument1 argument2]
  {:pre [(check-if-function? "filter" argument1) (check-if-seqable? "filter" argument2)]}
  (clojure.core/filter argument1 argument2))

;; (mapcat f & colls)
;; Returns the result of applying concat to the result of applying map
;; to f and colls. Thus function f should return a collection.
;; !!!! TODO: add a condition for the number of args to mapcat !!!!
;; (the error message refers to the number of args to map) 
(defn mapcat [argument1 & args]
  {:pre [(check-if-function? "mapcat" argument1) (check-if-seqables? "mapcat" args 2)]}
  (apply clojure.core/mapcat argument1 args))

;; (concat)
;; (concat x)
;; (concat x y)
;; (concat x y & zs)
;; Returns a lazy seq representing the concatenation of the elements in the supplied colls.
(defn concat [& args]
  {:pre [(check-if-seqables? "concat" args 1)]}
  (apply clojure.core/concat args))
		
;;;;; Functions for type-independent sequence handling ;;;;;;

(defn add-first [argument1 argument2]
  {:pre [(check-if-seqable? "add-first" argument1)]}
  (cons argument2 argument1))

(defn add-last [argument1 argument2]
  {:pre [(check-if-seqable? "add-last" argument1)]}
  (doall (concat argument1 [argument2])))


;; user-friendly versions of confusing functions
(defn contains-value? [coll x]
	(let [values (if (map? coll) (vals coll) coll)]
		(not (every? #(not= x %) values))))

(def contains-key? contains?)
		
;; more content tests 
(defn any? [pred coll] (not (not-any? pred coll))) ; yes, I know :-(
(defn some? [pred coll] (not (not-any? pred coll)))

;; String functions
