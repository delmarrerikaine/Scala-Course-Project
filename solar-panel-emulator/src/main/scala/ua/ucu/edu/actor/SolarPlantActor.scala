package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ua.ucu.edu.model._
import ua.ucu.edu.Config

// TODO: refactor out code with creation of collection subactors into some common base class for SolarPanelActor, SolarPlantActor and SolarPlantManagerActor
class SolarPlantActor(
  plantId: String,
  location: Location
) extends Actor with ActorLogging {

  var panelIds = List[String]()
  for (i <- 0 until Config.PanelCount) {
    val panelId = "panel_" + i
    panelIds = panelId :: panelIds
  }

  var panelActors = List[ActorRef]()
  for (id <- panelIds) {
    panelActors = context.actorOf(Props(new SolarPanelActor(id))) :: panelActors
  }

  override def receive: Receive = {
    case ReadMeasurement => panelActors.foreach(_ ! ReadMeasurement)
    case panelRecord: PanelRecord => context.parent ! PlantRecord(plantId, location, panelRecord)
  }
}
