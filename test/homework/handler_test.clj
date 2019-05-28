(ns homework.handler-test
  (:require [cheshire.core :as json]
            [clojure.test :refer [deftest testing is] ]
            [homework.core :as core]
            [homework.handler :as handler]
            [homework.types :as t]
            [ring.mock.request :as mock]
            [clojure.spec.alpha :as s]))

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

(deftest post-record
  (testing "POST /records"
    (testing "comma-delimited gives 201 Created"
      (let [request-body "\"Reynolds, Malcolm, Male, Brown, 2553-10-10\""
            response (handler/app (-> (mock/request :post "/records")
                                      (mock/content-type "application/json")
                                      (mock/body request-body)))]
        (is (= (:status response) 201))))
    (testing "space-delimited gives 201 Created"
      (let [request-body "\"Reynolds Malcolm Male Brown 2553-10-10\""
            response (handler/app (-> (mock/request :post "/records")
                                      (mock/content-type "application/json")
                                      (mock/body request-body)))]
        (is (= (:status response) 201))))
    (testing "pipe-delimited gives 201 Created"
      (let [request-body "\"Reynolds | Malcolm | Male | Brown | 2553-10-10\""
            response (handler/app (-> (mock/request :post "/records")
                                      (mock/content-type "application/json")
                                      (mock/body request-body)))]
        (is (= (:status response) 201))))
    (testing "unexpected format gives 400 Bad Request"
      (let [request-body "\"Reynolds__Malcolm__Male__Brown__2553-10-10\""
            response (handler/app (-> (mock/request :post "/records")
                                      (mock/content-type "application/json")
                                      (mock/body request-body)))]
        (is (= (:status response) 400))))))
