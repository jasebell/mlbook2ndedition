(ns kafka.stream.prediction.db
  (:require
   [clojure.java.jdbc :as jdbc]
   [conman.core :as conman]
   [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
  :start (conman/connect! {:jdbc-url "jdbc:mysql://localhost:3307/mlchapter12?user=xxxx&password=xxxx"})
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

(mount.core/start #'*db*)
