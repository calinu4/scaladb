
import slick.driver.MySQLDriver.api._

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

object Main extends App{
  // The config string refers to mysqlDB that we defined in application.confval
  val db = Database.forConfig("mysqlDB")
  val peopleTable = TableQuery[People]
  val dropPeopleCmd = DBIO.seq(peopleTable.schema.drop)
  val initPeopleCmd = DBIO.seq(peopleTable.schema.create)



  def dropDB = {
    val dropFuture = Future {
      db.run(dropPeopleCmd)
    }
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => initialisePeople
      case Failure(error) => println("Dropping the table failed due to: " + error.getMessage)
        initialisePeople
    }
  }

  def initialisePeople = {
    val setupFuture = Future {
      db.run(initPeopleCmd)
    }
    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(error) => println("Initialising the table failed due to: " + error.getMessage)
    }
  }

  def runQuery = {
    val insertPeople = Future {
      val query = peopleTable ++= Seq(
        (10, "Jack", "Wood", 36)
        , (20, "Tim", "Brown", 24)
        , (20, "Alex", "Smith", 30)
        , (20, "Dave", "Mose", 27)
        , (20, "Colin", "Ngoju", 45)
        , (20, "Alex", "Poplis", 21)
        , (20, "Mark", "Fraser", 28)
        , (20, "Luie", "Melen", 39)
        , (20, "Alex", "Nockis", 44)
        , (20, "Lea", "Smith", 49)
        , (20, "Sue", "Collos", 50)

      )
      println(query.statements.head)
      db.run(query)
    }
    Await.result(insertPeople, Duration.Inf).andThen {
      case Success(_) => updateRow
      case Failure(error) => println("Welp! Something went wrong! " + error.getMessage)
    }
  }

  //Update
  def updateRow={
    // wrap this in a Try because this may fail for all sorts of reasons
    val updatePeople=Future {
      val updateQuery = peopleTable.filter(_.id === 1).map(c => (c.fName, c.age)).update(("John", 34))
      println(updateQuery.statements.head)
      db.run(updateQuery)
    }
    Await.result(updatePeople,Duration.Inf).andThen {
      case Success(_) => deleteRow
      case Failure(_) => println("An error occurred!")
    }
  }
//Delete
  def deleteRow={
    val idToDelete = 2
    val deleteRow=Future{
      val deleteRow=peopleTable.filter(_.id===idToDelete).delete
      println(deleteRow.statements.head)
      db.run(deleteRow)
    }
    Await.result(deleteRow,Duration.Inf).andThen{
      case Success(_)=>totalPeople
      case Failure(_)=>println("An error occured")
    }
  }

  //Total people in the table
  def totalPeople={

    val totPeople=Future {
      val total=peopleTable.length
      println(total.result.statements.head)
      db.run(total.result)
   }
    Await.result(totPeople,Duration.Inf).andThen{
      case Success(s) => println("Total Number of people is: "+s);averageAge
      case Failure(_) => println("An error occurred!")
    }
  }
  //Average age
  def averageAge={

    val avgPeople=Future {
      val ages=peopleTable.map(_.age).avg
      println(ages.result.statements.head)
      db.run(ages.result)
    }
    Await.result(avgPeople,Duration.Inf).andThen{
      case Success(s) => println("Average age : "+s.get);commonFName
      case Failure(_) => println("An error occurred!")
    }
  }

  //Common first name
  def commonFName={

    val common=Future {
      val comName=peopleTable.groupBy(_.fName).map{case(fName,group)=>(fName,group.length)}.sortBy(_._2)
      println(comName.result.statements.head)
      db.run(comName.result)
    }
    Await.result(common,Duration.Inf).andThen{
      case Success(s) => println("Most common name : "+s.last._1);commonLName
      case Failure(_) => println("An error occurred!")
    }
  }

  //Common last name
  def commonLName={

    val common=Future {
      val comName=peopleTable.groupBy(_.lName).map{case(lName,group)=>(lName,group.length)}.sortBy(_._2)
      println(comName.result.statements.head)
      db.run(comName.result)
    }
    Await.result(common,Duration.Inf).andThen{
      case Success(s) => println("Most common last name : "+s.last._1);listPeople
      case Failure(_) => println("An error occurred!")
    }
  }

   //


  def listPeople = {
    val queryFuture = Future {
      db.run(peopleTable.result).map(_.foreach { case (id, fName, lName, age) => println(s" $id $fName $lName $age") })
    }
    Await.result(queryFuture, Duration.Inf).andThen { case Success(_) => db.close()
    case Failure(error) => println("Listing people failed due to: " + error.getMessage)
    }
  }

dropDB
}
