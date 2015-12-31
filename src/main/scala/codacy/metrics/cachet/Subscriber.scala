package codacy.metrics.cachet

import codacy.macros.enriched

sealed trait Subscriber

@enriched case class SubscriberEmail(value:String) extends AnyVal
@enriched case class SubscriberVerify(value:Int)   extends AnyVal

case class SubscriberReq(email:SubscriberEmail, verify: Option[SubscriberVerify]) extends Subscriber
