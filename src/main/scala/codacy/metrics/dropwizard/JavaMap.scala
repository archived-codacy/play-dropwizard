package codacy.metrics.dropwizard

import scala.collection.JavaConversions._

sealed trait JavaMap{ self: Map[HealthCheckName, Result] =>
  def javaRepr:java.util.SortedMap[String, Result]
}

private[dropwizard] object JavaMap{

  def apply(javaMap:java.util.SortedMap[String, Result]):Map[HealthCheckName, Result] =
    JavaMapImpl(javaMap)

  private[this] case class JavaMapImpl(val javaRepr:java.util.SortedMap[String, Result] ) extends Map[HealthCheckName, Result] with JavaMap{
    private[this] lazy val asScala = (Map.empty ++ javaRepr).map{ case (key,value) => (HealthCheckName(key),value)}

    override def get(key: HealthCheckName): Option[Result] = asScala.get(key)

    override def iterator: Iterator[(HealthCheckName, Result)] = asScala.iterator

    override def -(key: HealthCheckName): Map[HealthCheckName, Result] = {
      javaRepr.remove(key.value)
      JavaMapImpl(javaRepr)
    }

    override def +[B1 >: Result](kv: (HealthCheckName, B1)): Map[HealthCheckName, B1] = {
      kv match{
        case (key,result:Result) =>
          javaRepr.put(key.value,result)
          JavaMapImpl(javaRepr)
        case _ => asScala
      }
    }
  }
}
