package ua.ucu.edu.kafka

import java.util.Properties
import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}

class SolarPanelDummy(val panelId: String, val location: String) {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val BrokerList: String = System.getenv(DummyConfig.KafkaBrokers)
  val Topic = "sensor-data"
  val sensors = List("sensor A", "sensor B", "sensor C")

  val props = new Properties()
  props.put("bootstrap.servers", BrokerList)
  props.put("client.id", "solar-panel-1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  logger.info("initializing SolarPanelDummy")

  val producer = new KafkaProducer[String, String](props)

  def generateData(): Unit = {
    for (sensor <- sensors) {
      val sensorData = (Random.nextFloat * 30).toString
      val msg = s"{ panelId: $panelId, location: $location, sensorType: $sensor, measurement: $sensorData }"
      logger.info(s"[$Topic] $msg")

      val data = new ProducerRecord[String, String](Topic, msg)
      producer.send(data)
    }
  }
}

class SolarPlantDummy(val location:String) {
  val panels = List(new SolarPanelDummy("panel 1", location),
    new SolarPanelDummy("panel 2", location),
    new SolarPanelDummy("panel 3", location))

  def generateData(): Unit = {
    panels.foreach(_.generateData())
  }
}

// delete_me - for testing purposes
object DummyDataProducer {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val plants = List(new SolarPlantDummy("location 1"),
    new SolarPlantDummy("location 2"),
    new SolarPlantDummy("location 3"))

  // This is just for testing purposes
  def pushTestData(): Unit = {
    logger.info("initializing DummyDataProducer")

    while (true) {
      Thread.sleep(10000)

      plants.foreach(_.generateData())
    }
  }
}

object DummyConfig {
  val KafkaBrokers = "KAFKA_BROKERS"
}