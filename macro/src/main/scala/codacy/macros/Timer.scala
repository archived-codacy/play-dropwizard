package codacy.macros

import language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox._

class timed extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro timedMacro.impl
}

object timedMacro {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def modifiedDeclaration(defdef:DefDef) = {
      val name = c.internal.enclosingOwner.fullName

      val mod = defdef match{
        case q"$mods def $tname[..$tparams](...$paramss): $tpt = $expr" =>
          val fullName = s"$name.$tname"
          val newExpr = q"""{
            import codacy.metrics.dropwizard.{timed,TimerName}
            timed(TimerName($fullName)){ $expr }
          }"""
          q"$mods def $tname[..$tparams](...$paramss): $tpt = $newExpr"
      }

      c.Expr(q"""$mod""")
    }

    annottees.map(_.tree) match {
      case (defdef: DefDef) :: Nil =>
        modifiedDeclaration(defdef)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee, only defs supported")
    }
  }
}

class timedAsync(param:String = "") extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro timedAsync.impl
}

object timedAsync{

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def modifiedDeclaration(defdef:DefDef) = {
      val name = c.internal.enclosingOwner.fullName

      val mod = defdef match{
        case q"$mods def $tname[..$tparams](...$paramss): $tpt = $expr" =>
          val fullName = s"$name.$tname"
          val newExpr = q"""{
            import codacy.metrics.dropwizard.{timedAsync,TimerName}
            timedAsync(TimerName($fullName)){ $expr }
          }"""
          q"$mods def $tname[..$tparams](...$paramss): $tpt = $newExpr"
      }

      c.Expr(q"""$mod""")
    }

    annottees.map(_.tree) match {
      case (defdef: DefDef) :: Nil =>
        modifiedDeclaration(defdef)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee, only defs supported")
    }
  }
}
