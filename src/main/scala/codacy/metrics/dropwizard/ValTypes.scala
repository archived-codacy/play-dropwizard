package codacy.metrics.dropwizard

case class HealthCheckName(value:String) extends AnyVal

case class MeterName(private val raw:String){
  lazy val value = s"meter.$raw"
  override def toString = value
}

case class TimerName(private val raw:String){
  lazy val value = s"timer.$raw"
  override def toString = value
}

case class HistogramName(private val raw:String){
  lazy val value = s"histogram.$raw"
  override def toString = value
}

