package codacy.metrics.cachet

private[cachet] trait Crud{

  trait CrudTypes{
    type Base
    type Id       <: Identifier[Base]
    type Response <: AnyRef{ def id: Identifier[Base] }
    type Create   <: Base
    type Update   <: Base with AnyRef{ def id: Identifier[Base] }
  }

  trait ListCall[A <: CrudTypes]{
    def list: Request[Unit,Set[A#Response]]
  }

  trait ByIdCall[A <: CrudTypes]{
    def byId: Request[A#Id,Option[A#Response]]
  }

  trait CreateCall[A <: CrudTypes]{
    def create: Request[A#Create,A#Response]
  }

  trait UpdateCall[A <: CrudTypes]{
    def update: Request[A#Update,A#Response]
  }

  trait RemoveCall[A <: CrudTypes]{
    def remove:Request[A#Id,Boolean]
  }

  /*some combinations*/
  trait Crlud[A <: CrudTypes] extends UpdateCall[A] with ListCall[A] with ByIdCall[A] with RemoveCall[A] with CreateCall[A]
  trait Crld[A <: CrudTypes]  extends ListCall[A] with ByIdCall[A] with RemoveCall[A] with CreateCall[A]
  trait Cld[A <: CrudTypes]   extends ListCall[A] with RemoveCall[A] with CreateCall[A]
}
