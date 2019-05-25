(ns homework.core-test
  (:require [clojure.test :refer :all]
            [homework.core :as sut]
            [homework.types :as t]
            [clojure.spec.alpha :as s]))

(deftest line->record
  (let [expected-record {:first-name "John"
                         :last-name "Maruska"
                         :gender "Male"
                         :favorite-color "Green"
                         :date-of-birth "1995-05-05"}]
    (testing "parses pipe delimited input : ` | `"
      (let [input "Maruska | John | Male | Green | 1995-05-05"]
        (is (= expected-record (sut/line->record #" \| " input)))))
    (testing "parses comma delimited input : `, `"
      (let [input "Maruska, John, Male, Green, 1995-05-05"]
        (is (= expected-record (sut/line->record #", " input)))))
    (testing "parses space delimited input : ` `"
      (let [input "Maruska John Male Green 1995-05-05"]
        (is (= expected-record (sut/line->record #" " input)))))))

(deftest get-records-from-file
  (testing "converts all entries to records"
    (let [file "./test/pipe-input.txt"]
      (is (every? #(s/valid? ::t/record %1)
                  (sut/get-records-from-file file #" \| "))))))


(def hamilton-record {:first-name "Margaret"
                      :last-name "Hamilton"
                      :gender "Female"
                      :favorite-color "Orange"
                      :date-of-birth "1936-08-17"})

(def maruska-record {:first-name "John"
                     :last-name "Maruska"
                     :gender "Male"
                     :favorite-color "Green"
                     :date-of-birth "1995-05-05"})

(def sansone-record {:first-name "Ellis"
                     :last-name "Sansone"
                     :gender "Other"
                     :favorite-color "Steel"
                     :date-of-birth "1994-07-07"})

(def townsend-record {:first-name "Devin"
                      :last-name "Townsend"
                      :gender "Male"
                      :favorite-color "Blue"
                      :date-of-birth "1972-05-05"})

(deftest sort-for-first-output
  (let [original-order [townsend-record
                        sansone-record
                        maruska-record
                        hamilton-record]
        expected-order (seq [hamilton-record
                             maruska-record
                             townsend-record
                             sansone-record])]
    (is (= expected-order
           (sut/sort-for-first-output original-order)))))

(deftest sort-for-second-output
  (let [original-order [hamilton-record
                        maruska-record
                        sansone-record
                        townsend-record]
        expected-order (seq [hamilton-record
                             townsend-record
                             sansone-record
                             maruska-record])]
    (is (= expected-order
           (sut/sort-for-second-output original-order)))))

(deftest sort-for-third-outfit
  (let [original-order [hamilton-record
                        maruska-record
                        sansone-record
                        townsend-record]
        expected-order (seq [townsend-record
                             sansone-record
                             maruska-record
                             hamilton-record])]
    (is (= expected-order
           (sut/sort-for-third-output original-order)))))
