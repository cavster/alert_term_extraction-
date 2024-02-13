package controllers


import connectors.DefaultApiService
import entities.{Alert, QueryTerm, _}

import javax.inject._
import models._
import play.api.libs.json._
import play.api.mvc._
import entities.formats._

import scala.concurrent.Future
@Singleton
class AlertController @Inject()(cc: ControllerComponents,apiS:DefaultApiService) extends AbstractController(cc) {


  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private def findMatches(alerts:Seq[Alert],queryTerms:Seq[QueryTerm]):Seq[Match] = { // Define a function to check if an alert content contains a query term text

  def isMatch(alert: Alert, queryTerm: QueryTerm): Boolean = {
    if (queryTerm.keepOrder) {
      alert.contents.exists(con => con.text.contains(queryTerm.text) && con.language == queryTerm.language)
    } else {
      //check for each bit of text
      val textAsSeq = queryTerm.text.split(" ").toSeq
      alert.contents.forall { con =>
        textAsSeq.forall { queryText =>
          con.text.contains(queryText) && con.language == queryTerm.language
        }
      }
    }
  }

    val allMatches = for {
    alert <- alerts
    queryTerm <- queryTerms
    if isMatch(alert, queryTerm)
  } yield Match(alert.id, queryTerm.id,queryTerm.text,alert.contents)

  allMatches.distinct
}
  def findMatches(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    //I'm keeping this out side for yield so they are done in parallel
   val futureAlerts =   apiS.getAlerts()
    val futureQueryTerms =  apiS.getQueryTerms()
    val matches: Future[Seq[Match]] = for {
      alerts <- futureAlerts
      queryTerms <- futureQueryTerms
    } yield findMatches(alerts,queryTerms)
    matches.map(mat =>  Ok(Json.toJson(mat)) )
  }
}
