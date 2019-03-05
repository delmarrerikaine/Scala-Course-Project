package ua.ucu.edu.kafka

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.PlantRecord
import ua.ucu.edu.Config

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
    val msg = s"{ plantId: ${plantRecord.plantId}," +
      s" latitude: ${plantRecord.location.latitude}," +
      s" longitude: ${plantRecord.location.longitude}," +
      s" panelId: ${plantRecord.panelRecord.panelId}," +
      s" sensorId: ${plantRecord.panelRecord.sensorRecord.sensorId}," +
      s" measurement: ${plantRecord.panelRecord.sensorRecord.measurement} }"

    logger.info(s"[$topic] $msg")
    val data = new ProducerRecord[String, String](topic, msg)
    producer.send(data)
  }
}
