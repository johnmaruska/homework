(ns homework.handler
  (:require [clojure.spec.alpha :as s]
            [compojure.api.sweet :as sweet]
            [homework.core :as core]
            [homework.types :as t]
            [ring.util.http-response :as http]))

(def app
  (sweet/api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Records"
                   :description "API for records in homework assignment"}
            :tags [{:name "records", :description "records api"}]}}}
   (sweet/context "/records" []
     :tags ["records"]
     :coercion :spec
     (sweet/GET "/gender" []
       (http/ok {:results (-> (core/get-all-records)
                              core/sort-by-gender
                              core/convert-date-of-birth-format)}))
     (sweet/GET "/birthdate" []
       (http/ok {:results (-> (core/get-all-records)
                              core/sort-by-birthdate
                              core/convert-date-of-birth-format)}))
     (sweet/GET "/name" []
       (http/ok {:results (-> (core/get-all-records)
                              core/sort-by-name
                              core/convert-date-of-birth-format)}))
     (sweet/POST "/" []
       :return String
       :body [line String]
       :summary "Writes a space separated record line to the datastore"
       (if (s/valid? ::t/record-line line)
         (do
           (core/write-record-line line)
           (http/created line))
         (http/bad-request
          (str "Must format body `lastName firstName gender favoriteColor"
               " dateOfBirth` where dateOfBirth is yyyy-mm-dd")))))))
