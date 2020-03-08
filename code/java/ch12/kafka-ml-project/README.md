# Learning how to design automatically updating AI with Apache Kafka and Deeplearning4J.


This is the codebase for Chapter 12 of the Machine Learning Hands On book.

## Requirements
In order to run this system you require the following components:

* [Kafka](http://kafka.apache.org) (I'm using the supplied Zookeeper distribution).
* [MySQL](http://www.mysql.com)
* [Leiningen](https://leiningen.org/) (for building the Clojure projects)
* [Maven](http://maven.apache.org) (for building the Java projects) 
* Java 1.8 (parts of the system use Clojure 1.9 but there's no need to download this, Leiningen takes care of that for you)

## Breakdown of the project

The project uses a mixture of Java (for the model creation) and Clojure (for Kafka Streams and the HTTP API). The directory structure of this project is broken down as follows:

`config` - Kafka Connect configurations: one for the event_topic persistance and the other to save the training data ready for model training.

`crontab` - Scheduled jobs for model creation.

`db` - A schema for the MySQL database which holds information on the training and model accuracy and another table to hold the slope/intercept of a simple linear regression model.

`messages` - Simple JSON messages for the cronjob to send to the event stream.

`projects` - The main bulk of the coding is here in four projects: Model builds (`dlj4.mlp`), Kafka Streaming applications (`kafka.stream.events` and `kafka.stream.prediction`) and a very basic HTTP API (`prediction.http.api`)

`scripts` - Shell scripts to create the required Kafka topics, environment variables and event trigger for the cron job.

`slides` - Slides from the talk will be added once the talk has taken place on 24th May.

## General order of build

Before you start please change the username/password values to a user on your MySQL database.

1. Create a directory to store persisted events.
2. Create a directory to save training data.
3. Create a directory for the generated models.
4. Start Zookeeper and Kafka
5. Run the `create-topics.sh` to create the required topics.
6. Start Kafka Connect and add the two Connect configurations.
7. Run `lein uberjar` on the Kafka Streaming apps and the HTTP API.
8. Run `mvn package` to create the model builders.
9. Run the streaming applications:

```
PROFILE=local java -jar path/to/jar/kafka-stream-events.jar
```

and 

```
PROFILE=local java -jar path/to/jar/kafka-stream-prediction.jar
```

## Event Messages

There are two types of events: training events and build events. 

```
{"type":"command", "payload":"build_mlp"}
```

and

```
{"type":"training", "payload":"3,4,5,6"}
```
  
Training events are based on a four column piece of CSV data. If you want to accomodate others then you will need to modify the model builds and the streaming applications. Right now there's nothing dynamic but I'd like to work on that in the future. 

Send all events to the `event_topic` and they will be persisted and processed by the streaming app.

## Predictions

Once models are built you can make predictions through Kafka by sending a message to the `prediction_request_topic` and watching the results come back through the `prediction_response_topic`. 

JSON payloads look like this:

```
{"model":"mlp", "payload":"3,4,5"}
```

Note the fourth column is missing compared to the training data, this is the class you are predicting. The Kafka Streaming app takes care of the parsing and preparation to make a prediction.

## Crontab
There are two flavors of cronjobs, the first is a direct call to the executable to create the model. Alternatively, and more desirable, is to send an event to the `event_topic` and let the stream pick up the event and process it. This means the event stream is preserved via Kafka Connect and can be replayed.

## Notes
This is a work in progress to prove out my thoughts for the book.
There are plenty of improvements that could be done.

  
