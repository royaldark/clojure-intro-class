(ns errors.messageobj)
  ;(:refer corefn/core :only [add-fisrt add-last]))
;; Functions related to a message object. Message object 
;; is a vector of parts of a message (in order). Each 
;; part is a hash map that contains the message text :msg,
;; the formatting id (e.g. :reg), the length of the text
;; :length, and the start of the message :start
;; A message pre-object doesn't have :start

(defn make-msg-preobj-hash 
	"creates a hash map for a msg pre-object out of a msg and style"
	([msg style] (let [m (str msg)]
			{:msg m :stylekey style :length (count m)}))
	([msg] (let [m (str msg)] 
			{:msg m :stylekey :reg :length (count m)})))

(defn- make-msg-preobj-hashes-helper [messages result]
	(if (empty? messages) result
		(let [next (second messages)]
			(if (keyword? next) (recur (rest (rest messages))
					           (conj result (make-msg-preobj-hash (first messages) next)))
				            (recur (rest messages)
				            	   (conj result (make-msg-preobj-hash (first messages))))))))

(defn make-preobj-hashes [& args]
	"creates a vector of hash maps out of a vector that are strings, possibly followed by optional keywords"
	(make-msg-preobj-hashes-helper args []))

;(defn make-preobj-hashes [messages] 
;	"creates a vector of hash maps out of a vector of vectors of msg + optional style"
;	;; apply is needed since messages contains vectors of 1 or 2 elements
;	(map #(apply make-msg-preobj-hash %) messages))

(defn make-obj [pre-obj] ; pre-obj is a vector of hashmaps
  "fills in the starting points of objects in the hash maps"
  (loop [hashes pre-obj start 0 res []]
    (if (empty? hashes) res
      (recur (rest hashes) 
      	     (+ start (:length (first hashes)))
      	     (conj res (assoc (first hashes) :start start))))))

(defn get-all-text [msg-obj]
   "concatenate all text from a message object into a string"
   (reduce #(str %1 (:msg %2)) "" msg-obj))

(defn make-mock-preobj [matches]
  "creates a test message pre-obj. Used for testing so that things don't break"
  (make-preobj-hashes "This is a" "test" :arg))

