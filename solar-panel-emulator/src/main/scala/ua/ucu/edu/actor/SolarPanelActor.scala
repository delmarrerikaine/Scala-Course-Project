package ua.ucu.edu.actor

import akka.actor.{Actor, ActorRef, Props}
import ua.ucu.edu.model._

class SolarPanelActor(
  val panelId: String
) extends Actor {

  val sensorCount = 4
  var sensorIds = List[String]()
  for (i <- 0 until sensorCount) {
    val sensorId = "sensor_" + i
    sensorIds = sensorId :: sensorIds
  }

  var sensorActors = List[ActorRef]()
  for (id <- sensorIds) {
    sensorActors = context.actorOf(Props(new SensorActor(id))) :: sensorActors
  }

  override def receive: Receive = {
    case ReadMeasurement => sensorActors.foreach(_ ! ReadMeasurement)
    case sensorRecord: SensorRecord => context.parent ! PanelRecord(panelId, sensorRecord)
  }
}
