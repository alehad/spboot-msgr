# spboot-msgr
Spring Boot Messenger App

docker.hub: alehad/msgr

What is this:
Simple REST WebService that allows for creating/updating/deleting of simple messages:

{
   "message": "a message",
   "author": "the author"
}

Folders:
c4 > contains c4 diagram of the app

devops > docker > contains docker images for running app or required docker images [mongo/kakfa/etc] locally

  > msgr-docker-compose.yaml - docker compose file for running all required components + the app itself
  > 
  > msgr-pipeline-services-compose.yaml - docker compose file for running all services locally [useful for debugging]
  > 
  > mongo-compose.yaml - docker compose file for running mongo/mongo-express locally

  > docker compose -f msgr-docker-compose.yaml up [/down] 

jenkins > contains the shell script for running jenkins locally [docker in docker -dnd] for building/disting to hub.docker.com 

k8> same as devops folder but containg the k8 deployment files. use minikube to run locally

App:

The web service is designed to allow easy addition of different technologies for learning purposes.

Adding support for instance for SQLServer as a message store would require:
  * Implementing SQLServerStore class that extends InitializableComponentRegistry<IMessageStore>
  * setting the property msgr.db.store=SQLServer in application.properties file

I plan to add caching layer using similar concept and wire in Redis or ES on demand.

Similar to this, different message brokering technologies could also be introduced.

Kafka implementation in the app is a simple 'sync' implementation, whereby the KafkaMessageBroker will handle both posting
and processing of the kafka topics.

This app is not intended for any other use except for learning purposes.
