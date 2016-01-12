package codacy.macros

import language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox._

class enriched extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro enrichedMacro.impl
}

object enrichedMacro{

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def extractClassNameAndFieldsAndDecl(classDecl: ClassDef) = {
      classDecl match{
        case q"$mods class $tpname[..$tparams] $ctorMods(..$params) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" if params.length == 1 =>
          val q"$_ val $name: $_ = $_" = params.head

          //val par = if(parents.isEmpty) Seq(q"AnyVal") else parents
          val newClass =
            q"""$mods class $tpname[..$tparams] $ctorMods(..$params) extends { ..$earlydefns } with ..$parents { $self =>
               ..$stats
               override def toString() = ${name}.toString
            }"""
          (tpname, params,newClass)

        case t@q"$mods class $tpname[..$tparams] $ctorMods(..$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>
          (tpname, paramss,t)
        case _ =>
          c.abort(c.enclosingPosition, "Annotation is not supported here")
      }
    }

    def modifiedCompanion(compDeclOpt: Option[ModuleDef], format: ValDef, className: TypeName) = {
      compDeclOpt map { compDecl =>
        // Add the formatter to the existing companion object
        val q"object $obj extends ..$bases { ..$body }" = compDecl
        q"""
          object $obj extends ..$bases {
            ..$body
            $format
          }
        """
      } getOrElse {
        // Create a companion object with the formatter
        q"object ${className.toTermName} { $format }"
      }
    }

    def jsonFormatter(className: TypeName, fields: List[ValDef]) = {
      fields.length match {
        case 0 =>
          c.abort(c.enclosingPosition, "Cannot create json formatter for case class with no fields")
        case 1 =>
          q"""
            implicit val jsonAnnotationFormat = {
              import play.api.libs.json._
              Format(
                __.read[${fields.head.tpt}].map(s => new ${className.toTypeName}(s)),
                new Writes[$className] { def writes(o: $className) = Json.toJson(o.${fields.head.name}) }
              )
            }
          """
        case _ =>
          q"""implicit val jsonAnnotationFormat = {
             import play.api.libs.json._
             import codacy.util._

             val fmt = Json.format[$className]
             OFormat[$className](
               fmt,
               fmt.asOWrites
             )
          }"""
      }
    }

    def modifiedDeclaration(classDecl: ClassDef, compDeclOpt: Option[ModuleDef] = None) = {
      val (className, fields,cDecl) = extractClassNameAndFieldsAndDecl(classDecl)
      val format = jsonFormatter(className, fields)
      val compDecl = modifiedCompanion(compDeclOpt, format, className)

      // Return both the class and companion object declarations
      c.Expr(q"""
        $cDecl
        $compDecl
      """)
    }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case (classDecl: ClassDef) :: (compDecl: ModuleDef) :: Nil => modifiedDeclaration(classDecl, Some(compDecl))
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
