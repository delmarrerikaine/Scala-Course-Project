package ua.ucu.edu.provider

import ua.ucu.edu.model._
import scalaj.http._
import org.json4s._
import org.json4s.native.JsonMethods._

trait WeatherProviderApi {

  private val key: String = "e42b07a8621c9e61339060eed6484977"

  implicit val formats = DefaultFormats

  def weatherAtLocation(location: Location): WeatherData = {
    val response: HttpResponse[String] = Http("https://api.openweathermap.org/data/2.5/weather")
      .param("lat", location.latitude.toString)
      .param("lon", location.longitude.toString)
      .param("units", "metric")
      .param("APPID", key)
      .asString

    val json: JValue = parse(response.body)

    val temp: Double = (json \\ "main" \\ "temp").extract[Double]
    val humidity: Double = (json \\ "main" \\ "humidity").extract[Double]

    WeatherData(location, temp, humidity)
  }
}
