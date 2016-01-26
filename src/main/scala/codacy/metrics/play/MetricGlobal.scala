package codacy.metrics.play

import java.util.concurrent.TimeUnit

import codacy.metrics.cachet._
import codacy.metrics.dropwizard.{Result => _, _}
import play.api.{Configuration, Application}
import play.api.libs.ws.{WS, WSClient}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class CachetConfigKeys(createComponent:CreateComponent,createGroupOpt: Option[CreateGroup])
case class GraphiteConfigKeys(componentName:String,instanceName:String)

class MetricGlobal(cfg: Application => (Option[CachetConfigKeys],Option[GraphiteConfigKeys]), filters: EssentialFilter*) extends WithFilters(filters ++ metricFilters:_*){ self =>
  import MetricConfiguration._

  lazy val metricsRouter = new MetricRouter(DefaultMetricController)

  override def onStart(app:Application): Unit = {
    super.onStart(app)

    val (cachetConfOpt, graphiteConfOpt) = cfg(app)

    //Cachet init
    cachetConfOpt.collect{ case conf if CachetConfiguration.cachet.enabled =>
      import play.api.libs.concurrent.Execution.Implicits._
      implicit val wsClient = WS.client(app)
      implicit val config   = app.configuration
      CachetConnection.register(conf.createComponent,conf.createGroupOpt).onSuccess{ case component =>
        if (CachetConfiguration.cachet.metrics)
          CachetReporter(component).start(1,TimeUnit.MINUTES)
      }
    }

    //Graphite init
    for {
      conf <- graphiteConfOpt
    } yield {
      new GraphiteReporter(conf, app.configuration).start()
    }
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    //if(! excludeRequest(request)) Metrics.mark(requestsName)
    metricsRouter(request).orElse(super[WithFilters].onRouteRequest(request))
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    if(! excludeRequest(request)) mark(failedRequests)
    super[WithFilters].onError(request,ex)
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    if(! excludeRequest(request)) mark(failedRequests)
    super[WithFilters].onBadRequest(request,error)
  }
}

object CachetConnection{
  def register(component: CreateComponent,group:Option[CreateGroup] )(implicit ws: WSClient, executionContext: ExecutionContext, configuration:Configuration): Future[ResponseComponent] = {
    //groupId:
    val groupId = group.map{ case group =>
      Cachet.components.groups.list().flatMap{ case groups =>
        groups.collectFirst{ case g if g.name == group.name =>
          Future.successful(g.id)
        }.getOrElse{
          //we have to create the group
          Cachet.components.groups.create(group).map(_.id)
        }
      }.map( Option(_) )
    }.getOrElse(
      //GroupId 0 means no group... :(
      Future.successful(Option.empty)
    )

    groupId.flatMap{ case groupId =>

      //create update set status
      Cachet.components.list().flatMap{ case components =>
        components.collectFirst{ case cmp if cmp.groupId == groupId && cmp.name == component.name =>
          if (cmp.status == ComponentStatus.Operational) {
            Future.successful(cmp)
          }
          else{
            Cachet.components.update(UpdateComponent(cmp.id, status = Some(ComponentStatus.Operational)))
          }
        }.getOrElse{
          val res = Cachet.components.create(component.copy(groupId = groupId))
          /*res.onComplete{
            case Success(resp) =>
              //println(s"successfully registered component: ${resp.id}")
            case Failure(err) =>
              //println(s"error registering component: ${err.getMessage}")
          }*/
          res
        }
      }
    }
  }
}
