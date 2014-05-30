(ns exceptions.generate1)

;###################
;### STATIC FILE ###
;## DO NOT CHANGE ##
;### DO NOT RUN ####
;###################

;*** Environment ***
; Windows 7 v.6.1.7601 , Intel i7 4700MQ @ 2.40ghz 4 cores (8 hyperthreaded), 8gb ram

;*** Functions ***
(def path "exceptions/")

(defn- run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

(defn- export-to-file
  "Uses Java's Serializable to write a (java) object to a file"
  [obj filepath]
  (let [file-stream (java.io.FileOutputStream. filepath)
        obj-stream (java.io.ObjectOutputStream. file-stream)
        ]
    (.writeObject obj-stream obj)
    (.close obj-stream)
    (.close file-stream)
    (println (str "data saved in project folder or: " filepath))
  ))

(defn- import-from-file
  "Uses Java's Serializable to read a (java) object from a file"
  [filepath]
  (let [file-stream (java.io.FileInputStream. filepath)
        obj-stream (java.io.ObjectInputStream. file-stream)
        e (.readObject obj-stream)]
    (.close obj-stream)
    (.close file-stream)
    e))

(defn- write-objects-local
  "writes a java object to a file, creating it if it does not exist, in path (see errors.exceptions)"
  [object filename]
  (export-to-file object (str path filename)))

(defn- read-objects-local
  "reads a file in path (see errors.exceptions) as a java-object"
  [filename]
  (import-from-file (str path filename)))

;*** file generation ***

;ClassCastException
(comment

(write-objects-local (run-and-catch '(inc "duck.")) "ClassCast-1-1.ser")

(write-objects-local (run-and-catch '(+ 2 "goose.")) "ClassCast-1-2.ser")

;IllegalArgumentException

(write-objects-local (run-and-catch '(cons 1 2)) "IllegalArgument-1-1.ser")

(write-objects-local (run-and-catch '(contains? (seq [1 3 6]) 2)) "IllegalArgument-1-2.ser")

;IndexOutOfBoundsException

(write-objects-local (run-and-catch '(nth [1 2 3] 5)) "IndexOutOfBounds-1-1.ser")

;ArityException

(write-objects-local (run-and-catch '(conj 2)) "ArityException-1-1.ser")

;NullPointerException

(write-objects-local (run-and-catch '(+ nil 2)) "NullPointer-1-1.ser")

)
