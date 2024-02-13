package controllers

import org.scalatestplus.play._
import org.scalatest.concurrent.ScalaFutures
import org.mockito.Mockito._

import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.json._
import play.api.test._
import connectors.DefaultApiService
import entities._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.{contentAsString, stubControllerComponents}
import entities.formats._

import scala.concurrent.duration._
import akka.util.Timeout
import common.CommonTestData

import java.time.LocalDateTime
import org.mockito.Mockito

class AlertControllerSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures with Results  with CommonTestData{

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(5.seconds), interval = scaled(100.millis))

  // Mocking DefaultApiService
  val mockApiService: DefaultApiService = Mockito.mock(classOf[DefaultApiService])

  // Creating AlertController instance with the mocked service
  val alertController = new AlertController(stubControllerComponents(), mockApiService)



  "AlertController" should {

    "return matches when findMatches method is called" in {
      // Stubbing the DefaultApiService response
      when(mockApiService.getAlerts()).thenReturn(Future.successful(testAlerts))
      when(mockApiService.getQueryTerms()).thenReturn(Future.successful(testQueryTerms))

      // Calling the method under test
      val result: Future[Result] = alertController.findMatches().apply(FakeRequest())

      implicit val timeout: Timeout = Timeout(5.seconds)

      val bodyText: String = contentAsString(result)
      val matches: Seq[Match] = Json.parse(bodyText).as[Seq[Match]]

      matches.length mustBe 3

      matches.exists(_.alertId.value == "101") mustBe true
      matches.exists(_.alertId.value == "102") mustBe true
      matches.exists(_.alertId.value == "103") mustBe true
      matches.exists(_.alertId.value == "104") mustBe false

    }

    "return matches when findMatches method is called and check the language" in {
      // Stubbing the DefaultApiService response
      when(mockApiService.getAlerts()).thenReturn(Future.successful(testAlerts))
      val germanTestQueryTerms = testQueryTerms.map(_.copy(language = "de"))
      when(mockApiService.getQueryTerms()).thenReturn(Future.successful(germanTestQueryTerms))

      // Calling the method under test
      val result: Future[Result] = alertController.findMatches().apply(FakeRequest())

      implicit val timeout: Timeout = Timeout(5.seconds)

      val bodyText: String = contentAsString(result)
      val matches: Seq[Match] = Json.parse(bodyText).as[Seq[Match]]

      matches.length mustBe 0

      matches.exists(_.alertId.value == "101") mustBe false
      matches.exists(_.alertId.value == "102") mustBe false
      matches.exists(_.alertId.value == "103") mustBe false
      matches.exists(_.alertId.value == "104") mustBe false

    }
    "return matches when findMatches method is called for mutliply terms when keep order is false" in {
      // Stubbing the DefaultApiService response
      when(mockApiService.getAlerts()).thenReturn(Future.successful(testAlertsMultiplyTerms))
      when(mockApiService.getQueryTerms()).thenReturn(Future.successful(testMultiplyTermsQueryTerms))

      // Calling the method under test
      val result: Future[Result] = alertController.findMatches().apply(FakeRequest())

      implicit val timeout: Timeout = Timeout(5.seconds)

      val bodyText: String = contentAsString(result)
      val matches: Seq[Match] = Json.parse(bodyText).as[Seq[Match]]

      matches.length mustBe 1

      matches.exists(_.alertId.value == "101") mustBe true

    }
    "return matches when findMatches method is called for mutliply terms when keep order is true" in {
      // Stubbing the DefaultApiService response
      when(mockApiService.getAlerts()).thenReturn(Future.successful(testAlertsMultiplyTerms))
      when(mockApiService.getQueryTerms()).thenReturn(Future.successful(testMultiplyTermsQueryTerms.map(_.copy(keepOrder = true))))

      // Calling the method under test
      val result: Future[Result] = alertController.findMatches().apply(FakeRequest())

      implicit val timeout: Timeout = Timeout(5.seconds)

      val bodyText: String = contentAsString(result)
      val matches: Seq[Match] = Json.parse(bodyText).as[Seq[Match]]

      matches.length mustBe 0

      matches.exists(_.alertId.value == "101") mustBe false

    }
  }
}
