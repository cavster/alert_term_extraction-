package common

import entities._

import java.time.LocalDateTime

trait CommonTestData {
  // Sample data for testing
  val testQueryTerms: Seq[QueryTerm] = Seq(
    QueryTerm(QueryTermId(1), 1, "term1", "en", keepOrder = false),
    QueryTerm(QueryTermId(2), 1, "term2", "en", keepOrder = false),
    QueryTerm(QueryTermId(3), 1, "term3", "en", keepOrder = true)
  )

  val testAlerts: Seq[Alert] = Seq(
    Alert(AlertId("101"), Seq(Content("Alert with term1", "type1", "en")), LocalDateTime.now().toString, "inputType"),
    Alert(AlertId("102"), Seq(Content("Alert with term2", "type1", "en")), LocalDateTime.now().toString, "inputType"),
    Alert(AlertId("103"), Seq(Content("Alert with term3", "type1", "en")), LocalDateTime.now().toString, "inputType"),
    Alert(AlertId("104"), Seq(Content("No term here", "type1", "en")), LocalDateTime.now().toString, "inputType")
  )
}
