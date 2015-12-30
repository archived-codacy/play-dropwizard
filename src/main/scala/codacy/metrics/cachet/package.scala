package codacy.metrics

import _root_.play.api.libs.json._

package object cachet extends Formats with WsApi with Crud with Cruds{
  type Date = String

  implicit class WritesExtension[A](writes:Writes[A]){
    def asOWrites: OWrites[A] = OWrites((a:A) => writes.writes(a) match{
      case obj:JsObject => obj
      case _ => throw new Exception("tried to convert a non object write to an object write")
    })
  }
}