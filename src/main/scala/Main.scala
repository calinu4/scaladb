
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
        (10, "Jack", "Wood", 36,"London","Highfield Avenue",23)
        , (20, "Tim", "Brown", 24,"London","Highfield Avenue",45)
        , (20, "Alex", "Smith",30,"Cambridge","Russell Avenue",2)
        , (20, "Dave", "Mose", 27,"Manchester","Broadway Road",100)
        , (20, "Colin", "Ngoju", 45,"Brighton","Pier Road",5)
        , (20, "Alex", "Poplis", 21,"London","Highfield Avenue",78)
        , (20, "Mark", "Fraser", 28,"Cambridge","Russell Avenue",67)
        , (20, "Luie", "Melen", 39,"Glasgow","Oxford St",134)
        , (20, "Alex", "Nockis", 44,"Oxford","New Hyke",3)
        , (20, "Lea", "Smith", 49,"London","Stanmore Road",23)
        , (20, "Sue", "Collos", 50,"Brighton","Sunset",48)
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
      case Success(s) => println("Most common last name : "+s.last._1);commonCity
      case Failure(_) => println("An error occurred!")
    }
  }

   //Most common city
   def commonCity={

     val commonC=Future {
       val comCity=peopleTable.groupBy(_.city).map{case(city,group)=>(city,group.length)}.sortBy(_._2)
       println(comCity.result.statements.head)
       db.run(comCity.result)
     }
     Await.result(commonC,Duration.Inf).andThen{
       case Success(s) => println("Most common city : "+s.last._1);commonStreet
       case Failure(_) => println("An error occurred!")
     }
   }
  //Most common street with people that have neighbours
  def commonStreet ={

    val commonStreet=Future {
      val comC=peopleTable.groupBy(_.street).map{case(street,group)=>(street,group.length)}.sorted(_._2.reverse)
      println(comC.result.statements.head)
      db.run(comC.result)
    }
    Await.result(commonStreet,Duration.Inf).andThen{
      case Success(s) =>print("Number of people on same street:");s.foreach(r=>print(r.toString()+", "));listPeople
      case Failure(_) => println("An error occurred!")
    }
  }


  def listPeople = {
    val queryFuture = Future {
      db.run(peopleTable.result).map(_.foreach { case (id, fName, lName, age,city,street,hNo) => println(s" $id $fName $lName $age $city $street $hNo") })
    }
    Await.result(queryFuture, Duration.Inf).andThen { case Success(_) => db.close()
    case Failure(error) => println("Listing people failed due to: " + error.getMessage)
    }
  }

dropDB
}
