package connectors

import akka.actor.ActorSystem
import akka.stream.Materializer
import connectors.DefaultApiService
import entities.{Alert, QueryTerm}
import org.mockito.Mockito
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import common.CommonTestData
import entities.formats._

class DefaultApiServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with GuiceOneAppPerSuite with Injecting with CommonTestData {

  // Mocking WSClient, ActorSystem, and Materializer
  implicit val system: ActorSystem = ActorSystem("test-system")
  implicit val materializer: Materializer = Materializer(system)
  implicit val ec: ExecutionContext = system.dispatcher
  val mockWSClient: WSClient = Mockito.mock(classOf[WSClient])

  // Creating DefaultApiService instance using Guice injection
  val apiService = new DefaultApiService(mockWSClient)

  "DefaultApiService" should {
    "fetch query terms correctly" in {
      // Stubbing WSClient response
      val mockResponse: WSResponse = Mockito.mock(classOf[WSResponse])
      Mockito.when(mockResponse.status).thenReturn(200)
      Mockito.when(mockResponse.json).thenReturn(Json.toJson(testQueryTerms))

      val mockRequest: WSRequest = Mockito.mock(classOf[WSRequest])
      Mockito.when(mockRequest.get()).thenReturn(Future.successful(mockResponse))

      Mockito.when(mockWSClient.url("https://services.prewave.ai/adminInterface/api/testQueryTerm?key=colm:e91cb42b751d53da0c8222e18393382dc70d2d7461bc471b10c1eddfe681ab78"))
        .thenReturn(mockRequest)

      // Testing getQueryTerms method
      val result: Future[Seq[QueryTerm]] = apiService.getQueryTerms()
      whenReady(result, timeout(5.seconds)) { queryTerms =>
        queryTerms shouldEqual testQueryTerms
      }
    }

    "fetch alerts correctly" in {
      // Stubbing WSClient response
      val mockResponse: WSResponse = Mockito.mock(classOf[WSResponse])
      Mockito.when(mockResponse.status).thenReturn(200)
      Mockito.when(mockResponse.json).thenReturn(Json.toJson(testAlerts))

      val mockRequest: WSRequest = Mockito.mock(classOf[WSRequest])
      Mockito.when(mockRequest.get()).thenReturn(Future.successful(mockResponse))

      Mockito.when(mockWSClient.url("https://services.prewave.ai/adminInterface/api/testAlerts?key=colm:e91cb42b751d53da0c8222e18393382dc70d2d7461bc471b10c1eddfe681ab78"))
        .thenReturn(mockRequest)

      // Testing getAlerts method
      val result: Future[Seq[Alert]] = apiService.getAlerts()
      whenReady(result, timeout(5.seconds)) { alerts =>
        alerts shouldEqual testAlerts
      }
    }
  }
}
