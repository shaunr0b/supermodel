Supermodel provides a utility belt of functions that take an information model and transform it into a variety of formats.

<img src="https://raw.githubusercontent.com/env/supermodel/master/doc/logo.png"
alt="Supermodel" title="Supermodel" align="right" width="30%"/>


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

For example, an online store stand
```clojure
{:vendor  {:name     {:type :string}
           :ident    {:type :string
                      :unique :value
                      :required true}}
 :user    {:name     {:type :string
                      :unique :value}
           :password {:type :string
                      :encrypted true}
           :roles  {:type :ref
                    :cardinality :many}}
 :roles    {:ident {:type :keyword
                    :unique :ident}}
 :product  {:name  {:type :string}
            :vendor {:type :refing}
            :ident {:type :string
                    :unique :ident}}}
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

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
