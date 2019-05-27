(ns homework.handler
  (:require [compojure.api.sweet :as sweet :refer :all]
            [ring.util.http-response :as http :refer :all]
            [homework.core :as core]))


(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Records"
                   :description "API for records in homework assignment"}
            :tags [{:name "records", :description "records api"}]}}}
   (context "/records" []
     :tags ["records"]
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
                              core/convert-date-of-birth-format)})))))
