package ua.ucu.edu.actor

import akka.actor.Actor
import ua.ucu.edu.device.{SensorApi, SensorGenerator}
import ua.ucu.edu.model.{ReadMeasurement, SensorRecord}

class SensorActor(
  sensorId: String
) extends Actor {

  val api: SensorApi = new SensorGenerator

  override def receive: Receive = {
    case ReadMeasurement => sender ! SensorRecord(sensorId, api.readCurrentValue)
  }
}
