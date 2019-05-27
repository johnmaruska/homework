(ns homework.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [homework.types :as t]))

(defn row->record [row]
  {:last-name (nth row 0)
   :first-name (nth row 1)
   :gender (nth row 2)
   :favorite-color (nth row 3)
   :date-of-birth (nth row 4)})

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

;; haven't dug enough into spec to know how to utilize these...
(comment
  ;; define the spec for the function! I haven't done anything with spec before
  (s/fdef get-all-records
    :args (s/cat :input-file string?
                 :delimiter #(= java.util.regex.Matcher
                                (type %1)))
    :ret (s/coll-of ::t/record)))

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
  (let [date-row (string/split date #"-")
        year (nth date-row 0)
        month (nth date-row 1)
        day (nth date-row 2)]
    (format "%s/%s/%s"
            (Integer/parseInt month)
            (Integer/parseInt day)
            year)))

(defn convert-date-of-birth-format [records]
  (map #(update %1 :date-of-birth iso8601->mmddyyyy) records))

(defn -main [& args]
  ;; sorting is easier before updating the date
  (let [records (get-all-records)]
    (println "Step 1:\n")
    (println "\nOutput 1:\n")
    (clojure.pprint/pprint
     (-> records
         sort-by-gender
         convert-date-of-birth-format))
    (println "\nOutput 2:\n")
    (clojure.pprint/pprint
     (-> records
         sort-by-birthdate
         convert-date-of-birth-format))
    (println "\nOutput 3:\n")
    (clojure.pprint/pprint
     (-> records
         sort-by-name
         convert-date-of-birth-format))))
