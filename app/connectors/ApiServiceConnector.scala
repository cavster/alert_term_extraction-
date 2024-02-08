package connectors



import javax.inject._
import akka.actor.ActorSystem
import akka.stream.Materializer
import entities.{Alert, QueryTerm}
import play.api.libs.json._
import play.api.libs.ws._
import entities.formats._
import scala.concurrent.{ExecutionContext, Future}

trait ApiService {
  def getQueryTerms(): Future[Seq[QueryTerm]]
  def getAlerts(): Future[Seq[Alert]]
}

@Singleton
class DefaultApiService @Inject()(ws: WSClient)
                                 (implicit val system: ActorSystem,
                                  implicit val materializer: Materializer,
                                  implicit val ec: ExecutionContext) extends ApiService {

  val baseUri = "https://services.prewave.ai/adminInterface/api"
  //I would normally keep these in the config file and have them encrypted
  val apiKey = "colm:e91cb42b751d53da0c8222e18393382dc70d2d7461bc471b10c1eddfe681ab78"



  def getQueryTerms(): Future[Seq[QueryTerm]] = {
    val url = s"$baseUri/testQueryTerm?key=$apiKey"
    fetchAndDeserialize[Seq[QueryTerm]](url)
  }
  def getAlerts(): Future[Seq[Alert]] = {
    val url = s"$baseUri/testAlerts?key=$apiKey"
    fetchAndDeserialize[Seq[Alert]](url)
  }
  private def fetchAndDeserialize[T](url: String)(implicit reads: Reads[T]): Future[T] = {
    ws.url(url).get().flatMap { response =>
      response.status match {
        case 200 =>

          try {
            Future.successful(response.json.as[T])
          } catch {
            case e: JsResultException =>
              val errorMessage = s"Failed to parse JSON response from $url: ${e.getMessage}"
              Future.failed(new RuntimeException(errorMessage, e))
          }
        case _ =>
          // Handle non-200 responses
          val errorMessage = s"Failed to fetch data from $url: ${response.status} ${response.body}"
          Future.failed(new RuntimeException(errorMessage))
      }
    }
  }
}