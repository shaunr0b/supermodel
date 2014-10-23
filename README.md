Supermodel provides a utility belt of functions that take an information model and transform it into a variety of formats.

<img src="https://raw.githubusercontent.com/env/supermodel/master/doc/logo.png"
alt="Supermodel" title="Supermodel" align="right" width="30%"/>

Leiningen dependency (Clojars): `[env/supermodel "0.1.1"]`

It was born out of real world Clojure projects needing a concise common denomenator fully express a domain's information model.
 * **Database:** queries, transactions, schema migrations, seed data generation
 * **Domain Modeling:** validations, type coercions, defaults
 * **Logging:** pretty printing output
 * **Encryption:** encrypted property values

## Information Model
A model is expressed using the format:
```clojure
{:entity-type {:field {:property :value}}}
```

For example, a simple online store:
```clojure
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
                    
 :roles    {:ident  {:type :keyword 
                     :unique :identity}}
                    
 :product  {:name   {:type :string}
            :vendor {:type :ref}
            :ident  {:type :string
                     :unique :identity}}})
```

## Example
### Datomic
```clojure
(require '[supermodel.datomic :refer [model->schema-tx-data])

(def model ...)

(model->schema-tx-data model)
=>
({:db/id {:part :db.part/db, :idx -1000003},
  :db/ident :vendor/name,
  :db/valueType :db.type/string,
  :db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db}
 {:db/unique :db.unique/value,
  :db/id {:part :db.part/db, :idx -1000004},
  :db/ident :vendor/ident,
  :db/valueType :db.type/string,
  :db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db}
 {:db/unique :db.unique/identity,
  :db/id {:part :db.part/db, :idx -1000005},
  :db/ident :roles/ident,
  :db/valueType :db.type/keyword,
  :db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db}
 {:db/id {:part :db.part/db, :idx -1000006}
 ...
```
### Prismatic Schema
```clojure

(require '[supermodel.schema :refer [type-map make-schema-map]]
         '[schema.core :as s])

(def model ...)

(def schema-map (make-schema-map type-map model))
=>
{:vendor {:name java.lang.String, :ident java.lang.String},
 :roles
 {:ident {:p? #<core$keyword_QMARK_ clojure.core$keyword_QMARK_@29e2f29e>, :pred-name keyword?}},
 :product {:vendor java.lang.Number, :name java.lang.String, :ident java.lang.String},
 :user {:password java.lang.String, :name java.lang.String, :roles java.lang.Number}}

(s/validate (:vendor schema-map) {:name 123})
;; Schema error: name must be a String
(s/validate (:vendor schema-map) {:name "Acme Corp"})
;; => {:name "Acme Corp"}
```

## Plugins / Extensions
Extensions are just functions that take this map as an argument. 
So far, included are:
* Datomic
* Prismatic Schema

Extensions for future releases:
* Encryption
* Defaults
* Logging

Also todo:
* Unit tests

Pull requests are welcome.

### Contributors

[@tvanhens](http://github.com/tvanhens): thanks for the inspiration and code in datom-smasher.

## License

Copyright © 2014 Shaun Robinson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
