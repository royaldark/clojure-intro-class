(ns intro.core_test
  (:require [expectations :refer :all]
            [errors.prettify_exception :refer :all]
            [errors.exceptions :refer :all]))

(defn my-prettify-exception [e]
  (let [e-class (class e)
        m (.getMessage e)
        message (if m m "")] ; converting an empty message from nil to ""
    (get-pretty-message e-class message)))

(expect [{:msg "Attempted to use ", :stylekey :reg, :length 17}
         {:msg "a character", :stylekey :type, :length 11}
         {:msg ", but ", :stylekey :reg, :length 6}
         {:msg "a number", :stylekey :type, :length 8}
         {:msg " was expected.", :stylekey :reg, :length 14}]
          (my-prettify-exception
            (run-and-catch '(+ \a 2))))

;; Same as above, but putting all of the :msg pieces of the hash-maps together into
;; one string
(expect "Attempted to use a character, but a number was expected."
        (apply str (map #(:msg %)
            (my-prettify-exception
              (run-and-catch '(+ \a 2))))))

(expect [{:msg "Divide by zero", :stylekey :reg, :length 14}]
          (my-prettify-exception
            (run-and-catch '(/ 5 0))))

(expect [{:msg "Attempted to use ", :stylekey :reg, :length 17}
         {:msg "a number", :stylekey :type, :length 8}
         {:msg ", but ", :stylekey :reg, :length 6}
         {:msg "a collection", :stylekey :type, :length 12}
         {:msg " was expected.", :stylekey :reg, :length 14}]
          (my-prettify-exception
            (run-and-catch '(into 6 [1 2]))))

(expect [{:msg "An index in a sequence is out of bounds",
          :stylekey :reg,
          :length 39}]
        (my-prettify-exception
          (run-and-catch '(nth ["hello" "world" "I" "rock"] 9))))

(expect [{:msg "An index in a sequence is out of bounds",
          :stylekey :reg,
          :length 39}]
        (my-prettify-exception
          (run-and-catch '(nth (seq #{1 0}) 2))))

;################################################################
;### Skipped from duplicate-seq to a-nil-key, they seem hard. ###
;################################################################

(expect [{:msg "An attempt to access a non-existing object: ", :stylekey :reg, :length 44}
         {:msg "some message", :stylekey :arg, :length 12}
         {:msg "\n(NullPointerException)", :stylekey :reg, :length 23}]
        (my-prettify-exception
          (run-and-catch '(throw (new NullPointerException "some message")))))

(expect [{:msg "Don't know how to create ", :stylekey :reg, :length 25}
         {:msg "a sequence", :stylekey :type, :length 10}
         {:msg " from ", :stylekey :reg, :length 6}
         {:msg "a function", :stylekey :type, :length 10}]
        (my-prettify-exception
         (run-and-catch '(doall (concat [:banana] +)))))

(expect [{:msg "Don't know how to create ", :stylekey :reg, :length 25}
         {:msg "a sequence", :stylekey :type, :length 10}
         {:msg " from ", :stylekey :reg, :length 6}
         {:msg "a number", :stylekey :type, :length 8}]
        (my-prettify-exception
         (run-and-catch '(rest 1))))

(expect [{:msg "Attempted to use ", :stylekey :reg, :length 17}
         {:msg "a function", :stylekey :type, :length 10}
         {:msg ", but ", :stylekey :reg, :length 6}
         {:msg "a collection", :stylekey :type, :length 12}
         {:msg " was expected.", :stylekey :reg, :length 14}]
        (my-prettify-exception
         (run-and-catch '(conj + 1))))

;; This is a group
;;###################################################################
;(expect [{:msg "Don't know how to create ", :stylekey :reg, :length 25}
;         {:msg "a sequence", :stylekey :type, :length 10}
;         {:msg " from ", :stylekey :reg :length 19}
;         {:msg "name ", :stylekey :reg, :length 5}
;         {:msg "some?", :stylekey :arg, :length 5}
;         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}
;         {:msg "NO_SOURCE_PATH:105:26", :stylekey :arg, :length 21}]
;        (my-prettify-exception
;         (run-and-catch '(some? #(+ % 2) [:k :v]))))

;(expect [{:msg "Don't know how to create ", :stylekey :reg, :length 25}
;         {:msg "a sequence", :stylekey :type, :length 10}
;         {:msg " from ", :stylekey :reg :length 19}
;         {:msg "name ", :stylekey :reg, :length 5}
;         {:msg "some?", :stylekey :arg, :length 5}
;         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}
;         {:msg "NO_SOURCE_PATH:105:26", :stylekey :arg, :length 21}]
;        (my-prettify-exception
;         (run-and-catch '(some? #(+ % 2) [:k :v]))))

;(expect [{:msg "Don't know how to create ", :stylekey :reg, :length 25}
;         {:msg "a sequence", :stylekey :type, :length 10}
;         {:msg " from ", :stylekey :reg :length 19}
;         {:msg "name ", :stylekey :reg, :length 5}
;         {:msg "some?", :stylekey :arg, :length 5}
;         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}]
;        (butlast
;         (my-prettify-exception
;          (run-and-catch '(some? #(+ % 2) [:k :v])))))

;; This is a group
;;###################################################################
;(expect [{:msg "Compilation error: ", :stylekey :reg, :length 19}
;         {:msg "name ", :stylekey :reg, :length 5}
;         {:msg "add-first", :stylekey :arg, :length 9}
;         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}
;         {:msg "NO_SOURCE_PATH:97:26", :stylekey :arg, :length 20}]
;        (my-prettify-exception
;         (run-and-catch '(add-first 1 []))))

(expect [{:msg "Compilation error: ", :stylekey :reg, :length 19}
         {:msg "name ", :stylekey :reg, :length 5}
         {:msg "add-first", :stylekey :arg, :length 9}
         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}]
        (butlast
         (my-prettify-exception
           (run-and-catch '(add-first 1 [])))))

(expect #"NO_SOURCE_PATH"
        (:msg
         (last
           (my-prettify-exception
             (run-and-catch '(add-first 1 []))))))

;; This is a group
;;###################################################################
;(expect [{:msg "Compilation error: ", :stylekey :reg, :length 19}
;         {:msg "name ", :stylekey :reg, :length 5}
;         {:msg "some?", :stylekey :arg, :length 5}
;         {:msg " is undefined, while compiling ", :stylekey :reg, :length 31}
;         {:msg "NO_SOURCE_PATH:105:26", :stylekey :arg, :length 21}]
;        (my-prettify-exception
;         (run-and-catch '(some? #(+ % 2) [:k :v]))))
