package ua.ucu.edu.model

case object ReadMeasurement

// TODO: add datetime to SensorRecord
case class SensorRecord(sensorId: String, measurement: String)
case class PanelRecord(panelId: String, sensorRecord: SensorRecord)
case class PlantRecord(plantId: String, location: Location, panelRecord: PanelRecord)