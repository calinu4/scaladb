import java.sql.{Connection, DriverManager, SQLException}
import java.util


import com.mysql._

import scala.collection.mutable.ListBuffer
object Hangman {
  var connection: Connection = null
  var words: ListBuffer[String] = new ListBuffer[String]()
  var selectedWord:String=""
  var gameOver:Boolean=false
  var won:Boolean=false
  var guesses:ListBuffer[Char]=new ListBuffer[Char]()
  var count:Int=0
  var dashedWord:ListBuffer[Char]=new ListBuffer[Char]()

  def main(args: Array[String]): Unit = {
    Hangman.connectToDatabase()
    Hangman.retrieveData("words")
    Hangman.chooseWord()
    Hangman.displayWord()

  }

  //Display dashed word
  def displayWord()={
    dashedWord.foreach(c=>print(c+" "))
    println(selectedWord)
    println(words.length)
  }
  //Word selector by computer
  def chooseWord()={
    val number=scala.util.Random.nextInt(words.length-1)
    selectedWord=words(number)
    for(i<-0 until selectedWord.length)
      dashedWord+='_'

  }
  //Connect to database
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
      println("Connection successful")
    } catch {
      case e:Exception => e.printStackTrace
    }
    //connection.close()
  }

  //Retrieve words from database
  def retrieveData(table:String)={
    try {
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"SELECT * FROM $table")
      while (resultSet.next()) {
        words +=resultSet.getString("name")
      }
      println("Words retrieved from database")
    }
    catch{
      case e:Exception=>e.printStackTrace()
    }
  }


}
