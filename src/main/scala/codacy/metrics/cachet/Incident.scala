package codacy.metrics.cachet

import codacy.metrics.cachet.ComponentStatus.ComponentStatus

sealed trait Incident

case class IncidentId(value:Long)        extends Identifier[Incident]{ override def toString = value.toString }
case class IncidentName(value:String)    extends AnyVal
case class IncidentMessage(value:String) extends AnyVal
case class IncidentVisibility(value:Int) extends AnyVal
object IncidentStatus extends Enumeration{
  type IncidentStatus = Value
  val Scheduled, Investigating, Identified, Watching, Fixed = Value
}

import IncidentStatus._

case class CreateIncident(name: IncidentName, message:IncidentMessage, status:IncidentStatus,
                          visible:IncidentVisibility, componentId:Option[ComponentId],
                          componentStatus: Option[ComponentStatus], notifySubscribers:Boolean) extends Incident

case class UpdateIncident(id:IncidentId, name: Option[IncidentName]=None,
                          message:Option[IncidentMessage]=None, status:Option[IncidentStatus]=None,
                          visible:Option[IncidentVisibility]=None, componentId:Option[ComponentId]=None,
                          notifySubscribers:Option[Boolean]=None) extends Incident

case class ResponseIncident(id:IncidentId, componentId:ComponentId, name: IncidentName,
                            status:IncidentStatus,visible:IncidentVisibility, message:IncidentMessage,
                            scheduledAt:Date, createdAt:Date, updatedAt:Option[Date],
                            deletedAt:Option[Date]) extends Incident