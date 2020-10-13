package tutorial.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

object Square {

  case class Props(value: String, onClick: Callback)

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(p: Props) =
      <.button(
        ^.cls := "square",
        ^.onClick --> p.onClick,
        p.value
      )

  }
  val component = ScalaComponent
    .builder[Props]("Square")
    .renderBackend[Backend]
    .build

  def apply(value: Option[String], onClick: Callback) =
    component(Props(value.getOrElse(""), onClick))
}

object Board {

  val lines = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8),
    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8),
    List(0, 4, 8),
    List(2, 4, 6)
  );

  def calculateWinner(state: Vector[Option[String]]) =
    lines
      .map(_.map(state(_)) match {
        case Some(q) :: Some(w) :: Some(e) :: Nil if w == e && q == e =>
          Some(q)
        case _ => None
      })
      .collectFirst { case Some(x) => x }

  case class State(
      hasWon: Option[String],
      nextMove: Option[String],
      vals: Vector[Option[String]]
  )

  class Backend(bs: BackendScope[Unit, (State, List[State])]) {

    val switchNextMove = (winner: Option[String], prev: Option[String]) =>
      winner match {
        case Some(_) => None
        case _       => prev.map(x => if (x == "O") "X" else "O")
      }

    def handleClick(i: Int) =
      bs.modState {
        case (s, list) =>
          s.vals(i) match {
            case None =>
              val newBoard: Vector[Option[String]] =
                s.vals.updated(i, s.nextMove)
              val winner = calculateWinner(newBoard)
              (
                State(
                  winner,
                  switchNextMove(winner, s.nextMove),
                  newBoard
                ),
                s :: list
              )
            case _ => (s, list)
          }
      }

    def render(stateWithHistory: (State, List[State])) = {
      val (s, list) = stateWithHistory
      val status = s.hasWon match {
        case Some(x) => s" $x has Won"
        case _ =>
          s"Next player: ${s.nextMove.getOrElse("")}"
      }
      def renderSquare(i: Int) = Square(s.vals(i), handleClick(i))
      <.div(
        <.div(
          ^.cls := "status",
          status
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(0),
          renderSquare(1),
          renderSquare(2)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(3),
          renderSquare(4),
          renderSquare(5)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(6),
          renderSquare(7),
          renderSquare(8)
        ),
        <.div(
          <.ol(
            (<.li(s.vals.mkString(",")) ::
              (list.map(_.vals) map (x => <.li(x.mkString(","))))): _*
          )
        )
      )
    }

  }

  val component = ScalaComponent
    .builder[Unit]("Board")
    .initialState(
      (State(None, Some("X"), Vector.fill(9)(None)), Nil: List[State])
    )
    .renderBackend[Backend]
    .build

  def apply() = component()
}

object Game {
  val component = ScalaComponent
    .builder[Unit]("Game")
    .renderStatic(
      <.div(
        ^.cls := "game",
        <.div(
          ^.cls := "game-board",
          Board()
        ),
        <.div(
          <.div( /*Status*/ ),
          <.ol( /* TODO */ )
        )
      )
    )
    .build

  def apply() = component()
}

object TutorialApp {
  def main(args: Array[String]): Unit = {
    Game().renderIntoDOM(dom.document.getElementById("root"))
  }

}
