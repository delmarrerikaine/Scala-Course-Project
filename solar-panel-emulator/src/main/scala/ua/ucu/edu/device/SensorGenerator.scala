package ua.ucu.edu.device
import scala.util.Random

class SensorGenerator extends SensorApi {
  override def readCurrentValue: String = (Random.nextFloat * 30).toString
}
