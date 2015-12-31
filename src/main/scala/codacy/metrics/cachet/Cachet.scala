package codacy.metrics.cachet

object Cachet{ calls =>

  object components extends Crlud[ComponentCrud]{ cmp =>
    private[this] val path = "/components"

    override def create = Post(_ => path, jsonDataBody[ResponseComponent])
    override def update = Put(a => s"$path/${a.id}", jsonDataBody[ResponseComponent])
    override def remove = Delete(id => s"$path/$id", is204 )
    override def byId   = Get(id => s"$path/$id", jsonDataBody[Option[ResponseComponent]])
    override def list   = Get(_ => path, jsonDataBody[Set[ResponseComponent]])

    object groups extends Crlud[GroupCrud]{
      private[this] val path = s"${cmp.path}/groups"

      override def create = Post( _ => path,jsonDataBody[ResponseGroup])
      override def remove = Delete(id => s"$path/$id", is204)
      override def update = Put(a => s"$path/${a.id}",jsonDataBody[ResponseGroup])
      override def byId   = Get(id => s"$path/$id",jsonDataBody[Option[ResponseGroup]])
      override def list   = Get(_ => path,jsonDataBody[Set[ResponseGroup]])
    }
  }

  object metrics extends Crld[MetricsCrud]{ metrics =>
    private[this] val path = s"/metrics"

    override def create = Post(a => path,jsonDataBody[ResponseMetric])
    override def remove = Delete(id => s"$path/$id",is204)
    override def byId   = Get(id => s"$path/$id",jsonDataBody[Option[ResponseMetric]])
    override def list   = Get(_ => path,jsonDataBody[Set[ResponseMetric]])

    case class points(metricId: MetricId) extends Cld[PointCrud] {
      private[this] val path = s"${metrics.path}/$metricId/points"

      override def create = Post(_ => path,jsonDataBody[ResponsePoint])
      override def remove = Delete(id => s"$path/$id",is204)
      override def list   = Get(_ => path,jsonDataBody[Set[ResponsePoint]])
    }
  }

  object incidents extends Crlud[IncidentCrud]{
    private[this] val path = s"/incidents"

    override def list   = Get(_ => path,jsonDataBody[Set[ResponseIncident]])
    override def create = Post(_ => path,jsonDataBody[ResponseIncident])
    override def update = Put(a => s"$path/${a.id}",jsonDataBody[ResponseIncident])
    override def remove = Delete(id => s"$path/$id", is204)
    override def byId   = Get(id => s"$path/$id",jsonDataBody[Option[ResponseIncident]])
  }
}