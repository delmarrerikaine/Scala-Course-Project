package ua.ucu.edu

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.json4s
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s._


case class SensorData (
  plantId: String,
  latitude: String,
  longitude: String,
  panelId: String,
  sensorId: String,
  measurement: String
)

// dummy app for testing purposes
object DummyStreamingApp extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming_app_0")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(Config.KafkaBrokers))
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  import Serdes._

  val builder = new StreamsBuilder

  val weatherStream = builder.table[String, String]("weather_data")
  val sensorStream = builder.stream[String, String]("sensor-data")

  implicit val formats = DefaultFormats

  val valueJoiner = (sensor: String, weather: String) => {
    val sensorJson = parse(sensor)
    val weatherJson = parse(weather)

    val merged = sensorJson merge weatherJson
    val result = pretty(render(merged))

    result
  }

  val mergedStream = sensorStream.join(weatherStream)(valueJoiner)

  mergedStream.foreach { (k, v) =>
    logger.info(s"record processed $k->$v")
  }

  mergedStream.to("test_topic_out")

  val streams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  sys.addShutdownHook {
    streams.close(10, TimeUnit.SECONDS)
  }

  object Config {
    val KafkaBrokers = "KAFKA_BROKERS"
  }
}
