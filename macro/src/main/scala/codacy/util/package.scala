package codacy

import play.api.libs.json.{JsObject, OWrites, Writes}

package object util {
  implicit class WritesExtension[A](writes:Writes[A]){
    def asOWrites: OWrites[A] = OWrites((a:A) => writes.writes(a).as[JsObject])
  }
}
