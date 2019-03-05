package ua.ucu.edu

import java.util.Properties

import ua.ucu.edu.kafka.{Config, DummyDataProducer}
import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.json4s.NoTypeHints
import org.slf4j.LoggerFactory
import ua.ucu.edu.provider._
import ua.ucu.edu.model._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import ua.ucu.edu.kafka.DummyDataProducer.logger

import scala.concurrent.duration
import scala.language.postfixOps

object Main extends App {

  val logger = LoggerFactory.getLogger(getClass)

  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher

  import duration._
  implicit val formats = Serialization.formats(NoTypeHints)

  val provider = new WeatherProviderApi {}

  val BrokerList: String = System.getenv(Config.KafkaBrokers)
  val Topic = "weather_data"
  val props = new Properties()
  props.put("bootstrap.servers", BrokerList)
  props.put("client.id", "weather-provider")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  logger.info("initializing producer")

  val producer = new KafkaProducer[String, String](props)

  def pushData(weatherData: String): Unit = {

    logger.info(s"[$Topic] $weatherData")
    val data = new ProducerRecord[String, String](Topic, weatherData)
    producer.send(data, (metadata: RecordMetadata, exception: Exception) => {
      logger.info(metadata.toString, exception)
    })
  }

  system.scheduler.schedule(5 seconds, 10 seconds, new Runnable {
    override def run(): Unit = {
      logger.debug("weather request")
      // todo - ask weather api and send data to kafka topic - recommended format is json - or you can come up with simpler string-based protocol

      val weatherData: WeatherData = provider.weatherAtLocation(Location(49.8397, 24.0297))
      val serialized = write(weatherData)

      pushData(serialized)
    }
  })

  // for testing purposes only
  DummyDataProducer.pushTestData()
}
