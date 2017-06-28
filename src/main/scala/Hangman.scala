import java.sql.{Connection, DriverManager, SQLException}
import java.util
import java.util.Scanner

import com.mysql._

import scala.collection.mutable.ListBuffer

object Hangman {
  var connection: Connection = null
  var words: ListBuffer[String] = new ListBuffer[String]()
  var selectedWord: String = ""
  var gameOver: Boolean = false
  var won: Boolean = false
  var guesses: ListBuffer[Char] = new ListBuffer[Char]()
  var count: Int = 0
  var dashedWord: ListBuffer[Char] = new ListBuffer[Char]()

  def main(args: Array[String]): Unit = {
    Hangman.connectToDatabase()
    Hangman.retrieveData("words")
    Hangman.chooseWord()
    //println(selectedWord)
    Hangman.displayWord()
    Hangman.readInput()


  }
  def drawHangman()={
    count match{
      case 0=> print(" _________     \n");print("|         |    \n")
      case 1=> print(" _________     \n");print("|         |    \n");print("|         0    \n")
      case 2=> print(" _________     \n");print("|         |    \n");print("|         0    \n");print("|        / \n")
      case 3=> print(" _________     \n");print("|         |    \n");print("|         0    \n");print("|        /|\\ \n")
      case 4=> print(" _________     \n");print("|         |    \n");print("|         0    \n");print("|        /|\\ \n");print("|        /  \n")
      case _=> print(" _________     \n");print("|         |    \n");print("|         0    \n");print("|        /|\\ \n");print("|        / \\ \n")
    }
  }
  def readInput() = {
    val wordChars=selectedWord.toCharArray
    var sc = new Scanner(System.in)
    while (checkGameOver() == false&&won==false) {
      val c= sc.next().toCharArray.head
      guesses+=c
      if(checkGuess(c)){
        for(i<-0 until wordChars.length-1)
          if(wordChars(i)==c)
            dashedWord(i)=c
      }else {count+=1;drawHangman()}
      won=checkWin()
      displayWord()
    }
    checkWin()
    if(won)
      println("User won")
    else
      println("Game Over")
  }

  def checkGuess(c:Char)={
    if(selectedWord.contains(c))
      true
    else
      false

  }
  //Display dashed word
  def displayWord() = {
    dashedWord.foreach(c => print(c + " "))
    println()
    //println("Guesses: " + guesses.foreach(c => print(c + ", ")))
    // println(selectedWord)
    //println(words.length)
  }

  def checkWin() = {
    val w=dashedWord.mkString.trim()
    if (w.contains('_')==false)
      true
    else
      false

  }

  def checkGameOver() = {
    if (count > 4)
      gameOver = true
    else
      false
  }

  //Word selector by computer
  def chooseWord() = {
    val number = scala.util.Random.nextInt(words.length - 1)
    selectedWord = words(number)
    for (i <- 0 until selectedWord.length-1)
      dashedWord += '_'

  }

  //Connect to database
  def connectToDatabase() = {
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
      case e: Exception => e.printStackTrace
    }
    //connection.close()
  }

  //Retrieve words from database
  def retrieveData(table: String) = {
    try {
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"SELECT * FROM $table")
      while (resultSet.next()) {
        words += resultSet.getString("name")
      }
      println("Words retrieved from database")
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
  }


}
