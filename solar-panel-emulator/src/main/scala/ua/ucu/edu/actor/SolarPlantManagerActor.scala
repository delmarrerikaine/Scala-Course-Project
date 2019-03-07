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

  // TODO: properly sync locations with weather service and check if locations is bigger than plantIds.size
  val locations = List (Location(49.8397, 24.0297),
    Location(46.916073, 4.466319),
    Location(40.154661, -2.936860),
    Location(33.170562, -2.880370),
    Location(40.665862, 34.503130)
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
