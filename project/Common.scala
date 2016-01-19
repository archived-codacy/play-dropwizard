import codacy.CodacySbt
import sbt.Keys._
import sbt._

object Common {

  val appSettings: Seq[Def.Setting[_]] =
    CodacySbt.projectSettings

}

