package il.co.dotcore.myziotest

import zio._
import zio.Console._

object WhenCase extends ZIOAppDefault {

  def minesweeper(level: String) = ZIO.attempt(???)
  def ticTacToe = ZIO.attempt(???)
  def snake(rows: Int, columns: Int) = ZIO.attempt(???)

  def myApp =
    ZIO.whenCaseZIO {
      (Console.print(
        "Please choose one game (minesweeper, snake, tictactoe)? "
      ) *> Console.readLine).orDie
    } {
      case "minesweeper" =>
        Console.print(
          "Please enter the level of the game (easy/hard/medium)?"
        ) *> Console.readLine.flatMap(minesweeper)
      case "snake" =>
        Console.printLine(
          "Please enter the size of the game: "
        ) *> Console.readLine.mapAttempt(_.toInt).flatMap(n => snake(n, n))
      case "tictactoe" => ticTacToe
    }

  override def run = myApp.debug
}
