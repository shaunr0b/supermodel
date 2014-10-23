(ns supermodel.datomic
  (:require [datomic.api :as d]
            [clojure.set]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Private

(def datomic-property-value-map
  "Map specifying the conversion of a domain property-value to a
  datomic property ident."
  {:type        {:string :db.type/string
                 :long    :db.type/long
                 :keyword :db.type/keyword
                 :boolean :db.type/boolean
                 :bigint  :db.type/bigint
                 :float   :db.type/float
                 :double  :db.type/double
                 :bigdec  :db.type/bigdec
                 :ref     :db.type/ref
                 :uuid    :db.type/uuid
                 :instant :db.type/instant
                 :uri     :db.type/uri
                 :bytes   :db.type/bytes}
   :cardinality {:one    :db.cardinality/one
                 :many   :db.cardinality/many}
   :unique      {:identity :db.unique/identity
                 :value    :db.unique/value}})

(defn field->datomic-ident-map
  "Given a model and an entity-type returns a mapping of
  corresponding domain fields to datomic idents."
  [model entity-type]
  (let [properties (keys (get-in model [entity-type]))]
    (into {}
          (for [property properties]
            [property
             (keyword (name entity-type) (name property))]))))


(defn property-value->datomic-ident
  "Given a property and value, returns the corresponding datomic
  property ident."
  [property value]
  (if-let [return (get-in datomic-property-value-map
                          [property value])]
    return
    (throw (ex-info "Not a valid property-value selection."
                    {:property                   property
                     :value                      value
                     :datomic-property-value-map datomic-property-value-map}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public

(defn model->schema-tx-data
  "Converts a datom-model into datomic schema tx-data."
  [model]
  (for [[entity-type field-map] model
        [field property-map] field-map]
    (let [{:keys [type cardinality unique index doc fulltext
                  is-component no-history]
           :or {cardinality :one}} property-map]
      (cond-> {:db/id                             (d/tempid :db.part/db)
               :db/ident                          (keyword (name entity-type)
                                                           (name field))
               :db/valueType                      (property-value->datomic-ident
                                                   :type type)
               :db/cardinality                    (property-value->datomic-ident
                                                   :cardinality cardinality)
               :db.install/_attribute             :db.part/db}
              unique       (assoc :db/unique      (property-value->datomic-ident
                                                   :unique unique))
              index        (assoc :db/index       true)
              doc          (assoc :db/doc         doc)
              fulltext     (assoc :db/fulltext    true)
              is-component (assoc :db/isComponent true)
              no-history   (assoc :db/noHistory   true)))))

(defn assert
  "Generates transaction data asserting an entity."
  [model entity field-map-or-maps]
  (let [datomic-ks (field->datomic-ident-map model entity)
        field-map-or-maps (if (sequential? field-map-or-maps)
                            field-map-or-maps
                            (vector field-map-or-maps))]
    (mapv #(clojure.set/rename-keys % datomic-ks) field-map-or-maps)))
