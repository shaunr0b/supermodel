(ns supermodel.core)

(def model-template
  "Template for a datom-model.
  :entity-type - the datomic entity-type being modeled
  :field       - the field of the entity-type being modeled
  :property    - datom-model field property
  :value       - datom-model field property value"
  
  {:entity-type {:field {:property :value}}})


