# Scala Streaming Application for Course Project

Initial flow:
'''
cd streaming-ucu-final-project
sbt docker
docker-compose up
'''

**PLEASE NOTE**: on windows machine before pull project use this fix for line breaks dos format issue(https://github.com/docker/labs/issues/215#issuecomment-304596295):
'''
git config --global core.autocrlf false
'''

While topics are not created after docker-compose you will see a lot of warnings.

To test if environment is setted up please perform following:
1) In the new console window run:
'''
docker run --net=host --rm confluentinc/cp-kafka:5.1.0 kafka-topics --create --topic foo --partitions 4 --replication-factor 1 --if-not-exists --zookeeper localhost:2181
'''
2) run Scripts/Consumer.py
3) run Scripts/Producer.py
4) Check on the UI http://localhost:8000/ that populated data exist in the kafka topic foo