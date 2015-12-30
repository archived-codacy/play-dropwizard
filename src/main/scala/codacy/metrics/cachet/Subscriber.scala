package codacy.metrics.cachet

sealed trait Subscriber

case class SubscriberEmail(value:String) extends AnyVal
case class SubscriberVerify(value:Int)   extends AnyVal

case class SubscriberReq(email:SubscriberEmail, verify: Option[SubscriberVerify]) extends Subscriber
