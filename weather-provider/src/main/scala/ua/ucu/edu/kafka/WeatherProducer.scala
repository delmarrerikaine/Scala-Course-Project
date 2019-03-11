package ua.ucu.edu.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.WeatherData


object WeatherProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val BrokerList: String = System.getenv(Config.KafkaBrokers)
  val Topic = "omr-weather-data"

  val props = new Properties()
  props.put("bootstrap.servers", BrokerList)
  props.put("client.id", "weather-provider")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


  logger.info("initializing producer...")
  val producer = new KafkaProducer[String, String](props)

  implicit val formats = Serialization.formats(NoTypeHints)

  def pushData(weatherData: WeatherData): Unit = {
    val serialized = write(weatherData)
    logger.info(s"[$Topic] $serialized")

    val key = s"${weatherData.location.latitude}-${weatherData.location.longitude}"
    val data = new ProducerRecord[String, String](Topic, key, serialized)
    producer.send(data, (metadata: RecordMetadata, exception: Exception) => {
      logger.info(metadata.toString, exception)
    })
  }
}
