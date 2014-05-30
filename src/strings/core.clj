(ns strings.core)

;#######################
;## Better String Fns ##
;#######################

;String Library

;Emma Sax, Aaron Lemmon, Henry Fellows.

;;; make-string: sequence of escaped characters -> string
(defn make-string
  "Takes a word as a sequence of escaped characters and turns
  it into a string."
  [word-as-sequence]
  (apply str word-as-sequence))

;;; index-of: string, string, optional number -> number
(defn index-of
  "Takes two strings and an optional index number and returns the
  index of the first character of the first appearance of the
  substring, returns -1 if the second string isn't a substring of the
  first string or the index is larger than the last index of hte first string."
  ([string substring]
   (.indexOf string substring))
  ([string substring index]
   (.indexOf string substring index)))

;;; last-index-of: string, string, optional number -> number
(defn last-index-of
  "Takes two strings and an optional index number and returns the
  index of the first character of the last appearance of the
  substring by searching backward through the string; else returns -1."
  ([string substring]
   (.lastIndexOf string substring))
  ([string substring index]
   (.lastIndexOf string substring index)))

;;; append: any amount of strings -> string
(defn append
  "Takes any number of strings and appends them together, returns an empty string
  if there are no strings given or if the string(s) given are nil."
  [& strings]
  (apply str strings))

;;; substring: string, number, optional number -> string
(defn substring
  "Takes a string and 1-2 index numbers and returns a substring from either the
  given index to the end of the string or from the first index (inclusive) to
  the second index (exclusive), throws IndexOutOfBoundsException if the first
  index is negative, either index is larger than the length of hte string, or if
  the first index is larger than the second index."
  ([string index]
   (.substring string index))
  ([string begin-index end-index]
   (.substring string begin-index end-index)))

;;; glyph-at: string, number -> string
(defn glyph-at
  "Takes a string and an index and returns a string version of the character
  at the given index, throws StringIndexOutOfBoundsException if the index is
  bigger than the largest index of the string or if the index is negative."
  [string index]
  (str (.charAt string index)))

;;; string-length: string -> number
(defn string-length
  "Takes a string and returns the number of characters in the string."
  [string]
  (.length string))

;;; empty-string: string -> boolean
(defn empty-string?
  "Takes a string and returns true if the length of the string is 0, returns false
  otherwise."
  [string]
  (zero? (string-length string)))

;;; first-string: string -> string
(defn first-string
  "Takes a string and returns the first character of the string as a string."
  [string]
  (str (first string)))

;;; last-string: string -> string
(defn last-string
  "Takes a string and returns the last character of the string as a string."
  [string]
  (str (last string)))

;;; contains-string: string, string -> boolean
(defn contains-string
  "Takes two strings and returns true if the first string contains the second
  string, returns false otherwise."
  [string characters]
  (.contains string characters))

;;; compare-to: string, string -> number
(defn compare-to
  "Takes two strings and returns 0 if they are equal, a number less than 0 if
  the first string is lexigraphically less than the second string, and a number
  greater than 0 if the first string is lexigraphically greater than the second
  string, throws IllegalArgumentException if more than two strings are given or
  if less than two strings are given."
  [string1 string2]
  (.compareTo string1 string2))

;;; reverse-string: string -> string
(defn reverse-string
  "Takes a string and reverses all of the characters."
  [string]
  (apply str (reverse string)))









