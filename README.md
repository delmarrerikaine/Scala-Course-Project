# Scala Streaming Application for Course Project

## Getting Started
Initial flow:
```
cd streaming-ucu-final-project
sbt docker
docker-compose up
```

**PLEASE NOTE**: on windows machine before pull project use this fix for line breaks dos format issue(https://github.com/docker/labs/issues/215#issuecomment-304596295):
```
git config --global core.autocrlf false
```

While topics are not created after docker-compose you will see a lot of warnings.

To test if environment is setted up please perform following:
1) In the new console window run:
```
docker run --net=host --rm confluentinc/cp-kafka:5.1.0 kafka-topics --create --topic foo --partitions 4 --replication-factor 1 --if-not-exists --zookeeper localhost:2181
```
2) run Scripts/Consumer.py
3) run Scripts/Producer.py
4) run `sudo ./create_topics`
5) Check on the UI http://localhost:8000/ that populated data exist in the kafka topic foo

**Note:** instead of points 2 and 3 above we can use [kafkacat](https://github.com/edenhill/kafkacat) tool, which is a generic console producer/consumer

## Troubleshooting
### What to do if my changes aren't include in docker image?
Don't forget to run `sbt docker` to update the images.
### How to resolve the error "Bind for 0.0.0.0:2181 failed: port is already allocated"?
Please run `sudo docker-compose down` to shutdown all handling images
### How to resolve the error "...oci runtime create failed: container_linux.go:348..."?
Hey pal, you'd better not spend a few hours trying to resolve it. Just reinstall `docker-compose` to fix it:
1. `sudo apt-get remove docker-compose`
2. `sudo apt-get install docker-compose`
