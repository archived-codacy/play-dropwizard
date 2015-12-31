package codacy.metrics.cachet

import codacy.macros.enriched

sealed trait Group

@enriched case class GroupId(value:Int)      extends Identifier[Group]
@enriched case class GroupName(value:String) extends AnyVal
@enriched case class GroupOrder(value:Int)   extends AnyVal

case class CreateGroup(name:GroupName,order:Option[GroupOrder]=None) extends Group
case class UpdateGroup(id: GroupId, name:Option[GroupName],order:Option[GroupOrder]) extends Group
case class ResponseGroup(id: GroupId, name: GroupName, order:GroupOrder, createdAt: Date,
                         updatedAt: Option[Date]=None) extends Group
