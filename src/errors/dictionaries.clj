(ns errors.dictionaries)


(def type-dictionary {:java.lang.String "string"
                      :java.lang.Number "number"})

(defn get-type [t]
  ((keyword t) type-dictionary))

(defn replace-types [f]
  (fn [matches] (f (map get-type (rest matches)))))

(def error-dictionary [{:class ClassCastException
                   :match #"(.*) cannot be cast to (.*)"
                   :replace (replace-types #(str "Attempted to use " (nth %1 0) ", but " (nth %1 1) " was expected."))}])
