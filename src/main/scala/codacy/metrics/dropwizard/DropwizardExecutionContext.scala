package codacy.metrics.dropwizard

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object DropwizardExecutionContext{ self =>

  object Implicits{
    implicit val default: ExecutionContextExecutor = self.default
  }

  val default: ExecutionContextExecutor =  ExecutionContext.fromExecutor( Executors.newCachedThreadPool() )
}