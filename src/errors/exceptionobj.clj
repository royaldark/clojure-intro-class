(ns errors.exceptionobj
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [errors.messageobj]))

(defn exception->exceptionobj
  ""
  [e]
  (let [ename nil
	msg-obj nil
        st nil
        fst nil
        hint nil]
    {:exception-name ename
     :message-object msg-obj
     :stacktrace st
     :filtered-stacktrace fst
     :hints hint}))
