package codacy.metrics.cachet

sealed trait Component

case class ComponentId(value:Long) extends Identifier[Component]{ override def toString = value.toString }
case class ComponentName(value:String){ assert(value.nonEmpty) }
case class ComponentDescription(value:String) extends AnyVal
case class ComponentLink(value:String)        extends AnyVal
case class ComponentOrder(value:Int)          extends AnyVal

object ComponentStatus extends Enumeration(1){
  type ComponentStatus = Value
  val Operational, PerformanceIssues, PartialOutage, MajorOutage = Value
}

import ComponentStatus._
case class CreateComponent(name: ComponentName,
                           description:Option[ComponentDescription]=None,
                           status: ComponentStatus=Operational,
                           link: Option[ComponentLink]=None,
                           order: Option[ComponentOrder]=None,
                           groupId: Option[GroupId]=None,
                           enabled:Option[Boolean]=None) extends Component

case class UpdateComponent(id:ComponentId,
                           name: Option[ComponentName]=None,
                           status: Option[ComponentStatus]=None,
                           link: Option[ComponentLink]=None,
                           order: Option[ComponentOrder]=None,
                           groupId: Option[GroupId]=None) extends Component

case class ResponseComponent(id: ComponentId,
                             name: ComponentName,
                             description:Option[ComponentDescription],
                             status: ComponentStatus,
                             link: Option[ComponentLink],
                             order: ComponentOrder,
                             groupId: Option[GroupId],
                             createdAt: Date,
                             updatedAt: Option[Date],
                             deletedAt: Option[Date]) extends Component
