(ns homework.core
  (:require [clojure.string :as str]))

(defn row->record [row]
  {:last-name (nth row 0)
   :first-name (nth row 1)
   :gender (nth row 2)
   :favorite-color (nth row 3)
   :date-of-birth (nth row 4)})

(defn pipe-input->record [input]
  (row->record (str/split input #" \| ")))
(defn comma-input->record [input]
  (row->record (str/split input #", ")))
(defn space-input->record [input]
  (row->record (str/split input #" ")))
