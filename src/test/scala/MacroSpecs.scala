package codacy.metrics


import com.codahale.metrics.MetricRegistry
import org.specs2.mutable.Specification
import codacy.macros._
import scala.collection.JavaConversions._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class MacroSpecs extends Specification{

  def registry:MetricRegistry = dropwizard.MetricRegistry
  def allTimers = registry.getTimers

  @timed def myTimedMethod = 1
  @timedAsync def myAsyncTimedMethod = Future.successful(1)

  @timed("customDef") def myOtherTimedMethod = 1
  @timedAsync("customAsyncDef") def myOtherAsyncTimedMethod = Future.successful(1)

  "A timer" should{
    "be created" in{
      val myResult = myTimedMethod

      val timer = allTimers.find{ case (name,_) => name == "timer.codacy.metrics.MacroSpecs.myTimedMethod"}

      timer must beSome
    }

    "be created for async defs" in{
      val myResult = Await.result(myAsyncTimedMethod,1.second)

      val timer = allTimers.find{ case (name,_) => name == "timer.codacy.metrics.MacroSpecs.myAsyncTimedMethod"}

      timer must beSome
    }

    "be created with a custom name" in{
      val myResult = myOtherTimedMethod

      val timer = allTimers.find{ case (name,_) => name == "timer.codacy.metrics.MacroSpecs.customDef"}

      timer must beSome
    }

    "be created for async defs" in{
      val myResult = Await.result(myOtherAsyncTimedMethod,1.second)

      val timer = allTimers.find{ case (name,_) => name == "timer.codacy.metrics.MacroSpecs.customAsyncDef"}

      timer must beSome
    }
  }
}
