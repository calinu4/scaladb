import java.sql.{Connection, DriverManager, SQLException}
import java.util
import java.util.Scanner

import com.mysql._

import scala.collection.mutable.ListBuffer

object Hangman {
  var connection: Connection = null
  var words1: ListBuffer[String] = new ListBuffer[String]()
  var words2: ListBuffer[String] = new ListBuffer[String]()
  var words3: ListBuffer[String] = new ListBuffer[String]()
  var selectedWord: String = ""
  var gameOver: Boolean = false
  var won: Boolean = false
  var guesses: ListBuffer[Char] = new ListBuffer[Char]()
  var count: Int = 0
  var dashedWord: ListBuffer[Char] = new ListBuffer[Char]()

  def main(args: Array[String]): Unit = {
    Hangman.connectToDatabase()
    Hangman.retrieveData("words")
    Hangman.mainMenu()
    println("Selected word was: "+selectedWord)
  }
  def mainMenu()={
    print("Type a number to start game: \n"+
      "1.EASY MODE \n"+"2.MEDIUM MODE\n"+"3.HARD MODE\n"+"4.QUIT Game")
    var sc = new Scanner(System.in)
    var notChosen=true
     while(notChosen){
       val option=sc.nextInt()
       option match{
         case 1=>notChosen=false;chooseWord(1)
         case 2=>notChosen=false;chooseWord(2)
         case 3=>notChosen=false;chooseWord(3)
         case 4=>System.exit(0)
         case _=>println("Invalid Input. Try again!")
       }
     }
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
      println("YOU WON!")
    else
      println("Life is cruel. You are dead. CRUCIFIXION killed you.")
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
    println("Guesses: " + guesses.mkString)
    println("Remaining incorrect guesses: "+(5-count))
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
  def chooseWord(choice:Int) = {
    choice match{
      case 1=> val number = scala.util.Random.nextInt(words1.length - 1);selectedWord = words1(number)
      case 2=>val number = scala.util.Random.nextInt(words2.length - 1);selectedWord = words2(number)
      case 3=>val number = scala.util.Random.nextInt(words3.length - 1);selectedWord = words3(number)
    }
    for (i <- 0 until selectedWord.length-1)
      dashedWord += '_'
    displayWord()
    readInput()

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
        val res=resultSet.getString("name")
        if(res.length>=8)
          words1+=res
        else
          if(res.length<=7&&res.length>=5)
          words2+=res
        else
          words3+=res

      }
      println("Words retrieved from database")
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
  }


}
