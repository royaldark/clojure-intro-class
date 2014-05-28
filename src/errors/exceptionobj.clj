(ns errors.exceptionobj
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [errors.messageobj]))

(defn exception->exceptionobj
  ""
  [e]
  (let [msg-obj nil
        st nil
        fst nil
        hint nil]
    {:message-object msg-obj
     :stacktrace st
     :filtered-stacktrace fst
     :hints hint}))
