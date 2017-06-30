import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
class People(tag: Tag) extends Table[(Int, String, String, Int)](tag, "PEOPLE") {
  def id = column[Int]("PER_ID", O.PrimaryKey, O.AutoInc)
  def fName = column[String]("PER_FNAME")
  def lName = column[String]("PER_LNAME")
  def age = column[Int]("PER_AGE")
  def * = (id, fName, lName, age)
}
