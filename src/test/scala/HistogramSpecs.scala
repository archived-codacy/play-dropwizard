package codacy.metrics.dropwizard

import org.specs2.mutable.Specification

import scala.collection.JavaConversions._
import scala.util.Random

class HistogramSpecs extends Specification{

  def allHistograms = MetricRegistry.underlying.getHistograms

  "A histogram" should{

    val histogramName = HistogramName("myHistogram")

    "be created and have a specific measurement" in{
      val value = math.abs(Random.nextLong)

      update(histogramName,value)

      val lastMeasurement = allHistograms.collectFirst{ case(name,hist) if name == histogramName.value => hist }
        .flatMap{ case meter =>
          meter.getSnapshot.getValues.lastOption
        }

      lastMeasurement must beSome(value)
    }

    "have a prefixed name" in{
      update(histogramName,1)

      val internalName = allHistograms.collectFirst{ case(name,_) if name == histogramName.value => name }

      internalName must beSome.which(_.startsWith("histogram"))
    }

  }
}
