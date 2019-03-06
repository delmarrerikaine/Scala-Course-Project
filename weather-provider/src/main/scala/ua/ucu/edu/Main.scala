package ua.ucu.edu

import java.util.Properties

import ua.ucu.edu.kafka.{Config, WeatherProducer}
import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.json4s.NoTypeHints
import org.slf4j.LoggerFactory
import ua.ucu.edu.provider._
import ua.ucu.edu.model._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import ua.ucu.edu.kafka.DummyDataProducer.logger

import ua.ucu.edu.model.{Location}

import scala.concurrent.duration
import scala.language.postfixOps

object Main extends App {

  val logger = LoggerFactory.getLogger(getClass)

  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher

  import duration._
  implicit val formats = Serialization.formats(NoTypeHints)

  val provider = new WeatherProvider

  val locations = List (Location(49.8397, 24.0297),
    Location(46.916073, 4.466319),
    Location(40.154661, -2.936860),
    Location(33.170562, -2.880370),
    Location(40.665862, 34.503130)
  )

  system.scheduler.schedule(5 seconds, 10 seconds, new Runnable {
    override def run(): Unit = {
      logger.debug("weather request")
      // todo - ask weather api and send data to kafka topic - recommended format is json - or you can come up with simpler string-based protocol
      locations.foreach { location =>
        val weatherData = provider.weatherAtLocation(location)
        WeatherProducer.pushData(weatherData)
      }
    }
  })
}
