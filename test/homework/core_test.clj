(ns homework.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [homework.core :as sut]
            [homework.types :as t]
            [clojure.spec.alpha :as s]))

;;;; Write to datastore
(deftest write-record-line
  (testing "space-delimited entries write to space-input.txt"
    (let [expected-file "resources/space-input.txt"
          input "lastName firstName Male color 1999-09-09"
          spit-called? (atom false)]
      (with-redefs [spit (fn [file line & opts]
                           (is (= expected-file file))
                           (is (= (str input "\n") line))
                           (reset! spit-called? true))]
        (sut/write-record-line input)
        (is @spit-called?))))
  (testing "comma-delimited entries write to comma-input.txt"
    (let [expected-file "resources/comma-input.txt"
          input "lastName, firstName, Male, color, 1999-09-09"
          spit-called? (atom false)]
      (with-redefs [spit (fn [file line & opts]
                           (is (= expected-file file))
                           (is (= (str input "\n") line))
                           (reset! spit-called? true))]
        (sut/write-record-line input)
        (is @spit-called?))))
  (testing "pipe-delimited entries write to pipe-input.txt"
    (let [expected-file "resources/pipe-input.txt"
          input "lastName | firstName | Male | color | 1999-09-09"
          spit-called? (atom false)]
      (with-redefs [spit (fn [file line & opts]
                           (is (= expected-file file))
                           (is (= (str input "\n") line))
                           (reset! spit-called? true))]
        (sut/write-record-line input)
        (is @spit-called?)))))

;;;; Read from datastore

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
    (let [file "pipe-input.txt"]
      (is (every? #(s/valid? ::t/record %1)
                  (sut/get-records-from-file file #" \| "))))))

(deftest get-all-records
  (testing "joins all record files contents"
    (let [comma-records (sut/get-records-from-file "comma-input.txt" #", ")
          pipe-records (sut/get-records-from-file "pipe-input.txt" #" \| ")
          space-records (sut/get-records-from-file "space-input.txt" #" ")]
      ;; set comparison because we're assuming unordered and I don't want the
      ;; test to break if file reading order changes
      (is (= (set (concat comma-records pipe-records space-records))
             (set (sut/get-all-records)))))))

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

(deftest sort-by-gender
  (let [original-order [townsend-record
                        sansone-record
                        maruska-record
                        hamilton-record]
        expected-order (seq [hamilton-record
                             maruska-record
                             townsend-record
                             sansone-record])]
    (is (= expected-order
           (sut/sort-by-gender original-order)))))

(deftest sort-by-birthdate
  (let [original-order [hamilton-record
                        maruska-record
                        sansone-record
                        townsend-record]
        expected-order (seq [hamilton-record
                             townsend-record
                             sansone-record
                             maruska-record])]
    (is (= expected-order
           (sut/sort-by-birthdate original-order)))))

(deftest sort-by-name
  (let [original-order [hamilton-record
                        maruska-record
                        sansone-record
                        townsend-record]
        expected-order (seq [townsend-record
                             sansone-record
                             maruska-record
                             hamilton-record])]
    (is (= expected-order
           (sut/sort-by-name original-order)))))

(deftest iso8601->mmddyyyy
  (is (= "5/5/1995"
         (sut/iso8601->mmddyyyy "1995-05-05"))))
(deftest mmddyyyy->iso8601
  (is (= "1995-05-05"
         (sut/mmddyyyy->iso8601 "5/5/1995"))))

(deftest record->string
  (let [record {:last-name "lastName"
                :first-name "firstName"
                :gender "Other"
                :favorite-color "some"
                :date-of-birth "2000-01-01"}
        string-record "lastName | firstName | Other | some | 2000-01-01"]
    (is (= string-record (sut/record->string " | " record)))))

(deftest records->string
  (let [record-a {:last-name "lastName"
                  :first-name "firstName"
                  :gender "Other"
                  :favorite-color "some"
                  :date-of-birth "2000-01-01"}
        record-b {:last-name "bertie"
                  :first-name "tuca"
                  :gender "Female"
                  :favorite-color "Blue"
                  :date-of-birth "2019-05-17"}
        expected (str (sut/record->string " " record-a)
                      "\n"
                      (sut/record->string " " record-b))
        actual (sut/records->string " " [record-a record-b])]
    (is (= expected actual))))

(deftest convert-date-of-birth-format
  (let [original-records [{:date-of-birth "2000-01-01"}
                          {:date-of-birth "1995-05-05"}
                          {:date-of-birth "1995-05-12"}
                          {:date-of-birth "1970-01-01"}]
        expected-records [{:date-of-birth "1/1/2000"}
                          {:date-of-birth "5/5/1995"}
                          {:date-of-birth "5/12/1995"}
                          {:date-of-birth "1/1/1970"}]]
    (is (= expected-records
           (sut/convert-date-of-birth-format original-records)))))
