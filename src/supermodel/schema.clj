(ns supermode.schema
  (:require [schema.core :as s]))

(def type-map
  {:keyword s/Keyword
   :boolean s/Bool
   :string s/Str
   :uuid s/Uuid
   :inst s/Inst
   
   :long s/Num
   :bigint s/Num
   :float s/Num
   :bigdec s/Num
   :ref s/Num})

(def type-map-java
  (merge type-map {:long java.lang.Long
                   :bigint (s/either clojure.lang.BigInt java.math.BigInteger)
                   :float java.lang.Float
                   :bigdec java.math.BigDecimal
                   :ref java.lang.Long}))

(defn make-schema-map
  "Given a model, turns into a map of entities to
  their schema maps"
  [model type-map]
  (into {}
        (for [[entity fields] model]
          {entity
           (into {}
                 (for [[field props] fields]
                   [(if (-> props :required false?)
                      (s/optional-key field)
                      field)
                    (-> props :type type-map)]))})))


