package codacy.metrics.dropwizard

import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

class MeterSpecs extends Specification{

  def allMeters = MetricRegistry.underlying.getMeters

  "A meter" should{

    val meterName = MeterName("myMeter")

    "be created and have at least one value" in{
      mark(meterName)

      val count = allMeters.collectFirst{ case(name,meter) if name == meterName.value =>
        meter.getCount
      }

      count must beSome.which(_ > 1)
    }

    "have a prefixed name" in{
      mark(meterName)

      val internalName = allMeters.collectFirst{ case(name,_) if name == meterName.value => name }

      internalName must beSome.which(_.startsWith("meter"))
    }
  }
}
