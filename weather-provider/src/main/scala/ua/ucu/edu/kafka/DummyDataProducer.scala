package ua.ucu.edu.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.Main.provider
import ua.ucu.edu.model.{Location, WeatherData}

// delete_me - for testing purposes
object DummyDataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  implicit val formats = Serialization.formats(NoTypeHints)

  def pushTestData(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)
    val Topic = "omr-weather-data"
    val props = new Properties()
    props.put("bootstrap.servers", BrokerList)
    props.put("client.id", "weather-provider")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, String](props)

    val testMsg = "hot weather"

    while (true) {
      Thread.sleep(10000)
      logger.info(s"[$Topic] $testMsg")

      val weatherData: WeatherData = provider.weatherAtLocation(Location(49.8397, 24.0297))
      val serialized = write(weatherData)

      val data = new ProducerRecord[String, String](Topic, serialized)
      producer.send(data, (metadata: RecordMetadata, exception: Exception) => {
        logger.info(metadata.toString, exception)
      })
    }

    producer.close()
  }
}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
}