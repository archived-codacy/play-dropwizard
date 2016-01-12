import codacy.CodacySbt
import sbt.Keys._
import sbt._

object Common {

  private val buildVersion = sys.props.getOrElse("build.number", "dev-SNAPSHOT")
  private val appVersion = "1.0." + buildVersion

  val appSettings: Seq[Def.Setting[_]] = Seq(version := appVersion) ++
    CodacySbt.projectSettings ++ CodacySbt.autoImport.privateMvnPublish

}

