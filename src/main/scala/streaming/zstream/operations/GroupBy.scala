package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._
import java.io.IOException

object GroupBy extends ZIOAppDefault {
  case class Exam(person: String, score: Int)

  val examResults = Seq(
    Exam("Alex", 64),
    Exam("Michael", 97),
    Exam("Bill", 77),
    Exam("John", 78),
    Exam("Bobby", 71)
  )

  val groupByKeyResult: ZStream[Any, Nothing, (Int, Int)] =
    ZStream
      .fromIterable(examResults)
      .groupByKey(exam => exam.score / 10 * 10) { case (k, s) =>
        ZStream.fromZIO(s.runCollect.map(l => k -> l.size))
      }

  val counted: UStream[(Char, Long)] =
    ZStream(
      "Mary",
      "James",
      "Robert",
      "Patricia",
      "John",
      "Jennifer",
      "Rebecca",
      "Peter"
    )
      .groupBy(x => ZIO.succeed((x.head, x))) { case (char, stream) =>
        ZStream.fromZIO(stream.runCount.map(count => char -> count))
      }
  // Input:  Mary, James, Robert, Patricia, John, Jennifer, Rebecca, Peter
  // Output: (P, 2), (R, 2), (M, 1), (J, 3)

  val classifyStudents: ZStream[Any, IOException, (String, Seq[String])] =
    ZStream.fromZIO(
      printLine(
        "Please assign each student to one of the A, B, or C classrooms."
      )
    ) *> ZStream(
      "Mary",
      "James",
      "Robert",
      "Patricia",
      "John",
      "Jennifer",
      "Rebecca",
      "Peter"
    )
      .groupBy(student =>
        printLine(s"What is the classroom of $student? ") *>
          readLine.map(classroom => (classroom, student))
      ) { case (classroom, students) =>
        ZStream.fromZIO(
          students
            .runFold(Seq.empty[String])((s, e) => s :+ e)
            .map(students => classroom -> students)
        )
      }

  // Input:
  // Please assign each student to one of the A, B, or C classrooms.
  // What is the classroom of Mary? A
  // What is the classroom of James? B
  // What is the classroom of Robert? A
  // What is the classroom of Patricia? C
  // What is the classroom of John? B
  // What is the classroom of Jennifer? A
  // What is the classroom of Rebecca? C
  // What is the classroom of Peter? A
  //
  // Output:
  // (B,List(James, John))
  // (A,List(Mary, Robert, Jennifer, Peter))
  // (C,List(Patricia, Rebecca))

  override def run = for {
    _ <- print("groupByKeyResult: ") *> groupByKeyResult
      .runForeachChunk(printLine(_))
    _ <- print("counted: ") *> counted
      .runForeachChunk(printLine(_))
    // this one prompts:
    // _ <- print("classifyStudents: ") *> classifyStudents
    //   .runForeachChunk(printLine(_))
  } yield ()
}
