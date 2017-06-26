import java.sql.{Connection, DriverManager, SQLException}
import java.util

import com.mysql._

import scala.collection.mutable.ListBuffer

object ScalaConnect {
  var cities: ListBuffer[City] = new ListBuffer[City]()
  //Keep a reference to current connection to a database
  var connection: Connection = null

  def main(args:Array[String]) {
    ScalaConnect.connectToDatabase()
    ScalaConnect.createTable()
    ScalaConnect.retrieveData("city")
    println("Reading data from World Database")
    ScalaConnect.cities.foreach(e => println(e))
    //ScalaConnect.setData()
    //ScalaConnect.insertIntoTable(6,"Bucharest","BCR","South Romania",3000000)

  }

  def connectToDatabase()={
    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/world"
    val username = "root"
    val password = "password"
    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
    } catch {
      case e:Exception => e.printStackTrace
    }
    //connection.close()
  }
  def retrieveData(table:String)={
    try {
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"SELECT * FROM $table limit 10")
      while (resultSet.next()) {
        val id = resultSet.getInt("ID")
        val name = resultSet.getString("Name")
        val code = resultSet.getString("CountryCode")
        val district = resultSet.getString("District")
        val population = resultSet.getInt("Population")
        cities += new City(id, name, code, district, population)
      }
      println("Data retrieved from database")
    }
    catch{
      case e:Exception=>e.printStackTrace()
    }
  }

  def setInitialData()={
    try {
      val statement = connection.createStatement()
      for (elem <- cities) {
        val id = elem.id
        val name = elem.name
        val code = elem.countryCode
        val district = elem.district
        val population = elem.population
       statement.executeUpdate("insert into town VALUES('"+id+ "','"+name+"','"+code+"','"+district+"','"+population+"')")
        println("Initial data saved into database")
      }
    }
    catch{
      case e:Exception=>e.printStackTrace()
    }
  }
  def insertIntoTable(id:Int,name:String,code:String,district:String,population:Int)={
    try{
      val statement=connection.createStatement()
      statement.executeUpdate("insert into town VALUES('"+id+ "','"+name+"','"+code+"','"+district+"','"+population+"')")
      println("Entry added into table town")
    }catch{
      case e:Exception=>e.printStackTrace()
    }
  }

  def createTable()={
    try {
      val statement=connection.createStatement()
      val sql="create table if not exists town(ID int(11) primary key,Name varchar(35) not null,CountryCode varchar(3) not null,District varchar(20) not null,Population int(11) not null)"
      statement.executeUpdate(sql)
      println("Created table town in given database")
    }catch{
      case sq:SQLException=>println("SQL Create Table Exception")
      case e:Exception=>println(e.printStackTrace())
    }
  }
}
