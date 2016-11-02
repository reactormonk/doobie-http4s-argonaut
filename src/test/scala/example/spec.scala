import doobie.imports._
import scalaz._, Scalaz._
import scalaz.concurrent.Task

import doobie.contrib.specs2.analysisspec.AnalysisSpec
import org.specs2.mutable.Specification

object ExampleSpec extends Specification with AnalysisSpec {
  val transactor = controllers.Application.xa

  check(models.User.findAll)
  check(models.User.findQuery(2)) // needs a bogus value
}
