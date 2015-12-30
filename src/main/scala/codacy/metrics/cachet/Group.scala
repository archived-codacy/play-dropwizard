package codacy.metrics.cachet

sealed trait Group

case class GroupId(value:Int)       extends Identifier[Group]{ override def toString = value.toString }
case class GroupName(value:String)  extends AnyVal
case class GroupOrder(value:Int) extends AnyVal

case class CreateGroup(name:GroupName,order:Option[GroupOrder]=None) extends Group
case class UpdateGroup(id: GroupId, name:Option[GroupName],order:Option[GroupOrder]) extends Group
case class ResponseGroup(id: GroupId, name: GroupName, order:GroupOrder, createdAt: Date,
                         updatedAt: Option[Date]=None) extends Group
