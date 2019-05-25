(ns homework.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [homework.types :as t]))

(defn row->record [row]
  {:last-name (nth row 0)
   :first-name (nth row 1)
   :gender (nth row 2)
   :favorite-color (nth row 3)
   :date-of-birth (nth row 4)})

(defn line->record [delimiter line]
  (row->record (str/split line delimiter)))

;; assumes given file exists
;; assumes correct delimiter sent
;; does not enforce return type
(defn get-all-records
  "Read all records out of a given file.

  Given an input file and its delimiting character, return its contents as a
  lazy sequence of record maps.

  Assumes that given file exists."
  [input-file delimiter]
  (let [lines (-> input-file
                  io/resource
                  slurp
                  (str/split #"\n"))]
    (map (partial line->record delimiter)
         lines)))

;; haven't dug enough into spec to know how to utilize these...
(comment
  ;; define the spec for the function! I haven't done anything with spec before
  (s/fdef get-all-records
    :args (s/cat :input-file string?
                 :delimiter #(= java.util.regex.Matcher
                                (type %1)))
    :ret (s/coll-of ::t/record)))
