package ua.ucu.edu.provider

import org.json4s.{DefaultFormats, JValue}
import org.json4s.native.JsonMethods.parse
import scalaj.http.{Http, HttpResponse}
import ua.ucu.edu.model.{Location, WeatherData}

class WeatherProvider extends WeatherProviderApi {

  private val key: String = "e42b07a8621c9e61339060eed6484977"

  implicit val formats = DefaultFormats

  override def weatherAtLocation(location: Location): WeatherData = {
    val response: HttpResponse[String] = Http("https://api.openweathermap.org/data/2.5/weather")
      .param("lat", location.latitude.toString)
      .param("lon", location.longitude.toString)
      .param("units", "metric")
      .param("APPID", key)
      .asString

    val json: JValue = parse(response.body)

    val temp: Double = (json \\ "main" \\ "temp").extract[Double]
    val humidity: Double = (json \\ "main" \\ "humidity").extract[Double]
    val timestamp: Long = (json \\ "dt").extract[Long]

    WeatherData(location, timestamp, temp, humidity)
  }
}
