(ns homework.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [homework.types :as t]
            [java-time :as time]))

;;;; Common

(defn get-line-delimiter [line]
  (let [matches (re-matches t/record-line-regex line)]
    (nth matches 2)))

;;;; Write to datastore

(defn write-record-line [line]
  (let [file (case (get-line-delimiter line)
               " " "resources/space-input.txt"
               ", " "resources/comma-input.txt"
               " | " "resources/pipe-input.txt")]
    (spit file (str line "\n") :append true)))

;;;; Read from datastore

;; Assumes properly formatted row
(defn row->record [row]
  {:last-name (nth row 0)
   :first-name (nth row 1)
   :gender (nth row 2)
   :favorite-color (nth row 3)
   :date-of-birth (nth row 4)})

;; Assumes properly formatted line and correct delimiter
(defn line->record [delimiter line]
  (row->record (string/split line delimiter)))

;; assumes given file exists
;; assumes correct delimiter sent
;; does not enforce return type
(defn get-records-from-file
  "Read all records out of a given file.

  Given an input file and its delimiting character, return its contents as a
  lazy sequence of record maps.

  Assumes that given file exists."
  [input-file delimiter]
  (let [file-contents (slurp (io/resource input-file))]
    (map (partial line->record delimiter)
         (string/split file-contents #"\n"))))

(defn get-all-records []
  (concat
   (get-records-from-file "comma-input.txt" #", ")
   (get-records-from-file "pipe-input.txt" #" \| ")
   (get-records-from-file "space-input.txt" #" ")))

;; Could be extended to arbitrary number of ties with recursion over all keys
(defn comparator-with-tiebreaker
  [primary-key tiebreaker-key]
  (fn [left right]
    (if (= (primary-key left) (primary-key right))
      (compare (tiebreaker-key left) (tiebreaker-key right))
      (compare (primary-key left) (primary-key right)))))

(defn sort-by-gender
  "sorted by gender (females before males) then by last name ascending"
  [records]
  (let [key-fn identity
        comparator-fn (comparator-with-tiebreaker :gender :last-name)]
    (sort-by key-fn comparator-fn records)))

(defn sort-by-birthdate
  "sorted by birth date, ascending"
  [records]
  (sort-by :date-of-birth records))

(defn sort-by-name
  "Sorted by last name descending"
  [records]
  (let [key-fn identity
        comparator-fn (comparator-with-tiebreaker :last-name :first-name)]
    (reverse (sort-by key-fn comparator-fn records))))

(defn iso8601->mmddyyyy [date]
  (time/format "M/d/yyyy" (time/local-date "yyyy-MM-dd" date)))
(defn mmddyyyy->iso8601 [date]
  (time/format "yyyy-MM-dd" (time/local-date "M/d/yyyy" date)))

(defn convert-date-of-birth-format [records]
  (map #(update %1 :date-of-birth iso8601->mmddyyyy) records))

(defn record->string [delimiter record]
  (string/join delimiter [(:last-name record)
                          (:first-name record)
                          (:gender record)
                          (:favorite-color record)
                          (:date-of-birth record)]))

(defn records->string [delimiter records]
  (string/join "\n" (map (partial record->string delimiter)
                         records)))

(defn -main [& args]
  ;; sorting is easier before updating the date
  (let [records (get-all-records)
        ->display-fmt (fn [rs] (records->string " | " rs))]
    (println "Output 1:\n")
    (println (-> records
                 sort-by-gender
                 convert-date-of-birth-format
                 ->display-fmt))
    (println "\nOutput 2:\n")
    (println (-> records
                 sort-by-birthdate
                 convert-date-of-birth-format
                 ->display-fmt))
    (println "\nOutput 3:\n")
    (println (-> records
                 sort-by-name
                 convert-date-of-birth-format
                 ->display-fmt))))
