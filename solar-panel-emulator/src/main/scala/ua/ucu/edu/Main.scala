package ua.ucu.edu

import akka.actor._

import ua.ucu.edu.kafka.DummyDataProducer
import ua.ucu.edu.actor.SolarPlantManagerActor

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  val managerActor = system.actorOf(Props(new SolarPlantManagerActor), "plant-manager")

  while (true) {

  }
//  DummyDataProducer.pushTestData()
}