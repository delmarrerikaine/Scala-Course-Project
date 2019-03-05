package ua.ucu.edu.device
import scala.util.Random
import ua.ucu.edu.Config

class SensorGenerator extends SensorApi {
  val range = Config.MaxSensorValue - Config.MinSensorValue
  override def readCurrentValue: String = (Random.nextFloat * range + Config.MinSensorValue).toString
}
