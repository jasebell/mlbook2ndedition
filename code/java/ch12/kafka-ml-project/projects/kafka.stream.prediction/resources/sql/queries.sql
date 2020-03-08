-- :name load-accurate-model-by-type :? :*
SELECT uuid, model_type, model_accuracy from training_log WHERE model_type=:model-type ORDER BY model_accuracy DESC

-- :name load-linear-model :? :*
SELECT * FROM linear_model ORDER BY rsq DESC LIMIT 1
