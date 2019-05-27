(ns homework.types
  (:require [clojure.spec.alpha :as s]))

(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::gender string?)
(s/def ::favorite-color string?)
(def date-of-birth-regex #"^[0-9]{4}-[0-9]{2}-[0-9]{2}")
(s/def ::date-of-birth (s/and string? #(re-matches date-of-birth-regex %)))
(s/def ::record (s/keys :req-un [::first-name
                                 ::last-name
                                 ::gender
                                 ::favorite-color
                                 ::date-of-birth]))
