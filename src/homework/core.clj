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

(defn sort-for-first-output
  "sorted by gender (females before males) then by last name ascending"
  [records]
  (sort-by identity
           (fn [left right]
             (if (= (:gender left) (:gender right))
               (compare (:last-name left) (:last-name right))
               (compare (:gender left) (:gender right))))
           records))

(defn sort-for-second-output
  "sorted by birth date, ascending"
  [records]
  (sort-by :date-of-birth records))

(defn sort-for-third-output
  "Sorted by last name descending"
  [records]
  (reverse (sort-by :last-name records)))

(defn -main [& args]
  (let [records (get-all-records)]
    (println "Step 1:\n")
    (println "  Output 1:")
    (clojure.pprint/pprint (sort-for-first-output records))
    (println "\n  Output 2:")
    (clojure.pprint/pprint (sort-for-second-output records))
    (println "\n  Output 3:")
    (clojure.pprint/pprint (sort-for-third-output records))))
