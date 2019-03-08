package ua.ucu.edu.kafka

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.PlantRecord
import ua.ucu.edu.Config
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

object DataWriter {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val brokerList: String = System.getenv(Config.KafkaBrokers)
  val topic = "sensor-data"

  val props = new Properties()
  props.put("bootstrap.servers", brokerList)
  props.put("client.id", "solar-panel-1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  logger.info("initialized SolarPanelEmulator DataWriter")

  def writeData(plantRecord: PlantRecord): Unit = {
    val json =
    ("location" ->
      ("latitude" -> plantRecord.location.latitude) ~
      ("longitude" -> plantRecord.location.longitude)) ~
      ("panelId" -> plantRecord.panelRecord.panelId) ~
      ("sensorId" -> plantRecord.panelRecord.sensorRecord.sensorId) ~
      ("measurement" -> plantRecord.panelRecord.sensorRecord.measurement) ~
      ("plantId" -> plantRecord.plantId)

    val msg = compact(render(json))

    logger.info(s"[$topic] $msg")
    val key = s"${plantRecord.location.latitude}-${plantRecord.location.longitude}"
    val data = new ProducerRecord[String, String](topic, key, msg)
    producer.send(data, (metadata: RecordMetadata, exception: Exception) => {
      logger.info(metadata.toString, exception)
    })
  }
}
