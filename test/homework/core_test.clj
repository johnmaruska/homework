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
        (is (= expected-record) (sut/line->record #" \| " input))))
    (testing "parses comma delimited input : `, `"
      (let [input "Maruska, John, Male, Green, 1995-05-05"]
        (is (= expected-record (sut/line->record #", " input)))))
    (testing "parses space delimited input : ` `"
      (let [input "Maruska John Male Green 1995-05-05"]
        (is (= expected-record (sut/line->record #" " input)))))))

(deftest get-all-records
  (testing "converts all entries to records"
    (let [file "./test/pipe-input.txt"]
      (is (every? #(s/valid? ::t/record %1)
                  (sut/get-all-records file #" \| "))))))
