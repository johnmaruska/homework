(ns homework.types
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(defn mk-line-regex
  "Convert the regex pattern string to a Matcher that enforces a full line span"
  [pattern]
  (re-pattern (str "^" pattern "$")))

;; name * instead of + because technically possible to not have a full name
;; e.g. no last name
(def name-pattern "[A-Za-z]*")
(s/def ::first-name
  (s/and string? #(re-matches (mk-line-regex name-pattern) %)))
(s/def ::last-name
  (s/and string? #(re-matches (mk-line-regex name-pattern) %)))
(def gender-pattern "(Female|Male|Other)")
(s/def ::gender
  (s/and string? #(re-matches (mk-line-regex gender-pattern) %)))
(def color-pattern "[A-Za-z]+")
(s/def ::favorite-color
  (s/and string? #(re-matches (mk-line-regex color-pattern) %)))
(def date-of-birth-pattern "[0-9]{4}-[0-9]{2}-[0-9]{2}")
(s/def ::date-of-birth
  (s/and string? #(re-matches (mk-line-regex date-of-birth-pattern) %)))
(s/def ::record (s/keys :req-un [::first-name
                                 ::last-name
                                 ::gender
                                 ::favorite-color
                                 ::date-of-birth]))
(def record-line-pattern
  (string/join "( \\| |, | )" [name-pattern
                               name-pattern
                               gender-pattern
                               color-pattern
                               date-of-birth-pattern]))
(def record-line-regex (mk-line-regex record-line-pattern))
(s/def ::record-line
  (s/and string? #(re-matches record-line-regex %)))
