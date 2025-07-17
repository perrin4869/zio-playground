package il.co.dotcore.zio.playground

import zio._
import zio.Console._
import java.io.IOException

object GetNames extends ZIOAppDefault {

  def getNames: ZIO[Any, IOException, List[String]] =
    Console.print("Please enter all names") *>
      Console.printLine(" (enter \"exit\" to indicate end of the list):") *>
      ZIO
        .iterate((List.empty[String], true))(_._2) { case (names, _) =>
          Console.print(s"${names.length + 1}. ") *>
            Console.readLine.map {
              case "exit" => (names, false)
              case name   => (names :+ name, true)
            }
        }
        .map(_._1)
        .debug

  override def run = getNames
}
