package codacy.metrics.cachet

private[cachet] trait Cruds{

  sealed trait ComponentCrud extends CrudTypes{
    type Base      = Component
    type Id        = ComponentId
    type Response  = ResponseComponent
    type Create    = CreateComponent
    type Update    = UpdateComponent
  }

  sealed trait GroupCrud extends CrudTypes{
    type Base      = Group
    type Id        = GroupId
    type Response  = ResponseGroup
    type Create    = CreateGroup
    type Update    = UpdateGroup
  }

  sealed trait MetricsCrud extends CrudTypes{
    type Base      = Metric
    type Id        = MetricId
    type Response  = ResponseMetric
    type Create    = CreateMetric
    type Update    = Nothing
  }

  sealed trait PointCrud extends CrudTypes{
    type Base      = Point
    type Id        = PointId
    type Response  = ResponsePoint
    type Create    = CreatePoint
    type Update    = Nothing
  }

  sealed trait IncidentCrud extends CrudTypes{
    type Base      = Incident
    type Id        = IncidentId
    type Response  = ResponseIncident
    type Create    = CreateIncident
    type Update    = UpdateIncident
  }
}