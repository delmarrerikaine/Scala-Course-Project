package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ua.ucu.edu.model.{PlantRecord, ReadMeasurement, Location}
import ua.ucu.edu.kafka.DataWriter

import scala.concurrent.duration._
import scala.language.postfixOps

class SolarPlantManagerActor
  extends Actor with ActorLogging {

  val plantCount = 2
  var plantIds = List[String]()
  for (i <- 0 until plantCount) {
    val plantId = "plant_" + i
    plantIds = plantId :: plantIds
  }

  var plantActors = List[ActorRef]()
  for (id <- plantIds) {
    val location = Location(20, 20)
    plantActors = context.actorOf(Props(new SolarPlantActor(id, location))) :: plantActors
  }

  override def preStart(): Unit = {
    log.info(s"========== Solar Plant Manager starting ===========")
    super.preStart()

    // todo - schedule measurement reads
    context.system.scheduler.schedule(5 second, 5 seconds, self, ReadMeasurement)(
      context.dispatcher, self)
  }

  override def receive: Receive = {
    case ReadMeasurement => plantActors.foreach(_ ! ReadMeasurement)
    case plantRecord: PlantRecord => DataWriter.writeData(plantRecord)
  }
}
