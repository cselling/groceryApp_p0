import java.text.ParseException

object Main extends App { // to use, use the terminal to compile the program and give input
  // p0> scalac src\main\scala\Filename.scala
  // scala Filename

  import scala.io.StdIn
  import java.sql.{Connection, DriverManager}

  val driver = "com.mysql.cj.jdbc.Driver"
  val url = "jdbc:mysql://localhost:3306/grocerySpace"
  val username = "root"
  val password = "!cantSleep89"
  val storeInv = "foodCatalog"
  val shopCart = "shoppingCart"

  var connection: Connection = _



  println("Welcome to Selling Foods, where we sell food!")
  while(true){
    print("What is your desire?:  ")
    // try-catch this  catch "ParseException" from readf()
    var command = ""
    var arguments = ""
    try {
      var line = StdIn.readf("{0} {1}") // separates the cli into two variables, separated by a space
      command = line(0).toString  // describes the operation to perform:  BROWSE, ADD, REMOVE, CART, CHECKOUT
      arguments = line(1).toString // command dependant/everything else
    } catch {
        case x: ParseException =>  // incorrect user input, just do nothing because you CAN do nothing -- match("") goes to default which just cycles and explains proper input
          {}
    }

    command.toUpperCase match{
      case "BROWSE" => {
        browseDB(arguments)
      }
      case "ADD" => {
        addCart(arguments)
      }
      case "REMOVE" => {
        rmvCart(arguments)
      }
      case "CART" => {
        browseCart()
      }
      case "CHECKOUT" => {
        clearCart()
      }
      case default =>println("Describe the operation to perform:  BROWSE, ADD, REMOVE, CART, CHECKOUT")
    }

  }

  def browseDB(expr: String) {
    try {
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"""select itemID, Name, price, weight from $storeInv where Name like "%$expr%" or Department = "\t$expr\t";""")
      while (resultSet.next()) {
        val ID = resultSet.getString("itemID")
        val price = resultSet.getString("price")
        val name = resultSet.getString("Name")
        val weight = resultSet.getString("weight")
        println(f"|\t$ID\t||$name%20s||\t$$$price\t||\t$weight lbs.\t")
      }
      connection.close()
    } catch {
      case e: Exception => {e.printStackTrace()}
    }
  }
  def browseCart() {
    try {
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"""select inv.itemID, inv.Name, inv.price, inv.weight from $shopCart cart join $storeInv inv on cart.itemID = inv.itemID;""")
      while (resultSet.next()) {
        val ID = resultSet.getString("inv.itemID")
        val price = resultSet.getString("inv.price")
        val name = resultSet.getString("inv.Name")
        val weight = resultSet.getString("inv.weight")
        println(f"|\t$ID\t||$name%20s||\t$$$price\t||\t$weight lbs.\t")
      }
      connection.close()
    } catch {
      case e: Exception => {e.printStackTrace()}
    }
  }

  def addCart(expr: String) {
    try {
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      statement.executeUpdate(s"""insert into $shopCart(itemID) value(${expr.toInt});""")
      println(s"added $expr to your cart")
      connection.close()
    } catch {
      case e: Exception => {e.printStackTrace()}
    }
  }

  def rmvCart(item: String) {
    try {
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      statement.executeUpdate(
        s"""delete from $shopCart
           |  where itemID = ${item.toInt}
           |  order by cartID
           |  limit 1
           |;""".stripMargin)
      println(s"removed $item to your cart")
      connection.close()
    } catch {
      case e: Exception => {e.printStackTrace()}
    }
  }

  def clearCart() {
    try {
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(s"""select sum(inv.price) as sum from $shopCart cart join $storeInv inv on cart.itemID = inv.itemID;""")
      var result: Double = 0
      while(resultSet.next()){
        result = resultSet.getDouble("sum")
      }
      println(f"Thank You for shopping with us today!  Your total is:  $$$result%.2f")
      statement.executeUpdate(s"""delete from $shopCart""")
      connection.close()
    } catch {
      case e: Exception => {e.printStackTrace()}
    }
  }

}
