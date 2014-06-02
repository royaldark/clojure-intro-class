(ns strings.core)

;#######################
;## Better String Fns ##
;#######################

;String Library

;Emma Sax, Aaron Lemmon, Henry Fellows

;Author: Emma Sax

;;; make-string: sequence of escaped characters -> string
(defn make-string
  "Takes a word as a sequence of escaped characters and returns
  a string of the characters, returns an empty string if an empty sequence
  is given."
  [sequence-of-chars]
  {:pre [(sequential? sequence-of-chars)]
   :post [(string? %)]}
  (apply str sequence-of-chars))

;;; index-of: string, string, optional number -> number
(defn index-of
  "Takes two strings and an optional index number and returns the
  index of the first character of the first appearance of the
  substring, returns -1 if the second string isn't a substring of the
  first string or the index is larger than the last index of the first string
  or if the index is negative."
  ([string substring]
   {:pre [(string? string) (string? substring)]
    :post [(number? %)]}
   (.indexOf string substring))
  ([string substring index]
   {:pre [(string? string) (string? substring) (number? index)]
    :post [(number? %)]}
   (.indexOf string substring index)))

;;; last-index-of: string, string, optional number -> number
(defn last-index-of
  "Takes two strings and an optional index number and returns the
  index of the first character of the last appearance of the
  substring by searching backward through the string; else returns -1."
  ([string substring]
   {:pre [(string? string) (string? substring)]
    :post [(number? %)]}
   (.lastIndexOf string substring))
  ([string substring index]
   {:pre [(string? string) (string? substring) (number? index)]
    :post [(number? %)]}
   (.lastIndexOf string substring index)))

;;; append: any amount of strings -> string
(defn append
  "Takes any number of strings and returns a string which appends them together,
  returns an empty string if there are no strings given or if the string(s) given
  are all nil."
  [& strings]
  {:pre [(or (every? string? strings) (not (not-any? nil? strings)))]
   :post [(string? %)]}
  (apply str strings))

;;; glyph-at: string, number -> string
(defn glyph-at
  "Takes a string and an index and returns a string version of the character
  at the given index, returns an empty string if the index is bigger than the
  largest index of the string or if the index is negative."
  [string index]
  {:pre [(string? string) (number? index)]
   :post [(string? %)]}
  (str (get string index)))

;;; empty-string?: string -> boolean
(defn empty-string?
  "Takes a string and returns true if the length of the string is 0, returns false
  otherwise."
  [string]
  {:pre [(string? string)]
   :post [(or (true? %) (false? %))]}
  (zero? (count string)))

;;; first-of-string: string -> string
(defn first-of-string
  "Takes a string and returns the first character of the string as a string."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (str (first string)))

;;; last-of-string: string -> string
(defn last-of-string
  "Takes a string and returns the last character of the string as a string."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (str (last string)))

;;; rest-of-string: string -> string
(defn rest-of-string
  "Takes a string and returns a new string with all of the elements in order
  except for the first character."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (apply str (rest string)))

;;; second-of-string: string -> string
(defn second-of-string
  "Takes a string and returns the second character of the string as a string."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (str (second string)))

;;; contains-string?: string, string -> boolean
(defn contains-string?
  "Takes two strings and returns true if the first string contains the second
  string, returns false otherwise."
  [string characters]
  {:pre [(string? string) (string? characters)]
   :post [(or (true? %) (false? %))]}
  (.contains string characters))

;;; reverse-string: string -> string
(defn reverse-string
  "Takes a string and returns a new string with the charactes reversed."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (apply str (reverse string)))

;;; to-upper-case: string -> string
(defn to-upper-case
  "Takes a string and returns an new string with all of the characters
  as upper case."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (clojure.string/upper-case string))

;;; to-lower-case: string -> string
(defn to-lower-case
  "Takes a stringa and returns a new string with all of the characters
  as lower case."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (clojure.string/lower-case string))

;;; capitalize: string -> string
(defn capitalize
  "Takes a string and makes the first character uppercase and the the rest
  lowercase."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (clojure.string/capitalize string))

;;; drop-from-string: number, string -> string
(defn drop-from-string
  "Takes an index and a string and returns a new string which drops all of
  the characters from 0-index (inclusive)."
  [index string]
  {:pre [(number? index) (string? string)]
   :post [(string? %)]}
  (apply str (drop index string)))

;;; take-from-string: number, string -> string
(defn take-from-string
  "Takes a string and an index and returns a new string with only the characters
  from 0-index (exclusive)."
  [index string]
  {:pre [(number? index) (string? string)]
   :post [(string? %)]}
  (apply str (take index string)))

;;; join-string: string, string -> string
(defn join-string
  "Takes either one string and returns itself or takes two strings and
  returns a new string with the second string's characters separated by
  the first string."
  ([string]
   {:pre [(string? string)]
   :post [(string? %)]}
   (clojure.string/join string))
  ([separator string]
   {:pre [(string? separator) (string? string)]
   :post [(string? %)]}
   (clojure.string/join separator string)))

;;; replace-string: string, string, string -> string
(defn replace-string
  "Takes three strings, finds the substring of the second string in the first
  string, and then returns a new string with the substring of the first string
  replaced with the third string."
  [string match replacement]
  {:pre [(string? string) (string? match) (string? replacement)]
   :post [(string? %)]}
  (clojure.string/replace string match replacement))

;;; split-string: string, string, optional number -> vector
(defn split-string
  "Takes a string and returns a vector which is the string but split on a
  regular expression, or could also take a number limit that is the maximum
  number of splits (puts the rest of the characters into the last split)."
  ([string re]
   {:pre [(string? string)]
    :post [(vector? %)]}
   (clojure.string/split string re))
  ([string re limit]
   {:pre [(string? string) (number? limit)]
    :post [(vector? %)]}
   (clojure.string/split string re limit)))

;;; split-lines-string: string -> string
(defn split-lines-string
  "Takes a string and returns a vector which take the string and splits it
  on \n or \r\n."
  [string]
  {:pre [(string? string)]
   :post [(vector? %)]}
  (clojure.string/split-lines string))

;;; replace-first-of-string: string, string, string -> string
(defn replace-first-of-string
  "Takes a string and returns a string with the first occurrence of the second
  string in the first string replaced by the third string, if the second string
  doesn't occur in the first string, it just returns the first string."
  [string match replacement]
  {:pre [(string? string) (string? match) (string? replacement)]
   :post [(string? %)]}
  (clojure.string/replace-first string match replacement))

;;; trim-string: string -> string
(defn trim-string
  "Takes a string and returns a string with whitespace from both ends of the
  string removed."
  [string]
  {:pre [(string? string)]
   :post [(string? %)]}
  (clojure.string/trim string))

;;; string-blank?: string -> boolean
(defn string-blank?
  "Takes a string and returns true if the string is nil, empty, false, or contains
  only whitespace."
  [string]
  {:pre [(or (string? string) (nil? string) (false? string))]
   :post [(or (true? %) (false? %))]}
  (clojure.string/blank? string))
