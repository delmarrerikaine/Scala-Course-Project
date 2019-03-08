package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ua.ucu.edu.model.{PlantRecord, ReadMeasurement, Location}
import ua.ucu.edu.kafka.DataWriter
import ua.ucu.edu.Config

import scala.concurrent.duration._
import scala.language.postfixOps

class SolarPlantManagerActor
  extends Actor with ActorLogging {

  var plantIds = List[String]()
  for (i <- 0 until Config.PlantCount) {
    val plantId = "plant_" + i
    plantIds = plantId :: plantIds
  }

  val locations = List (Location(49.8397, 24.0297),
    Location(46.916073, 4.466319),
    Location(40.154661, -2.936860),
    Location(33.170562, -2.880370),
    Location(40.665862, 34.503130),
    Location(41.665862, 33.503130),
    Location(42.665862, 32.503130),
    Location(43.665862, 31.503130),
    Location(44.665862, 30.503130),
    Location(45.665862, 29.503130),
    Location(46.665862, 28.503130),
    Location(47.665862, 27.503130),
    Location(48.665862, 26.503130),
    Location(49.665862, 25.503130),
    Location(50.665862, 24.503130),
    Location(51.665862, 23.503130),
    Location(52.665862, 22.503130),
    Location(53.665862, 21.503130),
    Location(54.665862, 20.503130),
    Location(55.665862, 19.503130),
  )

  var plantActors = List[ActorRef]()
  for (i <- 0 until plantIds.size) {
    plantActors = context.actorOf(Props(new SolarPlantActor(plantIds(i), locations(i)))) :: plantActors
  }

  override def preStart(): Unit = {
    log.info(s"========== Solar Plant Manager starting ===========")
    super.preStart()

    // todo - schedule measurement reads
    context.system.scheduler.schedule(5 second, Config.ReportIntervalSeconds seconds, self, ReadMeasurement)(
      context.dispatcher, self)
  }

  override def receive: Receive = {
    case ReadMeasurement => plantActors.foreach(_ ! ReadMeasurement)
    case plantRecord: PlantRecord => DataWriter.writeData(plantRecord)
  }
}
