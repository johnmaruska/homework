(ns homework.handler-test
  (:require [cheshire.core :as json]
            [clojure.test :as t :refer [deftest testing is] ]
            [homework.handler :as handler]
            [ring.mock.request :as mock]
            [homework.core :as core]))

(defn parse-body [body]
  (json/parse-string (slurp body) true))

(deftest get-records-by-gender
  (testing "GET /records/gender"
    (let [response (handler/app (-> (mock/request :get "/records/gender")))
          body (parse-body (:body response))]
      (testing "has status 200 OK"
        (is (= (:status response) 200)))
      (testing "responds with results sorted by gender"
        (is (= (:results body) (core/sort-by-gender (:results body)))))
      (testing "responds with results that have proper date format"
        (is (every? #(re-matches #"\d{1,2}/\d{1,2}/\d{4}"
                                 (:date-of-birth %1))
                    (:results body)))))))

(deftest get-records-by-birthdate
  (testing "GET /records/birthdate"
    (let [response (handler/app (-> (mock/request :get "/records/birthdate")))
          body (parse-body (:body response))]
      (testing "has status 200 OK"
        (is (= (:status response) 200)))
      (testing "responds with results sorted by birthdate"
        ;; check that Year of birth is monotonically increasing.
        ;; not 100% accurate but will at show that sort was called.
        ;; unit tests around the sort for verifying the sort function itself
        (let [dob->year-int (fn [record]
                              (->> (:date-of-birth record)
                                   (take-last 4)
                                   (clojure.string/join "")
                                   Integer/parseInt))]
          (is (apply <= (map dob->year-int (:results body))))))
      (testing "responds with results that have proper date format"
        (is (every? #(re-matches #"\d{1,2}/\d{1,2}/\d{4}"
                                 (:date-of-birth %1))
                    (:results body)))))))

(deftest get-records-by-name
  (testing "GET /records/name"
    (let [response (handler/app (-> (mock/request :get "/records/name")))
          body (parse-body (:body response))]
      (testing "has status 200 OK"
        (is (= (:status response) 200)))
      (testing "responds with results sorted by name"
        (is (= (:results body) (core/sort-by-name (:results body)))))
      (testing "responds with results that have proper date format"
        (is (every? #(re-matches #"\d{1,2}/\d{1,2}/\d{4}"
                                 (:date-of-birth %1))
                    (:results body)))))))
