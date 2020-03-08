package dl4jkafkaevents;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

class JavaBootstrap {

    public static void main(String[] args) {

        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("kafka.stream.events.bootstrap"));

        IFn bootstrap = Clojure.var("kafka.stream.bootstrap", "bootstrap");
        bootstrap.invoke(args);
    }
}
