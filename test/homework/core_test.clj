(ns homework.core-test
  (:require [clojure.test :refer :all]
            [homework.core :as sut]))

(def example-record
  {:first-name "John"
   :last-name "Maruska"
   :gender "Male"
   :favorite-color "Green"
   :date-of-birth "1995-05-05"})

(deftest pipe-input->record
  (testing "parses into expected record"
    (let [input "Maruska | John | Male | Green | 1995-05-05"
          expected {:last-name "Maruska"
                    :first-name "John"
                    :gender "Male"
                    :favorite-color "Green"
                    :date-of-birth "1995-05-05"}]
      (is (= expected (sut/pipe-input->record input))))))

(deftest comma-input->record
  (testing "parses into expected record"
    (let [input "Maruska, John, Male, Green, 1995-05-05"
          expected {:last-name "Maruska"
                    :first-name "John"
                    :gender "Male"
                    :favorite-color "Green"
                    :date-of-birth "1995-05-05"}]
      (is (= expected (sut/comma-input->record input))))))

(deftest space-input->record
  (testing "parses into expected record"
    (let [input "Maruska John Male Green 1995-05-05"
          expected {:last-name "Maruska"
                    :first-name "John"
                    :gender "Male"
                    :favorite-color "Green"
                    :date-of-birth "1995-05-05"}]
      (is (= expected (sut/space-input->record input))))))
