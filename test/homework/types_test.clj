(ns homework.types-test
  (:require [homework.types :as sut]
            [clojure.test :as t :refer [deftest is testing]]))

(deftest record-line-regex
  (testing "record-line-regex"
    (testing "recognizes space-delimited"
      (is (re-matches sut/record-line-regex
                      "lastName firstName Male color 1999-09-09")))
    (testing "recognizes comma-delimited"
      (is (re-matches sut/record-line-regex
                      "lastName, firstName, Male, color, 1999-09-09")))
    (testing "recognizes pipe-delimited"
      (is (re-matches sut/record-line-regex
                      "lastName | firstName | Male | color | 1999-09-09")))))
