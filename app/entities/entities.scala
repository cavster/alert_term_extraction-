package entities

import play.api.libs.json.{Json, Reads, Writes, Format}

// Value classes for IDs
case class QueryTermId(value: Int) extends AnyVal
case class AlertId(value: String) extends AnyVal

// Case classes for entities
case class QueryTerm(id: QueryTermId, target: Int, text: String, language: String, keepOrder: Boolean)
case class Content(text: String, `type`: String, language: String)
case class Alert(id: AlertId, contents: Seq[Content], date: String, inputType: String)
case class Match(alertId: AlertId, queryTermId: QueryTermId, queryTermText: String, contents: Seq[Content])

object formats {
  // Custom JSON format for AlertId
  implicit val alertIdFormat: Format[AlertId] = Format(
    Reads.StringReads.map(AlertId.apply),
    Writes.StringWrites.contramap(_.value)
  )

  // Custom JSON format for QueryTermId
  implicit val queryTermIdFormat: Format[QueryTermId] = Format(
    Reads.IntReads.map(QueryTermId.apply),
    Writes.IntWrites.contramap(_.value)
  )


  // JSON formats for entities
  implicit val contentFormat: Format[Content] = Json.format[Content]
  implicit val alertFormat: Format[Alert] = Json.format[Alert]
  implicit val queryTermFormat: Format[QueryTerm] = Json.format[QueryTerm]

  implicit val matchFormat: Format[Match] = Json.format[Match]
  // JSON formats for sequences of entities
  implicit def seqFormat[T: Format]: Format[Seq[T]] = Format(Reads.seq[T], Writes.seq[T])
}
