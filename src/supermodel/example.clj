(ns example
  (:require [datomic.api :as d]))

(def model
  {:vendor  {:name     {:type :string}
             
             :ident    {:type :string
                        :unique :value
                        :required true}}
   
   :user    {:name     {:type :string
                        :unique :value}
             :password {:type :string
                        :encrypted true}
             :roles    {:type :ref
                        :cardinality :many}}
   
   :roles    {:ident   {:type :keyword
                        :unique :ident}}
   
   :product  {:name    {:type :string}
              :vendor  {:type :refing}
              :ident   {:type :string
                        :unique :ident}}})
